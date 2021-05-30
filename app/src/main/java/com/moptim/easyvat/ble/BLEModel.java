package com.moptim.easyvat.ble;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;

public class BLEModel {

    public BluetoothGattCharacteristic readCharacteristic;
    public BluetoothGattCharacteristic writeCharacteristic;

    public boolean bConnectState = false;//连接状态

    public int autoConnect = 1;

    public String deviceName;
    public String deviceAddress;
    public int    rssi;
    public String readData;

    public BLEModel(BluetoothDevice device){
        if (device.getName() != null && device.getName().length() > 0) {
            deviceName = device.getName();
        } else {
            deviceName = "Unknown";
        }
        deviceAddress = device.getAddress();
    }

}
