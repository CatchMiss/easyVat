/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.moptim.easyvat.ble;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.graphics.BlendMode;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Service for managing connection and data communication with a GATT server hosted on a
 * given Bluetooth LE device.
 */
public class BluetoothLeService extends Service {

    private final static String TAG = BluetoothLeService.class.getSimpleName();

    private final static String WRITE_SER_UUID_STR = "0000fff0-0000-1000-8000-00805f9b34fb";
    private final static String WRITE_CHA_UUID_STR = "0000fff2-0000-1000-8000-00805f9b34fb";
    private final static String READ_SER_UUID_STR = WRITE_SER_UUID_STR;
    private final static String READ_CHA_UUID_STR = "0000fff1-0000-1000-8000-00805f9b34fb";

    public static String writeServiceUuidStr = WRITE_SER_UUID_STR;
    public static String writeCharacterUuidStr = WRITE_CHA_UUID_STR;
    public static String readServiceUuidStr = READ_SER_UUID_STR;
    public static String readCharacterUuidStr = READ_CHA_UUID_STR;

    public final static String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";

    private BluetoothAdapter mBluetoothAdapter;
    public ArrayList<BluetoothGatt> mBluetoothGattList = new ArrayList<>();
    public ArrayList<BLEModel> mBleModeList = new ArrayList<>();

    private boolean bBLEScanning;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(EventUtil eventUtil) {
        Log.i(TAG, "onEventMainThread: " + eventUtil.what);
        switch (eventUtil.what) {
            case S_START_SCAN:
                startScanDevice();
                break;
            case S_STOP_SCAN:
                stopScanDevice();
                break;
            case S_CONNECT: {
                BLEModel model = eventUtil.obj;
                if (model != null) {
                    close(model.deviceAddress);
                    connect(model.deviceAddress);
                }
            }
            break;
            case S_AUTO_CONNECT: {
                BLEModel model = eventUtil.obj;
                if (model != null && model.autoConnect > 0) {
                    model.autoConnect--;
                    connect(model.deviceAddress);
                }
            }
            break;
            case S_DISCONNECT: {
                BLEModel model = eventUtil.obj;
                if (model != null) {
                    model.autoConnect = 0;
                    disconnect(model.deviceAddress);
                }
            }
            break;
            case S_WRITE_DATA:
                break;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        EventBus.getDefault().register(this);

        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        if (bluetoothManager == null) {
            Log.e(TAG, "Get bluetooth fail!");
        } else {
            mBluetoothAdapter = bluetoothManager.getAdapter();
            if (mBluetoothAdapter == null) {
                Log.e(TAG, "Get bluetooth adapter fail!");
            } else {
                if (!mBluetoothAdapter.isEnabled()) {
                    mBluetoothAdapter.enable();
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);

        stopScanDevice();

        for (BluetoothGatt bluetoothGatt : mBluetoothGattList) {
            if (bluetoothGatt == null) {
                continue;
            }
            bluetoothGatt.disconnect();
            bluetoothGatt.close();
            mBluetoothGattList.remove(bluetoothGatt);
        }

        super.onDestroy();
    }

    public void startScanDevice() {
        if (!bBLEScanning && mBluetoothAdapter != null) {
            bBLEScanning = true;
            mBluetoothAdapter.startLeScan(mLeScanCallback);
        }
    }

    public void stopScanDevice() {
        if (bBLEScanning && mBluetoothAdapter != null) {
            bBLEScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
    }

    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, final int rssi, byte[] scanRecord) {
            BLEModel mode = getModeForAddress(device.getAddress());
            if (mode == null) {
                mode = new BLEModel(device);
                mBleModeList.add(mode);
            }
            mode.rssi = rssi;

            EventUtil eventUtil = new EventUtil();
            eventUtil.what = EventWhat.STATUS_DISCOVER;
            eventUtil.obj = mode;
            EventBus.getDefault().post(eventUtil);
        }
    };

    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                Log.i(TAG, "BLE Connected!Attempting to start service discovery!");
                //开始扫描BLE服务
                gatt.discoverServices();
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Log.i(TAG, "Disconnected from GATT server.");
                // 失去连接
                BLEModel bleModel = getModeForAddress(gatt.getDevice().getAddress());
                if(bleModel != null){
                    bleModel.bConnectState = false;

                    EventUtil eventUtil = new EventUtil();
                    eventUtil.what = EventWhat.STATUS_DISCONNECT;
                    eventUtil.obj = getModeForAddress(gatt.getDevice().getAddress());
                    EventBus.getDefault().post(eventUtil);
                }
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                displayGattServices(gatt);
            } else {
                Log.w(TAG, "onServicesDiscovered received: " + status);
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.i(TAG, "characteristics read: " + Arrays.toString(characteristic.getValue()));
            }
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.i(TAG, "characteristics write: " + Arrays.toString(characteristic.getValue()));
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            BLEModel mode = getModeForAddress(gatt.getDevice().getAddress());
            if(mode != null){
                mode.readData = new String(characteristic.getValue(), StandardCharsets.US_ASCII);

                EventUtil eventUtil = new EventUtil();
                eventUtil.what = EventWhat.STATUS_READ_DATA;
                eventUtil.obj = mode;
                EventBus.getDefault().post(eventUtil);
            }
        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            super.onReadRemoteRssi(gatt, rssi, status);
        }
    };

    private void displayGattServices(BluetoothGatt bluetoothGatt) {
        boolean bFindWriteCharacter = false;
        boolean bFindReadCharacter = false;

        List<BluetoothGattService> gattServices = bluetoothGatt.getServices();
        BLEModel bleModel = getModeForAddress(bluetoothGatt.getDevice().getAddress());

        if (gattServices == null || bleModel == null) return;

        //遍历扫描到的服务
        for (BluetoothGattService gattService : gattServices) {

            //查询是否搜索到写服务
            if (gattService.getUuid().toString().equals(writeServiceUuidStr)) {
                //轮询是否有写特征
                for (BluetoothGattCharacteristic character : gattService.getCharacteristics()) {
                    Log.i(TAG, "characteristics:" + character.getUuid().toString());
                    //是否有写特征
                    if (character.getUuid().toString().equals(writeCharacterUuidStr)) {
                        bleModel.writeCharacteristic = character;
                        bFindWriteCharacter = true;
                    }

                    //如果读写在同个服务,查询是否有读特征
                    if (writeServiceUuidStr.equals(readServiceUuidStr) && character.getUuid().toString().equals(readCharacterUuidStr)) {
                        bleModel.readCharacteristic = character;
                        bFindReadCharacter = true;
                        //开启读通知
                        if ((character.getProperties() & BluetoothGattCharacteristic.PROPERTY_NOTIFY) == 0x10) {
                            setCharacteristicNotification(character, true, bluetoothGatt);
                        }
                    }
                }
            }

            //查询是否搜索到读服务
            if (!writeServiceUuidStr.equals(readServiceUuidStr)) {
                if (gattService.getUuid().toString().equals(readServiceUuidStr)) {
                    //轮询是否有读特征
                    for (BluetoothGattCharacteristic character : gattService.getCharacteristics()) {
                        Log.i(TAG, "characteristics:" + character.getUuid().toString());
                        if (character.getUuid().toString().equals(readCharacterUuidStr)) {
                            bleModel.readCharacteristic = character;
                            bFindReadCharacter = true;
                            //开启读通知
                            if ((character.getProperties() & BluetoothGattCharacteristic.PROPERTY_NOTIFY) == 0x10) {
                                setCharacteristicNotification(character, true, bluetoothGatt);
                            }
                        }
                    }
                }
            }
        }

        if (bFindWriteCharacter && bFindReadCharacter) {
            bleModel.bConnectState = true;
            bluetoothGatt.readRemoteRssi();
            //两个特征都找到后,才算连上
            EventUtil eventUtil = new EventUtil();
            eventUtil.what = EventWhat.STATUS_CONNECT;
            eventUtil.obj = getModeForAddress(bluetoothGatt.getDevice().getAddress());
            EventBus.getDefault().post(eventUtil);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * Connects to the GATT server hosted on the Bluetooth LE device.
     *
     * @param address The device address of the destination device.
     * @return Return true if the connection is initiated successfully. The connection result
     * is reported asynchronously through the
     * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     * callback.
     */
    public void connect(final String address) {
        if (mBluetoothAdapter == null || address == null) {
            return;
        }

        // Previously connected device.  Try to reconnect.
        BluetoothGatt mBluetoothGatt = getGattForAddress(address);
        if (mBluetoothGatt != null) {
            mBluetoothGatt.connect();
            return;
        }

        //新增的特征
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        if (device == null) {
            Log.i(TAG, "Device not found.  Unable to connect.");
            return;
        }

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            mBluetoothGatt = device.connectGatt(this, true, mGattCallback, TRANSPORT_LE);
//        } else {
//            mBluetoothGatt = device.connectGatt(this, true, mGattCallback);
//        }

        // We want to directly connect to the device, so we are setting the autoConnect parameter to false.
        mBluetoothGatt = device.connectGatt(this, false, mGattCallback);
        mBluetoothGattList.add(mBluetoothGatt);
        Log.d(TAG, "Trying to create a new connection.");
    }

    /**
     * Disconnects an existing connection or cancel a pending connection. The disconnection result
     * is reported asynchronously through the
     * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     * callback.
     */
    public void disconnect(String address) {
        BluetoothGatt mBluetoothGatt = getGattForAddress(address);
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.disconnect();
    }

    /**
     * After using a given BLE device, the app must call this method to ensure resources are
     * released properly.
     */
    public void close(String address) {
        BluetoothGatt mBluetoothGatt = getGattForAddress(address);
        if (mBluetoothGatt == null) {
            return;
        }
        mBluetoothGatt.close();
        mBluetoothGattList.remove(mBluetoothGatt);
    }

    public void writeDataBytes(String address, byte[] dataBytes) {
        BLEModel mode = getModeForAddress(address);
        if (mode == null || mode.writeCharacteristic == null || dataBytes == null) {
            return;
        }

        mode.writeCharacteristic.setValue(dataBytes);
        writeCharacteristic(mode.writeCharacteristic, mode.deviceAddress);
    }

    /**
     * Request a read on a given {@code BluetoothGattCharacteristic}. The read result is reported
     * asynchronously through the {@code BluetoothGattCallback#onCharacteristicRead(android.bluetooth.BluetoothGatt, android.bluetooth.BluetoothGattCharacteristic, int)}
     * callback.
     *
     * @param characteristic The characteristic to read from.
     * @param address        device address
     */
    public void readCharacteristic(BluetoothGattCharacteristic characteristic, String address) {
        BluetoothGatt mBluetoothGatt = getGattForAddress(address);
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.readCharacteristic(characteristic);
    }

    /**
     * @param characteristic write characteristic
     * @param address        device address
     */
    public void writeCharacteristic(BluetoothGattCharacteristic characteristic, String address) {
        BluetoothGatt mBluetoothGatt = getGattForAddress(address);
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.writeCharacteristic(characteristic);
    }

    /**
     * Enables or disables notification on a give characteristic.
     *
     * @param characteristic Characteristic to act on.
     * @param enabled        If true, enable notification.  False otherwise.
     */
    public void setCharacteristicNotification(BluetoothGattCharacteristic characteristic,
                                              boolean enabled, BluetoothGatt mBluetoothGatt) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);

        BluetoothGattDescriptor descriptor = characteristic.getDescriptor(UUID.fromString(CLIENT_CHARACTERISTIC_CONFIG));
        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        mBluetoothGatt.writeDescriptor(descriptor);
    }

    /**
     * Get BluetoothGatt depend on device address from BluetoothGatt list, if nothing return null.
     *
     * @param address device address
     * @return BluetoothGatt
     */
    private BluetoothGatt getGattForAddress(String address) {
        for (BluetoothGatt gatt : mBluetoothGattList) {
            if (gatt.getDevice().getAddress().equals(address)) {
                return gatt;
            }
        }
        return null;
    }

    private BLEModel getModeForAddress(String address) {
        for (BLEModel mode : mBleModeList) {
            if (mode.deviceAddress.equals(address)) {
                return mode;
            }
        }
        return null;
    }

}
