package com.hexihexi.hexihexi;

import com.hexihexi.hexihexi.bluetooth.Characteristic;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yurii on 10/22/17.
 */

public enum Mode {

    IDLE(0, R.string.mode_idle),
    WATCH(1, R.string.mode_watch),
    SENSOR_TAG(2, R.string.mode_sensor_tag),
    WEATHER_STATION(3, R.string.mode_weather_station),
    MOTION_CONTROL(4, R.string.mode_motion_control),
    HEARTRATE(5, R.string.mode_heartrate),
    PEDOMETER(6, R.string.mode_pedometer),
    COMPASS(7, R.string.mode_compass);

    private final int symbol;
    private final int stringResource;

    Mode(final int symbol, final int stringResource) {
        this.symbol = symbol;
        this.stringResource = stringResource;
    }

    public int getStringResource() {
        return stringResource;
    }

    public static Mode bySymbol(final int symbol) {
        for (Mode mode : values()) {
            if (mode.symbol == symbol) {
                return mode;
            }
        }
        throw new IllegalArgumentException("No mode with such symbol: " + symbol);
    }

    public List<Characteristic> getCharacteristics() {
        final List<Characteristic> characteristics = new ArrayList<>();

        switch (this) {
            case SENSOR_TAG:
                characteristics.add(Characteristic.BATTERY);
                characteristics.add(Characteristic.ACCELERATION);
                characteristics.add(Characteristic.MAGNET);
                characteristics.add(Characteristic.GYRO);
                characteristics.add(Characteristic.TEMPERATURE);
                characteristics.add(Characteristic.HUMIDITY);
                characteristics.add(Characteristic.PRESSURE);
                characteristics.add(Characteristic.LIGHT);
                break;
            case PEDOMETER:
                characteristics.add(Characteristic.STEPS);
                characteristics.add(Characteristic.CALORIES);
                break;
            case HEARTRATE:
                characteristics.add(Characteristic.HEART_RATE);
                break;
            default:
                break;
        }

        return characteristics;
    }

    public boolean hasCharacteristic(final String characteristicName) {
        return hasCharacteristic(Characteristic.valueOf(characteristicName));
    }

    public boolean hasCharacteristic(final Characteristic characteristic) {
        final List<Characteristic> characteristics = getCharacteristics();
        return characteristics.contains(characteristic);
    }

}