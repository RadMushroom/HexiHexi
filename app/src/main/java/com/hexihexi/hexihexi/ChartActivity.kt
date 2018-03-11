package com.hexihexi.hexihexi

import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.hexihexi.hexihexi.bluetooth.BluetoothServiceCallback
import com.hexihexi.hexihexi.bluetooth.Characteristic
import com.hexihexi.hexihexi.bluetooth.HexiBluetoothService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.activity_chart.*
import org.apache.commons.collections4.queue.CircularFifoQueue
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class ChartActivity : BaseActivity(), BluetoothServiceCallback {

    private val data = CircularFifoQueue<Float>(20)
    private val tripleData = CircularFifoQueue<Triple<Float, Float, Float>>(20)
    private val publishSubject = PublishSubject.create<Pair<Characteristic, String>>()

    override fun getContentView(): Int = R.layout.activity_chart

    @Inject
    internal lateinit var bluetoothService: HexiBluetoothService

    private lateinit var device: BluetoothDevice

    companion object {
        private const val EXTRA_DEVICE = "KEY_DEVICE"
        private const val EXTRA_CHARACTERISTIC = "KEY_CHAR"
        fun getStartIntent(context: Context, bluetoothDevice: BluetoothDevice, characteristic: Characteristic): Intent {
            return Intent(context, ChartActivity::class.java).apply {
                putExtra(EXTRA_DEVICE, bluetoothDevice)
                putExtra(EXTRA_CHARACTERISTIC, characteristic)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        HexiApp.hexiComponent().inject(this)
        device = intent.extras.getParcelable(EXTRA_DEVICE)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        publishSubject.throttleLast(1, TimeUnit.SECONDS, Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ drawValue(it.first, it.second) })
        bluetoothService.setUpWithDevice(device, this)
        chart.xAxis.apply {
            axisMinimum = 0f
            axisMaximum = 20f
            granularity = 1f
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onConnectionStateChanged(isConnected: Boolean) {

    }

    override fun onNewDataAvailable(characteristic: Characteristic, value: String) {
        val neededCharacteristic = intent.extras.getSerializable(EXTRA_CHARACTERISTIC) as Characteristic
        if (characteristic == neededCharacteristic) {
            publishSubject.onNext(Pair(characteristic, value))
        }
    }

    private fun drawValue(characteristic: Characteristic, value: String) {
        when (characteristic) {
            Characteristic.HEART_RATE -> {
                data.offer(value.replace("[a-zA-Z]".toRegex(), "").trim().toFloat())
                val dataSet = LineDataSet(data.mapIndexed { index, fl -> Entry(index.toFloat(), fl) },
                        "Heart Rate").apply {
                    color = Color.RED
                    valueTextColor = Color.BLACK
                }
                chart.axisLeft.apply {
                    axisMinimum = 0f
                    axisMaximum = 150f
                }
                dataSet.let {
                    chart.data = LineData(it)
                }
            }
            Characteristic.GYRO -> {
                tripleData.offer(parseTriple(value))
                val xDataSet = LineDataSet(arrayListOf(), "Gyro X").apply {
                    color = Color.RED
                    valueTextColor = Color.BLACK
                }
                val yDataSet = LineDataSet(arrayListOf(), "Gyro Y").apply {
                    color = Color.GREEN
                    valueTextColor = Color.BLACK
                }
                val zDataSet = LineDataSet(arrayListOf(), "Gyro Z").apply {
                    color = Color.BLUE
                    valueTextColor = Color.BLACK
                }
                tripleData.forEachIndexed { index, (x, y, z) ->
                    xDataSet.addEntry(Entry(index.toFloat(), x))
                    yDataSet.addEntry(Entry(index.toFloat(), y))
                    zDataSet.addEntry(Entry(index.toFloat(), z))
                }
                chart.axisLeft.apply {
                    axisMaximum = 260f
                }
                chart.data = LineData(xDataSet, yDataSet, zDataSet)
            }
            Characteristic.ACCELERATION -> {
                tripleData.offer(parseTriple(value))
                val xDataSet = LineDataSet(arrayListOf(), "Acceleration X").apply {
                    color = Color.RED
                    valueTextColor = Color.BLACK
                }
                val yDataSet = LineDataSet(arrayListOf(), "Acceleration Y").apply {
                    color = Color.GREEN
                    valueTextColor = Color.BLACK
                }
                val zDataSet = LineDataSet(arrayListOf(), "Acceleration Z").apply {
                    color = Color.BLUE
                    valueTextColor = Color.BLACK
                }
                tripleData.forEachIndexed { index, (x, y, z) ->
                    xDataSet.addEntry(Entry(index.toFloat(), x))
                    yDataSet.addEntry(Entry(index.toFloat(), y))
                    zDataSet.addEntry(Entry(index.toFloat(), z))
                }
                chart.axisLeft.apply {
                    axisMinimum = -3f
                    axisMaximum = 3f
                }
                chart.data = LineData(xDataSet, yDataSet, zDataSet)
            }
        }
        chart.invalidate()
    }

    private fun parseTriple(value: String): Triple<Float, Float, Float> {
        return value.replace("[^0-9,;-]".toRegex(), "")
                .replace(",", ".")
                .split(";")
                .map { it.toFloat() }
                .toList()
                .let { Triple(it[0], it[1], it[2]) }
    }
}
