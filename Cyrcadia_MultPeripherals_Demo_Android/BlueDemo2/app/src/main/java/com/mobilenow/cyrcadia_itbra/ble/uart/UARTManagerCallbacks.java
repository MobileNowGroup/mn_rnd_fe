package com.mobilenow.cyrcadia_itbra.ble.uart;

import android.bluetooth.BluetoothDevice;

import com.mobilenow.cyrcadia_itbra.ble.base.BleManagerCallbacks;


public interface UARTManagerCallbacks extends BleManagerCallbacks {

    void onDataReceived(final BluetoothDevice device, final String data);

    void onDataSent(final BluetoothDevice device, final String data);
}
