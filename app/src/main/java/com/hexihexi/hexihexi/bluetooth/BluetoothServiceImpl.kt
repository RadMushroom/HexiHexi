package com.hexihexi.hexihexi.bluetooth

import android.bluetooth.*
import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import com.hexihexi.hexihexi.Mode
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.*
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.LinkedBlockingDeque

class BluetoothServiceImpl(private val context: Context): HexiBluetoothService {

    private lateinit var device: BluetoothDevice
    private lateinit var bluetoothServiceCallback: BluetoothServiceCallback
    private var alertIn: BluetoothGattCharacteristic? = null
    private var isConnected: Boolean = false
    private var shouldUpdateTime: Boolean = false
    private var bluetoothGatt: BluetoothGatt? = null
    private val readableCharacteristics: MutableMap<String, BluetoothGattCharacteristic> = mutableMapOf()
    private val notificationsQueue = LinkedBlockingDeque<ByteArray>()
    private val readingQueue = ArrayBlockingQueue<String>(20)
    private var manufacturerInfo = ManufacturerInfo()
    private var mode: Mode? = null


    companion object {
        private val WRITE_NOTIFICATION: Byte = 1
        private const val WRITE_TIME: Byte = 3
    }

    override fun setUpWithDevice(device: BluetoothDevice, bluetoothServiceCallback: BluetoothServiceCallback) {
        this.device = device
        this.bluetoothServiceCallback = bluetoothServiceCallback
        createGATT()
    }

    private fun createGATT() {
        bluetoothGatt = device.connectGatt(context, true, object : BluetoothGattCallback() {
            override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
                isConnected = BluetoothProfile.STATE_CONNECTED == newState
                if (isConnected) {
                    Log.i(TAG, "GATT connected.")
                    gatt.discoverServices()
                } else {
                    Log.i(TAG, "GATT disconnected.")
                    gatt.connect()
                }
                bluetoothServiceCallback.onConnectionStateChanged(isConnected)
            }

            override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
                Log.i(TAG, "Services discovered.")
                if (status == BluetoothGatt.GATT_INSUFFICIENT_AUTHENTICATION) {
                    handleAuthenticationError(gatt)
                    return
                }

                discoverCharacteristics(gatt)
            }

            override fun onCharacteristicWrite(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic, status: Int) {
                Log.i(TAG, "Characteristic written: " + status)

                if (status == BluetoothGatt.GATT_INSUFFICIENT_AUTHENTICATION) {
                    handleAuthenticationError(gatt)
                    return
                }

                val command = characteristic.value[0]
                when (command) {
                    WRITE_TIME -> {
                        Log.i(TAG, "Time written.")

                        val batteryCharacteristic = readableCharacteristics[Characteristic.BATTERY.uuid]
                        batteryCharacteristic?.let {
                            gatt.setCharacteristicNotification(batteryCharacteristic, true)
                            for (descriptor in batteryCharacteristic.descriptors) {
                                if (descriptor.uuid.toString().startsWith("00002904")) {
                                    descriptor.value = BluetoothGattDescriptor.ENABLE_INDICATION_VALUE
                                    gatt.writeDescriptor(descriptor)
                                }
                            }
                        }
                    }
                    WRITE_NOTIFICATION -> {
                        Log.i(TAG, "Notification sent.")
                        if (notificationsQueue.isEmpty()) {
                            Log.i(TAG, "Reading characteristics...")
                            readNextCharacteristics(gatt)
                        } else {
                            Log.i(TAG, "writing next notification...")
                            alertIn?.value = notificationsQueue.poll()
                            gatt.writeCharacteristic(alertIn)
                        }
                    }
                    else -> Log.w(TAG, "No such ALERT IN command: " + command)
                }
            }

            override fun onDescriptorWrite(gatt: BluetoothGatt, descriptor: BluetoothGattDescriptor, status: Int) {
                readCharacteristic(gatt, Characteristic.MANUFACTURER)
            }

            override fun onCharacteristicRead(gatt: BluetoothGatt, gattCharacteristic: BluetoothGattCharacteristic, status: Int) {
                if (status == BluetoothGatt.GATT_INSUFFICIENT_AUTHENTICATION) {
                    handleAuthenticationError(gatt)
                    return
                }

                val characteristicUuid = gattCharacteristic.uuid.toString()
                val characteristic = Characteristic.byUuid(characteristicUuid)
                when (characteristic) {
                    Characteristic.MANUFACTURER -> {
                        manufacturerInfo.manufacturer = gattCharacteristic.getStringValue(0)
                        readCharacteristic(gatt, Characteristic.FW_REVISION)
                    }
                    Characteristic.FW_REVISION -> {
                        manufacturerInfo.firmwareRevision = gattCharacteristic.getStringValue(0)
                        readCharacteristic(gatt, Characteristic.MODE)
                    }
                    else -> {
                        characteristic?.let {
                            Log.v(TAG, "Characteristic read: " + characteristic.name)
                            if (characteristic === Characteristic.MODE) {
                                val newMode = Mode.bySymbol(gattCharacteristic.value[0].toInt())
                                if (mode !== newMode) {
                                    onModeChanged(newMode)
                                }
                            } else {
                                onBluetoothDataReceived(characteristic, gattCharacteristic.value)
                            }

                            if (shouldUpdateTime) {
                                updateTime()
                            }

                            if (notificationsQueue.isEmpty()) {
                                readNextCharacteristics(gatt)
                            } else {
                                alertIn?.value = notificationsQueue.poll()
                                gatt.writeCharacteristic(alertIn)
                            }
                        }
                    }
                }
            }

            override fun onCharacteristicChanged(gatt: BluetoothGatt, gattCharacteristic: BluetoothGattCharacteristic) {
                val characteristicUuid = gattCharacteristic.uuid.toString()
                val characteristic = Characteristic.byUuid(characteristicUuid)
                Log.d(TAG, "Characteristic changed: " + characteristic)

                if (characteristic === Characteristic.BATTERY) {
                    onBluetoothDataReceived(Characteristic.BATTERY, gattCharacteristic.value)
                }
            }
        })
    }

    private fun discoverCharacteristics(gatt: BluetoothGatt) {
        if (gatt.services.size == 0) {
            Log.i(TAG, "No services found.")
        }

        for (gattService in gatt.services) {
            storeCharacteristicsFromService(gattService)
        }

//        sendBroadcast(Intent(SERVICES_AVAILABLE))
    }

    private fun storeCharacteristicsFromService(gattService: BluetoothGattService) {
        for (gattCharacteristic in gattService.characteristics) {
            val characteristicUuid = gattCharacteristic.uuid.toString()
            val characteristic = Characteristic.byUuid(characteristicUuid)

            when {
                characteristic === Characteristic.ALERT_IN -> {
                    Log.d(TAG, "ALERT_IN DISCOVERED")
                    alertIn = gattCharacteristic
                    setTime()
                    updateTime()
                }
                characteristic != null -> {
                    Log.v(TAG, "${characteristic.type} : ${characteristic.name}")
                    readableCharacteristics.put(characteristicUuid, gattCharacteristic)
                }
                else -> Log.v(TAG, "UNKNOWN: $characteristicUuid")
            }
        }
    }


    private fun setTime() {
        Log.d(TAG, "Setting time...")
        if (!isConnected || alertIn == null) {
            Log.w(TAG, "Time not set.")
            return
        }

        shouldUpdateTime = true
    }

    private fun updateTime() {
        shouldUpdateTime = false

        val time = ByteArray(20)
        val currentTime = System.currentTimeMillis()
        val currentTimeWithTimeZoneOffset = (currentTime + TimeZone.getDefault().getOffset(currentTime)) / 1000

        val buffer = ByteBuffer.allocate(8)
        buffer.order(ByteOrder.LITTLE_ENDIAN).asLongBuffer().put(currentTimeWithTimeZoneOffset)
        val utcBytes = buffer.array()

        val length: Byte = 0x04

        time[0] = WRITE_TIME
        time[1] = length
        time[2] = utcBytes[0]
        time[3] = utcBytes[1]
        time[4] = utcBytes[2]
        time[5] = utcBytes[3]

        alertIn?.value = time
        alertIn?.writeType = BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
        bluetoothGatt?.writeCharacteristic(alertIn)
        showToast("Time was set")
    }

    private fun readNextCharacteristics(gatt: BluetoothGatt) {
        val characteristicUuid = readingQueue.poll()
        readingQueue.add(characteristicUuid)
        readCharacteristic(gatt, Characteristic.valueOf(characteristicUuid))
    }

    private fun readCharacteristic(gatt: BluetoothGatt, characteristic: Characteristic) {
        if (!isConnected) {
            return
        }

        val gattCharacteristic = readableCharacteristics[characteristic.uuid]
        if (gattCharacteristic != null) {
            gatt.readCharacteristic(gattCharacteristic)
        }
    }

    private fun onModeChanged(newMode: Mode) {
        Log.i(TAG, "Mode changed. New mode is: " + mode)
        mode = newMode

        setReadingQueue()

//        val modeChanged = Intent(MODE_CHANGED)
//        modeChanged.putExtra(MODE, newMode)
//        LocalBroadcastManager.getInstance(this).sendBroadcast(modeChanged)
    }

    private fun setReadingQueue() {
        readingQueue.clear()
        readingQueue.add(Characteristic.MODE.name)
        val enabledPreferences = listOf(Characteristic.ACCELERATION.name,
                Characteristic.GYRO.name,
                Characteristic.MAGNET.name,
                Characteristic.LIGHT.name,
                Characteristic.TEMPERATURE.name,
                Characteristic.HUMIDITY.name,
                Characteristic.PRESSURE.name,
                Characteristic.BATTERY.name,
                Characteristic.HEART_RATE.name,
                Characteristic.STEPS.name,
                Characteristic.CALORIES.name)
        mode?.let { _mode ->
            enabledPreferences.filterTo(readingQueue) { _mode.hasCharacteristic(it) }
        }
    }

    private fun onBluetoothDataReceived(type: Characteristic, data: ByteArray) {
        bluetoothServiceCallback.onNewDataAvailable(type, DataConverter.parseBluetoothData(type, data))
    }


    private fun handleAuthenticationError(gatt: BluetoothGatt) {
        gatt.close()
//        sendBroadcast(Intent(BluetoothService.ACTION_NEEDS_BOND))
//        gatt.device.createBond()
    }

    private fun showToast(message: String) {
//        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}
