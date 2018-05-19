package com.hexihexi.hexihexi.discovery

import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.content.Intent
import android.util.Log
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class DiscoveryServiceImpl(private val context: Context) : DiscoveryService {

    companion object {
        private val TAG = DiscoveryServiceImpl::class.java.simpleName
    }

    private val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
    private var scanCallback: ScanCallback? = null
    private var discoveryCallback: DiscoveryCallback? = null
    private var isScanRunning = false
    private var timeoutDisposable: Disposable? = null

    private val isEnabled: Boolean
        get() {
            return if (bluetoothAdapter == null) {
                Log.e(TAG, "Bluetooth not supported")
                false
            } else if (!bluetoothAdapter.isEnabled) {
                context.startActivity(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE))
                false
            } else {
                Log.v(TAG, "Bluetooth is enabled and functioning properly.")
                true
            }
        }

    override fun setDiscoveryCallback(discoveryCallback: DiscoveryCallback) {
        this.discoveryCallback = discoveryCallback
    }

    override fun startScan() {
        if (!isEnabled && isScanRunning) {
            return
        }

        isScanRunning = true
        scanCallback = object : ScanCallback() {
            override fun onScanResult(callbackType: Int, result: ScanResult) {
                super.onScanResult(callbackType, result)
                discoveryCallback?.onDeviceConnected(result.device)
            }

            override fun onScanFailed(errorCode: Int) {
                super.onScanFailed(errorCode)
                discoveryCallback?.onScanError()
                isScanRunning = false
            }
        }
        timeoutDisposable = Observable.just(0)
                .delay(30, TimeUnit.SECONDS, Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ _ -> release() }, { throwable -> Log.e(TAG, throwable.message) })
        with(bluetoothAdapter) {
            for (bondedDevice in bondedDevices) {
                discoveryCallback?.onDeviceConnected(bondedDevice)
            }
            bluetoothLeScanner.startScan(scanCallback)
        }
        discoveryCallback?.onScanStarted()
        Log.i(TAG, "Bluetooth device discovery started.")
    }

    override fun release() {
        timeoutDisposable?.run {
            if (!isDisposed) dispose()
        }

        if (!isEnabled) {
            return
        }

        bluetoothAdapter.bluetoothLeScanner.stopScan(scanCallback)
        discoveryCallback?.onScanFinished()
        isScanRunning = false
        Log.i(TAG, "Bluetooth device discovery canceled")
    }
}
