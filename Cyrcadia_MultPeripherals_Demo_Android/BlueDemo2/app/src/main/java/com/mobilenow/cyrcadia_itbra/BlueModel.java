package com.mobilenow.cyrcadia_itbra;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;

public class BlueModel {
    private BluetoothGatt gatt;
    private int mConnectionState;
    BluetoothDevice device;
    private int times = 0;
    private long startTime;
    private String deviceStatus;

    public BlueModel(BluetoothGatt gatt, int mConnectionState, BluetoothDevice device, long
            startTime) {
        this.gatt = gatt;
        this.mConnectionState = mConnectionState;
        this.device = device;
        this.startTime = startTime;
    }

    public int getTimes() {
        return times;
    }

    public void setTimes(int times) {
        this.times = times;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public BluetoothDevice getDevice() {
        return device;
    }

    public void setDevice(BluetoothDevice device) {
        this.device = device;
    }

    public BluetoothGatt getGatt() {
        return gatt;
    }

    public void setGatt(BluetoothGatt gatt) {
        this.gatt = gatt;
    }

    public int getmConnectionState() {
        return mConnectionState;
    }

    public void setmConnectionState(int mConnectionState) {
        this.mConnectionState = mConnectionState;
    }

    public String getDeviceStatus() {
        return deviceStatus;
    }

    public void setDeviceStatus(String deviceStatus) {
        this.deviceStatus = deviceStatus;
    }
    int count =0;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
