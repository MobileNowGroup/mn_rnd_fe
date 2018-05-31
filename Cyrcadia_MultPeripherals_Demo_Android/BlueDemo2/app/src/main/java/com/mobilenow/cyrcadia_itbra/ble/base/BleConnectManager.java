package com.mobilenow.cyrcadia_itbra.ble.base;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.PermissionUtils;
import com.mobilenow.cyrcadia_itbra.ble.uart.UARTManager;

import java.util.ArrayList;
import java.util.List;

import no.nordicsemi.android.support.v18.scanner.BluetoothLeScannerCompat;
//import no.nordicsemi.android.support.v18.scanner.ScanCallback;
//import no.nordicsemi.android.support.v18.scanner.ScanFilter;
//import no.nordicsemi.android.support.v18.scanner.ScanResult;
//import no.nordicsemi.android.support.v18.scanner.ScanSettings;


public class BleConnectManager {
    private static BleConnectManager instance = null;
    private boolean mIsScanning = false;
    private final Handler mHandler = new Handler();
    private final static long SCAN_DURATION = 15000;
    private Context context;
    static String BLE_IT_BRA_NAME = "iTBra";

    public static BleConnectManager getInstance() {
        if (instance == null) {
            instance = new BleConnectManager();
        }
        return instance;
    }

    void init(Context context) {
        this.context = context;
        // 蓝牙开闭状态接收器
        BluetoothStateListener listener = new BluetoothStateListener();
        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        context.registerReceiver(listener, filter);

    }


    public void connectItBra() {
//        UARTManager.getInstance(context).start();
        if (isBLEEnabled()) {
            if (ITBraBleManager.getInstance().isITBraBound()) {
                ITBraBleManager.getInstance().reConnect();
            }
            startScan();
        }
    }

    private BluetoothLeScanner scanner;
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            // 获得已经搜索到的蓝牙设备
            if (action.equals(BluetoothDevice.ACTION_FOUND)) {
                //通过此方法获取搜索到的蓝牙设备
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // 获取搜索到的蓝牙绑定状态,看看是否是已经绑定过的蓝牙
                Log.d("ddd", "Found Device " + device.getName() + ":" + device.getAddress() + ","
                        + "status = " + device.getBondState());
                if (device != null && device.getName() != null && device.getName().startsWith
                        (BLE_IT_BRA_NAME)) {
//                    if (results.get(i).getDevice() != null && results.get(i).getDevice().getName
//                            () != null) {
                    Log.d("BleManager", "device name = " + device.getName() + "," + "mac = " +
                            device.getAddress());
                    ITBraBleManager.getInstance().connectDevice(device.getAddress());
                }
                // 搜索完成
            } else if (action.equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)) {
//                setTitle("搜索蓝牙设备");
                Log.d("BleManager", "搜索蓝牙设备");
            } else if (action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)) {
                //获取发生改变的蓝牙对象
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                //根据不同的状态显示提示
                switch (device.getBondState()) {
                    case BluetoothDevice.BOND_BONDING://正在配对
                        Log.d("yxs", "正在配对......");
                        break;
                    case BluetoothDevice.BOND_BONDED://配对结束
                        Log.d("yxs", "完成配对");
//                        Toast.makeText(context, "签到成功", Toast.LENGTH_SHORT).show();
//                        handler.sendEmptyMessageDelayed(1,2000);
                        break;
                    case BluetoothDevice.BOND_NONE://取消配对/未配对
                        Log.d("yxs", "取消配对");
                    default:
                        break;
                }
            }
        }
    };

    private void startScan() {
        Log.d("BleManager", "startScan --------------------");
        if (!PermissionUtils.isGranted(Manifest.permission.ACCESS_COARSE_LOCATION)) {
            PermissionUtils.permission(Manifest.permission.ACCESS_COARSE_LOCATION).request();
            return;
        }
//        if (scanner != null) {
//            return;
//        }
        IntentFilter mFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        context.registerReceiver(mReceiver, mFilter);
        // 注册搜索完时的receiver
        mFilter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        context.registerReceiver(mReceiver, mFilter);
        //蓝牙连接状态发生改变时,接收状态
        mFilter = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        context.registerReceiver(mReceiver, mFilter);
        BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context
                .BLUETOOTH_SERVICE);
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        scanner = mBluetoothAdapter.getBluetoothLeScanner();
        mBluetoothAdapter.startDiscovery();

//        final ScanSettings settings = new ScanSettings.Builder().setScanMode(ScanSettings
//                .SCAN_MODE_LOW_LATENCY).setReportDelay(1000).setUseHardwareBatchingIfSupported
//                (false).build();
//        final List<ScanFilter> filters = new ArrayList<>();
//        scanner.startScan(callback);

        mIsScanning = true;
//        mHandler.postDelayed(() -> {
//            if (mIsScanning) {
//                stopScan();
//            }
//        }, SCAN_DURATION);
    }

    /**
     * Stop scan if user tap Cancel button
     */
    private void stopScan() {
        if (mIsScanning) {
            Log.d("BleManager", "stopScan--------- ");
//            scanner = BluetoothLeScannerCompat.getScanner();
            scanner.stopScan(callback);
            mIsScanning = false;
            scanner = null;
            mHandler.postDelayed(() -> {
                if (!mIsScanning) {
                    if (isBLEEnabled()) {
                        startScan();
                    }
                }
            }, 2000);
        }
    }

    ScanCallback callback = new ScanCallback() {
        @Override
        public void onBatchScanResults(List<android.bluetooth.le.ScanResult> results) {
            if (results != null && results.size() > 0) {
                for (int i = 0; i < results.size(); i++) {
//                    Log.d("itbra", "device name = " + results.get(i).getDevice().getName
//                            () + "," + "mac = " + results.get(i).getDevice().getAddress());
                    if (results.get(i).getDevice() != null && results.get(i).getDevice().getName
                            () != null && results.get(i).getDevice().getName().startsWith
                            (BLE_IT_BRA_NAME)) {
//                    if (results.get(i).getDevice() != null && results.get(i).getDevice().getName
//                            () != null) {
                        Log.d("BleManager", "device name = " + results.get(i).getDevice().getName
                                () + "," + "mac = " + results.get(i).getDevice().getAddress());
                        ITBraBleManager.getInstance().connectDevice(results.get(i).getDevice()
                                .getAddress());
                    }
                }
            }
        }
    };

//    private ScanCallback scanCallback = new ScanCallback() {
//        @Override
//        public void onScanResult(final int callbackType, final ScanResult result) {
//        }
//
//        @Override
//        public void onBatchScanResults(final List<ScanResult> results) {
//
//            if (results != null && results.size() > 0) {
//                for (int i = 0; i < results.size(); i++) {
////                    Log.d("itbra", "device name = " + results.get(i).getDevice().getName
////                            () + "," + "mac = " + results.get(i).getDevice().getAddress());
////                    if (results.get(i).getDevice() != null && results.get(i).getDevice().getName
////                            () != null && results.get(i).getDevice().getName().startsWith
////                            (BLE_IT_BRA_NAME)) {
//                    if (results.get(i).getDevice() != null && results.get(i).getDevice().getName
//                            () != null) {
//                        Log.d("BleManager", "device name = " + results.get(i).getDevice().getName
//                                () + "," + "mac = " + results.get(i).getDevice().getAddress());
//                        ITBraBleManager.getInstance().connectDevice(results.get(i).getDevice()
//                                .getAddress());
//                    }
//                }
//            }
//        }
//
//        @Override
//        public void onScanFailed(final int errorCode) {
//        }
//    };

    private boolean isBLEEnabled() {
        final BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService
                (Context.BLUETOOTH_SERVICE);
        assert bluetoothManager != null;
        final BluetoothAdapter adapter = bluetoothManager.getAdapter();
        return adapter != null && adapter.isEnabled();
    }

    private class BluetoothStateListener extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1);
            switch (state) {
                case BluetoothAdapter.STATE_TURNING_ON:
                    break;
                case BluetoothAdapter.STATE_ON:
                    connectItBra();
                    break;
                case BluetoothAdapter.STATE_TURNING_OFF:
                    break;
                case BluetoothAdapter.STATE_OFF:
                    ITBraBleManager.getInstance().disConnect();
                    break;
            }
        }
    }
}
