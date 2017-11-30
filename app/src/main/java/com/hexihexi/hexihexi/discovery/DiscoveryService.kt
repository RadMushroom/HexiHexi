package com.hexihexi.hexihexi.discovery

/**
 * Created by yurii on 10/16/17.
 */

interface DiscoveryService {
    fun startScan()
    fun release()
    fun setDiscoveryCallback(discoveryCallback: DiscoveryCallback)
}
