package com.hexihexi.hexihexi.bluetooth

/**
 * Created by yurii on 10/22/17.
 */

class ManufacturerInfo {
    var manufacturer: String? = null
    var hardwareRevision: String? = null
    var firmwareRevision: String? = null

    override fun toString(): String {
        return "ManufacturerInfo{" +
                "manufacturer='" + manufacturer + '\'' +
                ", hardwareRevision='" + hardwareRevision + '\'' +
                ", firmwareRevision='" + firmwareRevision + '\'' +
                '}'
    }
}
