package com.hexihexi.hexihexi.bluetooth

/**
 * Created by yurii on 10/22/17.
 */

enum class Characteristic {

    ACCELERATION(Type.READING, "00002001-0000-1000-8000-00805f9b34fb", "g"),
    GYRO(Type.READING, "00002002-0000-1000-8000-00805f9b34fb", "\u00B0/s"),
    MAGNET(Type.READING, "00002003-0000-1000-8000-00805f9b34fb", "\u00B5T"),
    LIGHT(Type.READING, "00002011-0000-1000-8000-00805f9b34fb", "%"),
    TEMPERATURE(Type.READING, "00002012-0000-1000-8000-00805f9b34fb", "\u2103"),
    HUMIDITY(Type.READING, "00002013-0000-1000-8000-00805f9b34fb", "%"),
    PRESSURE(Type.READING, "00002014-0000-1000-8000-00805f9b34fb", "kPa"),
    BATTERY(Type.READING, "00002a19-0000-1000-8000-00805f9b34fb", "%"),
    HEART_RATE(Type.READING, "00002021-0000-1000-8000-00805f9b34fb", "bpm"),
    STEPS(Type.READING, "00002022-0000-1000-8000-00805f9b34fb", ""),
    CALORIES(Type.READING, "00002023-0000-1000-8000-00805f9b34fb", ""),

    ALERT_IN(Type.ALERT, "00002031-0000-1000-8000-00805f9b34fb"),
    ALERT_OUT(Type.ALERT, "00002032-0000-1000-8000-00805f9b34fb"),

    MODE(Type.MODE, "00002041-0000-1000-8000-00805f9b34fb"),

    SERIAL(Type.INFO, "00002a25-0000-1000-8000-00805f9b34fb"),
    FW_REVISION(Type.INFO, "00002a26-0000-1000-8000-00805f9b34fb"),
    HW_REVISION(Type.INFO, "00002a27-0000-1000-8000-00805f9b34fb"),
    MANUFACTURER(Type.INFO, "00002a29-0000-1000-8000-00805f9b34fb"),

    CONTROL_POINT(Type.OTAP, "01ff5551-ba5e-f4ee-5ca1-eb1e5e4b1ce0"),
    DATA(Type.OTAP, "01ff5552-ba5e-f4ee-5ca1-eb1e5e4b1ce0"),
    STATE(Type.OTAP, "01ff5553-ba5e-f4ee-5ca1-eb1e5e4b1ce0");

    val type: Type
    val uuid: String
    val unit: String

    private constructor(type: Type, uuid: String) {
        this.type = type
        this.uuid = uuid
        this.unit = ""
    }

    private constructor(type: Type, uuid: String, unit: String) {
        this.type = type
        this.uuid = uuid
        this.unit = unit
    }

    enum class Type {
        READING, ALERT, MODE, INFO, OTAP
    }

    companion object {

        fun byUuid(uuid: String): Characteristic? = values().firstOrNull { it.uuid == uuid }

        fun byOrdinal(ordinal: Int): Characteristic = values()[ordinal]

        val readings: List<Characteristic>
            get() = values().filter { it.type == Type.READING }
    }
}
