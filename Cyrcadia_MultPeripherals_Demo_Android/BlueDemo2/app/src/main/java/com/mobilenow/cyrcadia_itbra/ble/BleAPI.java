package com.mobilenow.cyrcadia_itbra.ble;

import android.bluetooth.BluetoothDevice;

import com.blankj.utilcode.util.LogUtils;
import com.mobilenow.cyrcadia_itbra.ble.base.BleConnectManager;
import com.mobilenow.cyrcadia_itbra.ble.base.ITBraBleManager;
import com.mobilenow.cyrcadia_itbra.data.ble.BatteryStatusModel;
import com.mobilenow.cyrcadia_itbra.data.ble.DeviceStatusModel;
import com.mobilenow.cyrcadia_itbra.data.ble.DeviceUDIModel;
import com.mobilenow.cyrcadia_itbra.data.ble.TemperatureDataModel;
import com.mobilenow.cyrcadia_itbra.data.ble.TemperatureRecordStatusModel;

import io.reactivex.Observable;


public class BleAPI {

    public static boolean isITBraConnect() {
        return ITBraBleManager.getInstance().isITBraConnect();
    }

    public static void connectITBra() {
        BleConnectManager.getInstance().connectItBra();
    }

    public static void initITBra() {
        ITBraBleManager.getInstance().initDevice(null);
    }

    public static Observable<DeviceStatusModel> getDeviceStatus() {
        return Observable.create(e -> ITBraBleManager.getInstance().getDeviceStatus(e));
    }

    public static Observable<DeviceStatusModel> getDeviceStatus(String mac ) {
        return Observable.create(e -> ITBraBleManager.getInstance().getDeviceStatus(mac, e));
    }

    public static Observable<Object> setDeviceTemperatureRange() {
        return Observable.create(e -> ITBraBleManager.getInstance().setDeviceTemperatureRange(e));
    }

    public static Observable<Object> startWarnUpTest() {
        return Observable.create(e -> ITBraBleManager.getInstance().startWarnUpTest(e));
    }

    public static Observable<Object> start() {
        return Observable.create(e -> ITBraBleManager.getInstance().start(e));
    }

    public static Observable<Object> clear() {
        return Observable.create(e -> ITBraBleManager.getInstance().clear(e));
    }

    public static Observable<Object> reset() {
        return Observable.create(e -> ITBraBleManager.getInstance().reset(e));
    }

    public static Observable<BatteryStatusModel> getBatteryStatus() {
        return Observable.create(e -> ITBraBleManager.getInstance().getBatteryStatus(e));
    }

    public static Observable<DeviceUDIModel> getDeviceUDI() {
        return Observable.create(e -> ITBraBleManager.getInstance().getDeviceUDI(e));
    }

    public static Observable<TemperatureRecordStatusModel> getRecordStatus() {
        return Observable.create(e -> ITBraBleManager.getInstance().getRecordStatus(e));
    }

    public static Observable<TemperatureDataModel> getRecordData() {
        LogUtils.d("@@@ getRecordData data  ");
        return Observable.create(e -> ITBraBleManager.getInstance().getRecordData(e));
    }
}
