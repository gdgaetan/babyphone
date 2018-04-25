package com.example.gaetan.babyphoneapp.api;

import com.example.gaetan.babyphoneapp.bluetooth.BluetoothDeviceConnector;

public class ApiBluetoothDevice {
    private static BluetoothDeviceConnector device;

    public static BluetoothDeviceConnector getdevice() {
        return device;
    }

    public static void setdevice(BluetoothDeviceConnector device) {
        ApiBluetoothDevice.device = device;
    }
}
