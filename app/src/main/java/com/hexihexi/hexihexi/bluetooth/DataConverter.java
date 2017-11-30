package com.hexihexi.hexihexi.bluetooth;

import java.util.Locale;

/**
 * Created by yurii on 10/22/17.
 */

public class DataConverter {

    private static final String INTEGER = "%d";
    private static final String FLOAT = "%.0f";
    private static final String TRIPLE_VALUE = "%+.0f%+.0f%+.0f";

    private DataConverter() {
        // Not meant to be instantiated.
    }

    public static String parseBluetoothData(final Characteristic characteristic, final byte[] data) {
        if (data == null || data.length == 0) return "";

        float floatVal;
        float xfloatVal;
        float yfloatVal;
        float zfloatVal;

        final String unit = characteristic.getUnit();

        switch (characteristic) {
            case HEART_RATE:
            case BATTERY:
            case LIGHT:
            case CALORIES:
            case STEPS:
                floatVal = (data[0] & 0xff);
                return String.format("%.0f %s", floatVal, unit);
            case TEMPERATURE:
            case HUMIDITY:
            case PRESSURE:
                final int intVal = (data[1] << 8) & 0xff00 | (data[0] & 0xff);
                floatVal = (float) intVal / 100;
                return String.format("%.2f %s", floatVal, unit);
            case ACCELERATION:
            case MAGNET:
                final int xintVal = ((int) data[1] << 8) | (data[0] & 0xff);
                xfloatVal = (float) xintVal / 100;

                final int yintVal = ((int) data[3] << 8) | (data[2] & 0xff);
                yfloatVal = (float) yintVal / 100;

                final int zintVal = ((int) data[5] << 8) | (data[4] & 0xff);
                zfloatVal = (float) zintVal / 100;

                return String.format("%.2f %s;%.2f %s;%.2f %s", xfloatVal, unit, yfloatVal, unit, zfloatVal, unit);
            case GYRO:
                final int gyroXintVal = ((int) data[1] << 8) | (data[0] & 0xff);
                xfloatVal = (float) gyroXintVal;

                final int gyroYintVal = ((int) data[3] << 8) | (data[2] & 0xff);
                yfloatVal = (float) gyroYintVal;

                final int gyroZintVal = ((int) data[5] << 8) | (data[4] & 0xff);
                zfloatVal = (float) gyroZintVal;

                return String.format("%.2f %s;%.2f %s;%.2f %s", xfloatVal, unit, yfloatVal, unit, zfloatVal, unit);
            default:
                return "Unknown";
        }
    }

    public static String formatForPublushing(final Characteristic characteristic, final byte[] data) {
        if (data == null || data.length == 0) return "";

        switch (characteristic) {
            case HEART_RATE:
            case LIGHT:
            case BATTERY:
            case CALORIES:
                final int heartRate = (data[0] & 0xff);
                return format(INTEGER, heartRate);
            case STEPS:
            case TEMPERATURE:
            case HUMIDITY:
                final int intVal = (data[1] << 8) & 0xff00 | (data[0] & 0xff);
                final float floatVal = (float) intVal / 10;
                return format(FLOAT, floatVal);
            case PRESSURE:
                final int pressureVal = (data[1] << 8) & 0xff00 | (data[0] & 0xff);
                return format(INTEGER, pressureVal);
            case ACCELERATION:
            case MAGNET:
                final int xintVal = ((int) data[1] << 8) | (data[0] & 0xff);
                final float xfloatVal = (float) xintVal / 10;

                final int yintVal = ((int) data[3] << 8) | (data[2] & 0xff);
                final float yfloatVal = (float) yintVal / 10;

                final int zintVal = ((int) data[5] << 8) | (data[4] & 0xff);
                final float zfloatVal = (float) zintVal / 10;
                return format(TRIPLE_VALUE, xfloatVal, yfloatVal, zfloatVal);
            case GYRO:
                final int gyroXintVal = ((int) data[1] << 8) | (data[0] & 0xff);
                final float gyroXfloatVal = (float) gyroXintVal * 10;

                final int gyroYintVal = ((int) data[3] << 8) | (data[2] & 0xff);
                final float gyroYfloatVal = (float) gyroYintVal * 10;

                final int gyroZintVal = ((int) data[5] << 8) | (data[4] & 0xff);

                final float gyroZfloatVal = (float) gyroZintVal * 10;
                return format(TRIPLE_VALUE, gyroXfloatVal, gyroYfloatVal, gyroZfloatVal);
            default:
                return "Unknown";
        }
    }

    private static String format(final String type, final Object... values) {
        return String.format(Locale.ENGLISH, type, values);
    }

}
