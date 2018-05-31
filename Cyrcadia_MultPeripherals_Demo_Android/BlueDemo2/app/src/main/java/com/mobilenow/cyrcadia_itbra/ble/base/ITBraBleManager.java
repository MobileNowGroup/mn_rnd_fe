package com.mobilenow.cyrcadia_itbra.ble.base;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.util.Log;

import com.blankj.utilcode.util.LogUtils;
import com.mobilenow.cyrcadia_itbra.BlueModel;
import com.mobilenow.cyrcadia_itbra.ble.BleAPI;
import com.mobilenow.cyrcadia_itbra.ble.command.BaseCommand;
import com.mobilenow.cyrcadia_itbra.ble.command.ClearCommand;
import com.mobilenow.cyrcadia_itbra.ble.command.Command;
import com.mobilenow.cyrcadia_itbra.ble.command.GetBatteryStatusCommand;
import com.mobilenow.cyrcadia_itbra.ble.command.GetDeviceStatusCommand;
import com.mobilenow.cyrcadia_itbra.ble.command.GetDeviceTemperatureRangeCommand;
import com.mobilenow.cyrcadia_itbra.ble.command.GetDeviceUDIommand;
import com.mobilenow.cyrcadia_itbra.ble.command.GetTemperatureDataCommand;
import com.mobilenow.cyrcadia_itbra.ble.command.GetTemperatureRecordStatusCommand;
import com.mobilenow.cyrcadia_itbra.ble.command.NotifyBatteryStatusCommand;
import com.mobilenow.cyrcadia_itbra.ble.command.NotifyWarmAndMeasurementCommand;
import com.mobilenow.cyrcadia_itbra.ble.command.ResetCommand;
import com.mobilenow.cyrcadia_itbra.ble.command.SetMeasurementCommand;
import com.mobilenow.cyrcadia_itbra.ble.command.SetTemperatureRangeCommand;
import com.mobilenow.cyrcadia_itbra.ble.command.SetTimeCommand;
import com.mobilenow.cyrcadia_itbra.ble.command.StartCommand;
import com.mobilenow.cyrcadia_itbra.ble.command.WarnUpCommand;
import com.mobilenow.cyrcadia_itbra.ble.uart.UARTManager;
import com.mobilenow.cyrcadia_itbra.ble.uart.UARTManagerCallbacks;
import com.mobilenow.cyrcadia_itbra.data.ble.DeviceStatusModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import io.reactivex.ObservableEmitter;

public class ITBraBleManager implements UARTManagerCallbacks {
    private static ITBraBleManager instance = null;
    private HashMap<String, ObservableEmitter> callBackes = new HashMap<>();
    private HashMap<String, BaseCommand> commandList = new HashMap<>();
    private UARTManager mUARTManager;
    private Context context;
    private BluetoothDevice mBluetoothDevice;
    private List<BluetoothDevice> mBluetoothDeviceList = new ArrayList<>();
    private boolean isInit = false;
    private boolean isITBraConnect = false;

    public List<BlueModel> getDevices() {
        return mUARTManager.getmBleDevices();
    }

    public void getDeviceStatus(ObservableEmitter e) {
        addCommandAndSend(new GetDeviceStatusCommand(), e);
    }

    public void getDeviceStatus(BluetoothDevice device, ObservableEmitter e) {
        addCommandAndSend(device, new GetDeviceStatusCommand(), e);
    }

    public void setDeviceTemperatureRange(ObservableEmitter e) {
        addCommandAndSend(new SetTemperatureRangeCommand(), e);
    }

    public void startWarnUpTest(ObservableEmitter e) {
        addCommandAndSend(new WarnUpCommand(), e);
    }

    public void getRecordStatus(ObservableEmitter e) {
        addCommandAndSend(new GetTemperatureRecordStatusCommand(), e);
    }

    public void getRecordData(ObservableEmitter e) {
        addCommandAndSend(new GetTemperatureDataCommand(), e);
    }

    public void start(ObservableEmitter e) {
        addCommandAndSend(new StartCommand(), e);
    }

    public void clear(ObservableEmitter e) {
        addCommandAndSend(new ClearCommand(), e);
    }

    public void reset(ObservableEmitter e) {
        addCommandAndSend(new ResetCommand(), e);
    }

    public void getBatteryStatus(ObservableEmitter e) {
        addCommandAndSend(new GetBatteryStatusCommand(), e);
    }

    public void getDeviceUDI(ObservableEmitter e) {
        addCommandAndSend(new GetDeviceUDIommand(), e);
    }

    public void initDevice(ObservableEmitter e) {
        addCommandAndSend(new SetTimeCommand(), e);
        addCommandAndSend(new SetMeasurementCommand(), e);
        addCommandAndSend(new GetDeviceTemperatureRangeCommand(), e);
    }

    private void addCommandAndSend(BaseCommand command, ObservableEmitter e) {
        int size = commandList.size();
        if (size > 2) {
            new Thread(() -> {
                try {
                    Thread.sleep(100 + (size - 1) * 200);
                    command.sendCommand();

                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }).start();
        } else {
            command.sendCommand();
        }
        callBackes.put(command.getCommandID(), e);
        commandList.put(command.getCommandID(), command);
    }

    private void addCommandAndSend(BluetoothDevice device, BaseCommand command, ObservableEmitter
            e) {
        int size = commandList.size();
        if (size > 2) {
            new Thread(() -> {
                try {
                    Thread.sleep(100 + (size - 1) * 200);
                    command.sendCommand(device);

                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }).start();
        } else {
            command.sendCommand();
        }
        callBackes.put(command.getCommandID(), e);
        commandList.put(command.getCommandID(), command);
    }

    private void addNotifyCommand(BaseCommand command) {
        commandList.put(command.getCommandID(), command);
    }

    public static ITBraBleManager getInstance() {
        if (instance == null) {
            instance = new ITBraBleManager();
        }
        return instance;
    }

    public void connectDevice(String deviceAddress) {
        final BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService
                (Context.BLUETOOTH_SERVICE);
        final BluetoothAdapter adapter = bluetoothManager.getAdapter();
        mBluetoothDevice = adapter.getRemoteDevice(deviceAddress);
        mUARTManager.connect(mBluetoothDevice);
    }

    public void reConnect() {
        if (mBluetoothDeviceList != null && mBluetoothDeviceList.size() > 0) {
            for (int i = 0; i < mBluetoothDeviceList.size(); i++) {
                mUARTManager.connect(mBluetoothDeviceList.get(i));
            }
        }
    }

    public boolean isITBraBound() {
        mBluetoothDeviceList.clear();
        final BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService
                (Context.BLUETOOTH_SERVICE);
        final BluetoothAdapter adapter = bluetoothManager.getAdapter();
        Set<BluetoothDevice> boundBleSet = adapter.getBondedDevices();
        if (boundBleSet != null && boundBleSet.size() > 0) {
            for (BluetoothDevice device : boundBleSet) {
                Log.d("BleManager", "isITBraBound addr = " + device.getAddress());
                int boundStatus = device.getBondState();
                if (device.getName() != null && device.getName().equalsIgnoreCase
                        (BleConnectManager.BLE_IT_BRA_NAME)) {
//                    mBluetoothDevice = device;
//                    mBluetoothDeviceList.add(device);
//                    return true;
                }
            }
        }
        if (mBluetoothDeviceList != null && mBluetoothDeviceList.size() > 0) {
            return true;
        }
        return false;
    }


    public void disConnect() {
        UARTManager.getInstance(context).stop();
        mUARTManager.close();
        onDeviceDisconnected(mBluetoothDevice);
    }

    public void init(Context context) {
        this.context = context;
        BleConnectManager.getInstance().init(context);
        mUARTManager = UARTManager.getInstance(context);
        mUARTManager.setGattCallbacks(this);
    }

    public void send(BluetoothDevice device, String text) {
        mUARTManager.send(device, Command.COMMAND_HEAD + text);
    }

    public void send(String text) {
        mUARTManager.send(Command.COMMAND_HEAD + text);
    }

    @Override
    public void onDeviceConnecting(BluetoothDevice device) {

    }


    @Override
    public void onDeviceConnected(BluetoothDevice device) {
        isITBraConnect = true;
    }

    @Override
    public void onDeviceDisconnecting(BluetoothDevice device) {

    }

    @Override
    public void onDeviceDisconnected(BluetoothDevice device) {
        isITBraConnect = false;
    }

    @Override
    public void onLinklossOccur(BluetoothDevice device) {

    }

    @Override
    public void onServicesDiscovered(BluetoothDevice device, boolean optionalServicesFound) {

    }

    @Override
    public void onDeviceReady(BluetoothDevice device) {
    }

    @Override
    public boolean shouldEnableBatteryLevelNotifications(BluetoothDevice device) {
        return false;
    }

    @Override
    public void onBatteryValueReceived(BluetoothDevice device, int value) {

    }

    @Override
    public void onBondingRequired(BluetoothDevice device) {

    }

    @Override
    public void onBonded(BluetoothDevice device) {

    }

    @Override
    public void onError(BluetoothDevice device, String message, int errorCode) {

    }

    @Override
    public void onDeviceNotSupported(BluetoothDevice device) {

    }

    @Override
    public void onReady(BluetoothGatt gatt) {
//        EventBus.getDefault().post(new DeviceStatusEventMessage(DeviceStatusEventMessage.Status
//                .CONNECT));
        LogUtils.d("ddd init device, isInit = " + isInit);
        if (!isInit) {
            addNotifyCommand(new NotifyBatteryStatusCommand());
            addNotifyCommand(new NotifyWarmAndMeasurementCommand());
            isInit = true;

        }
        BleAPI.getDeviceStatus(gatt.getDevice()).subscribe(new DefaultObserver<DeviceStatusModel>
                () {
            @Override
            public void onNext(DeviceStatusModel deviceStatusModel) {
                if (deviceStatusModel != null && deviceStatusModel.getStatusByte() ==
                        DeviceStatusModel.StatusByte.WARMUP) {
//                    ActivityUtils.startActivity(PreScanCheckActivity.class);
                }
            }

            @Override
            public void onError(Throwable e) {

            }
        });
    }


    @Override
    public void onDataReceived(BluetoothDevice device, String data) {
        String commandID = Command.getCommandIDFromRes(data);
        if (commandID != null) {
            BaseCommand baseCommand = commandList.get(commandID);
            if (baseCommand != null) {
                ObservableEmitter emitter = callBackes.get(commandID);
                if (!baseCommand.isNotify()) {
                    commandList.remove(commandID);
                    if (emitter != null) {
                        callBackes.remove(commandID);
                    }
                }
                baseCommand.parseCommand(data, emitter);

            }
        }

    }

    @Override
    public void onDataSent(BluetoothDevice device, String data) {

    }

    public boolean isITBraConnect() {
        return isITBraConnect;
    }
}
