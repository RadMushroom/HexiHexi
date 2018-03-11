package com.hexihexi.hexihexi

import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import com.hexihexi.hexihexi.discovery.BluetoothBondReceiver
import com.hexihexi.hexihexi.discovery.DiscoveryCallback
import com.hexihexi.hexihexi.discovery.DiscoveryService
import com.hexihexi.hexihexi.discovery.OnDeviceBondListener
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

class MainActivity : BaseActivity(), DiscoveryCallback, OnItemPositionClickListener, OnDeviceBondListener {

    private val devicesAdapter = DevicesAdapter(this)
    private val bluetoothBondReceiver = BluetoothBondReceiver(this)

    @Inject
    lateinit var discoveryService: DiscoveryService

    override fun getContentView(): Int = R.layout.activity_main

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        HexiApp.hexiComponent().inject(this)
        swipeRefresh.setOnRefreshListener { discoveryService.startScan() }
        devicesRecyclerView.adapter = devicesAdapter
        discoveryService.setDiscoveryCallback(this)
        status.setOnClickListener { discoveryService.release() }
    }

    override fun onDeviceBonded(intent: Intent) {
        runOnUiThread {
            val device = intent.extras.getParcelable<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
            devicesAdapter.handleDevice(device)
            fastForwardDevice(device)
        }
    }

    private fun fastForwardDevice(device: BluetoothDevice) {
        if (device.bondState == BluetoothDevice.BOND_BONDED && device.name == "HEXIWEAR") {
            startActivity(DeviceDetailsActivity.getStartIntent(this, device))
            finishAffinity()
        }
    }

    override fun onStart() {
        super.onStart()
        discoveryService.startScan()
        registerReceiver(bluetoothBondReceiver, IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED))
    }

    override fun onStop() {
        super.onStop()
        discoveryService.release()
        unregisterReceiver(bluetoothBondReceiver)
    }

    override fun onDeviceConnected(device: BluetoothDevice) {
        devicesAdapter.handleDevice(device)
        fastForwardDevice(device)
    }

    override fun onScanFinished() {
        status.text = "Status: Finished"
        swipeRefresh.isRefreshing = false
    }

    override fun onScanStarted() {
        status.text = "Status: Scanning..."
        swipeRefresh.isRefreshing = true
    }

    override fun onScanError() {
        status.text = "Status: Error during scan"
        swipeRefresh.isRefreshing = false
    }

    override fun onItemClicked(position: Int) {
        val device = devicesAdapter.getItem(position)
        device?.let {
            if (it.bondState != BluetoothDevice.BOND_BONDED) {
                it.createBond()
            } else {
                startActivity(DeviceDetailsActivity.getStartIntent(this, it))
            }
        }
    }
}
