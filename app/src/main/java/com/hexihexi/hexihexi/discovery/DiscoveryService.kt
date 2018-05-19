package com.hexihexi.hexihexi.discovery


interface DiscoveryService {
    fun startScan()
    fun release()
    fun setDiscoveryCallback(discoveryCallback: DiscoveryCallback)
}
