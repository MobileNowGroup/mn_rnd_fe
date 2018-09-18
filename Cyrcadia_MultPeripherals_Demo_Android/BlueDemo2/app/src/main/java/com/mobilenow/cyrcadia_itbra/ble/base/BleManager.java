package com.mobilenow.cyrcadia_itbra.ble.base;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.blankj.utilcode.util.LogUtils;
import com.mobilenow.cyrcadia_itbra.BlueModel;
import com.mobilenow.cyrcadia_itbra.event.BleStatusChangeEvent;
import com.mobilenow.cyrcadia_itbra.util.ParserUtils;

import org.greenrobot.eventbus.EventBus;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;


public abstract class BleManager<E extends BleManagerCallbacks> {
    protected final static String TAG = "BleManager";

    private final static UUID CLIENT_CHARACTERISTIC_CONFIG_DESCRIPTOR_UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");

    private final static UUID BATTERY_SERVICE = UUID.fromString("0000180F-0000-1000-8000-00805f9b34fb");
    private final static UUID BATTERY_LEVEL_CHARACTERISTIC = UUID.fromString("00002A19-0000-1000-8000-00805f9b34fb");

    private final static UUID GENERIC_ATTRIBUTE_SERVICE = UUID.fromString("00001801-0000-1000-8000-00805f9b34fb");
    private final static UUID SERVICE_CHANGED_CHARACTERISTIC = UUID.fromString("00002A05-0000-1000-8000-00805f9b34fb");

    private final Object mLock = new Object();
    /**
     * The log session or null if nRF Logger is not installed.
     */
    private final Context mContext;
    private final Handler mHandler;
    boolean isInit = false;
    private BluetoothDevice mBluetoothDevice;
    protected E mCallbacks;
    //    private BluetoothGatt mBluetoothGatt;
    private CopyOnWriteArrayList<BlueModel> mBleDevices = new CopyOnWriteArrayList<>();
//    private BleManagerGattCallback mGattCallback;
    /**
     * Flag set to true when the device is connected.
     */
    private boolean mConnected;
    /**
     * Last received battery value or -1 if value wasn't received.
     */
    private final BroadcastReceiver mBluetoothStateBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.STATE_OFF);
            final int previousState = intent.getIntExtra(BluetoothAdapter.EXTRA_PREVIOUS_STATE, BluetoothAdapter.STATE_OFF);

            final String stateString = "[Broadcast] Action received: " + BluetoothAdapter.ACTION_STATE_CHANGED + ", state changed to " + state2String(state);
//            LogUtils.d(TAG, stateString);

            switch (state) {
                case BluetoothAdapter.STATE_TURNING_OFF:
                case BluetoothAdapter.STATE_OFF:
                    if (mConnected && previousState != BluetoothAdapter.STATE_TURNING_OFF && previousState != BluetoothAdapter.STATE_OFF) {
                        // The connection is killed by the system, no need to gently disconnect
//                        mGattCallback.notifyAllDeviceDisconnected();
                    }
                    // Calling close() will prevent the STATE_OFF event from being logged (this
                    // receiver will be unregistered). But it doesn't matter.
                    close();
                    break;
            }
        }

        private String state2String(final int state) {
            switch (state) {
                case BluetoothAdapter.STATE_TURNING_ON:
                    return "TURNING ON";
                case BluetoothAdapter.STATE_ON:
                    return "ON";
                case BluetoothAdapter.STATE_TURNING_OFF:
                    return "TURNING OFF";
                case BluetoothAdapter.STATE_OFF:
                    return "OFF";
                default:
                    return "UNKNOWN (" + state + ")";
            }
        }
    };

    private BroadcastReceiver mBondingBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            final BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            final int bondState = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, -1);
            final int previousBondState = intent.getIntExtra(BluetoothDevice.EXTRA_PREVIOUS_BOND_STATE, -1);

            BluetoothGatt bluetoothGatt = getGattByDeviceAddr(device.getAddress());
            if (bluetoothGatt == null) {
                return;
            }
            Log.d(TAG, "[Broadcast] Action received: " + BluetoothDevice.ACTION_BOND_STATE_CHANGED + ", bond state changed to: " + bondStateToString(bondState) + " (" + bondState + ")");
            Log.i(TAG, "Bond state changed for: " + device.getName() + " new state: " + bondState + " previous: " + previousBondState);

            switch (bondState) {
                case BluetoothDevice.BOND_BONDING:
                    mCallbacks.onBondingRequired(device);
                    break;
                case BluetoothDevice.BOND_BONDED:
                    Log.i(TAG, "Device bonded");
                    bluetoothGatt.discoverServices();
                    mCallbacks.onBonded(device);
                    break;
            }
        }
    };

    public BluetoothGatt getGattByDeviceAddr(String addr) {
        if (mBleDevices == null || mBleDevices.size() == 0 || addr == null) {
            return null;
        }
        for (int i = 0; i < mBleDevices.size(); i++) {
            BlueModel bluetoothGatt = mBleDevices.get(i);
            if (addr.equals(bluetoothGatt.getDevice().getAddress())) {
                return bluetoothGatt.getGatt();
            }
        }
        return null;
    }

    public BlueModel getModelByDeviceAddr(String addr) {
        if (mBleDevices == null || mBleDevices.size() == 0 || addr == null) {
            return null;
        }
        for (int i = 0; i < mBleDevices.size(); i++) {
            BlueModel bluetoothGatt = mBleDevices.get(i);
            if (addr.equals(bluetoothGatt.getDevice().getAddress())) {
                return bluetoothGatt;
            }
        }
        return null;
    }

    private static void unpairDevice(BluetoothDevice device) {
        try {
            Method m = device.getClass().getMethod("removeBond", (Class[]) null);
            m.invoke(device, (Object[]) null);
            Log.d(TAG, "unpairDevice mac = " + device.getAddress() + " success");
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    public boolean isHaveDeviceConnecting() {
        if (mBleDevices == null || mBleDevices.size() == 0) {
            return false;
        }
        Iterator<BlueModel> it = mBleDevices.iterator();
        while (it.hasNext()) {
            BlueModel bluetoothGatt = it.next();
            if (bluetoothGatt.getmConnectionState() == BlueStatus.STATE_CONNECTING) {
                if (System.currentTimeMillis() - bluetoothGatt.getStartTime() > 20000) {
                    bluetoothGatt.setmConnectionState(BlueStatus.STATE_INIT);
                    Log.d("dddd", "isHaveDeviceConnecting ");
                    bluetoothGatt.setStartTime(System.currentTimeMillis());
                    EventBus.getDefault().post(new BleStatusChangeEvent());
                    Log.d(TAG, "device need reconnect mac = " + bluetoothGatt.getDevice().getAddress());
//                    if (bluetoothGatt.getGatt() != null) {
//                        bluetoothGatt.getGatt().disconnect();
//                    }
//                    if (bluetoothGatt.getDevice() != null) {
//                        if (bluetoothGatt.getDevice().getBondState() == BluetoothDevice
//                                .BOND_BONDED) {
//                            unpairDevice(bluetoothGatt.getDevice());
//                        }
//                    }
                } else {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isDeviceNeedConnect(BluetoothDevice device) {
        if (mBleDevices == null || mBleDevices.size() == 0 || device == null) {
            return false;
        }
        for (int i = 0; i < mBleDevices.size(); i++) {
            BlueModel bluetoothGatt = mBleDevices.get(i);
            if (bluetoothGatt.getDevice().getAddress().equalsIgnoreCase(device.getAddress())) {
                if (bluetoothGatt.getmConnectionState() == BlueStatus.STATE_CONNECTED || bluetoothGatt.getmConnectionState() == BlueStatus.STATE_CONNECTING || bluetoothGatt.getmConnectionState() == BlueStatus.STATE_CLOSE) {
                    return false;
                } else {
                    return true;
                }
            }
        }
        return true;
    }

    public BluetoothDevice getConnectDevice() {
        if (mBleDevices == null || mBleDevices.size() == 0) {
            return null;
        }
        BlueModel bluetoothGatt = null;
        for (int i = 0; i < mBleDevices.size(); i++) {
            BlueModel temp = mBleDevices.get(i);
            if (temp.getmConnectionState() == BlueStatus.STATE_INIT) {
                if (bluetoothGatt == null || temp.getTimes() < bluetoothGatt.getTimes()) {
                    bluetoothGatt = temp;
                }
            }
            if (temp != null && temp.getmConnectionState() == BlueStatus.STATE_CONNECTING) {
                if (System.currentTimeMillis() - temp.getStartTime() > 20000) {
                    temp.setmConnectionState(BlueStatus.STATE_INIT);
                    Log.d("dddd", "isHaveDeviceConnecting ");
                    bluetoothGatt.setStartTime(System.currentTimeMillis());
                    EventBus.getDefault().post(new BleStatusChangeEvent());
                    Log.d(TAG, "device need reconnect mac = " + temp.getDevice().getAddress());
                    if (temp.getGatt() != null) {
                        temp.getGatt().disconnect();
//                        it.remove();
                    }
                    if (temp.getDevice() != null) {
                        if (temp.getDevice().getBondState() == BluetoothDevice.BOND_BONDED) {
//                            unpairDevice(temp.getDevice());
                        }
                    }
                } else {
                }
            }
        }
        if (bluetoothGatt != null) return bluetoothGatt.getDevice();
        return null;
    }

    public BluetoothGatt getConnectDeviceGatt(String addr) {
        if (mBleDevices == null || mBleDevices.size() == 0) {
            return null;
        }
        for (int i = 0; i < mBleDevices.size(); i++) {
            BlueModel temp = mBleDevices.get(i);
            if (temp.getDevice().getAddress().equalsIgnoreCase(addr)) {
                return temp.getGatt();
            }
        }
        return null;
    }

    private final BroadcastReceiver mPairingRequestBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            final BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

            if (getGattByDeviceAddr(device.getAddress()) == null) {
                return;
            }
        }
    };

    public BleManager(final Context context) {
        mContext = context;
        mHandler = new Handler();
    }

    /**
     * Returns the context that the manager was created with.
     *
     * @return the context
     */
    protected Context getContext() {
        return mContext;
    }

    /**
     * This method must return the gatt callback used by the manager.
     * This method must not create a new gatt callback each time it is being invoked, but rather
     * return a single object.
     *
     * @return the gatt callback object
     */
    protected abstract BleManagerGattCallback getGattCallback(String addr);

    protected abstract BleManagerGattCallback findGattCallback(String addr);

    /**
     * Returns whether to connect to the remote device just once (false) or to add the address to
     * white list of devices
     * that will be automatically connect as soon as they become available (true). In the latter
     * case, if
     * Bluetooth adapter is enabled, Android scans periodically for devices from the white list
     * and if a advertising packet
     * is received from such, it tries to connect to it. When the connection is lost, the system
     * will keep trying to reconnect
     * to it in. If true is returned, and the connection to the device is lost the
     * {@link BleManagerCallbacks#onLinklossOccur(BluetoothDevice)}
     * callback is called instead of
     * {@link BleManagerCallbacks#onDeviceDisconnected(BluetoothDevice)}.
     * <p>This feature works much better on newer Android phone models and many not work on older
     * phones.</p>
     * <p>This method should only be used with bonded devices, as otherwise the device may change
     * it's address.
     * It will however work also with non-bonded devices with private static address. A connection
     * attempt to
     * a device with private resolvable address will fail.</p>
     * <p>The first connection to a device will always be created with autoConnect flag to false
     * (see {@link BluetoothDevice#connectGatt(Context, boolean, BluetoothGattCallback)}). This is
     * to make it quick as the
     * user most probably waits for a quick response. However, if this method returned true during
     * first connection and the link was lost,
     * the manager will try to reconnect to it using {@link BluetoothGatt#connect()} which forces
     * autoConnect to true .</p>
     *
     * @return autoConnect flag value
     */
    protected boolean shouldAutoConnect() {
        return false;
    }

    protected void connect() {
        BluetoothDevice device = getConnectDevice();
        Log.d(TAG, "Start connect device device = " + device);
        if (device == null) {
            return;
        }
        BlueModel blueModel = getModelByDeviceAddr(device.getAddress());
//        if (connectDevice != null) {
//            device = connectDevice;
//        }
        if (device == null || !isDeviceNeedConnect(device)) {
            return;
        }
        if (!isInit) {
            // Register bonding broadcast receiver
            mContext.registerReceiver(mBluetoothStateBroadcastReceiver, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));
            mContext.registerReceiver(mBondingBroadcastReceiver, new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED));
            mContext.registerReceiver(mPairingRequestBroadcastReceiver, new IntentFilter("android" + ".bluetooth.device.action.PAIRING_REQUEST"/*BluetoothDevice
                        .ACTION_PAIRING_REQUEST*/));
            isInit = true;
        }
        Log.d(TAG, "connect device mac = " + device.getAddress());
        mCallbacks.onDeviceConnecting(device);
        BluetoothGatt bluetoothGatt = device.connectGatt(mContext, false, getGattCallback(device.getAddress()));
        Log.d("dddd", "connect " + device.getAddress());
        blueModel.setmConnectionState(BlueStatus.STATE_CONNECTING);
        blueModel.setStartTime(System.currentTimeMillis());
        blueModel.setTimes(blueModel.getTimes() + 1);
        EventBus.getDefault().post(new BleStatusChangeEvent());
        blueModel.setGatt(bluetoothGatt);
    }

    /**
     * Connects to the Bluetooth Smart device.
     *
     * @param device a device to connect to
     */
    public void connect(BluetoothDevice device) {
        Log.d(TAG, "Start connect device mac = " + device.getAddress());
        BlueModel blueModel = getModelByDeviceAddr(device.getAddress());
        Log.d(TAG, "Start connect device blueModel = " + blueModel);
        if (blueModel == null) {
            blueModel = new BlueModel(null, BlueStatus.STATE_INIT, device, System.currentTimeMillis());
            mBleDevices.add(blueModel);
            EventBus.getDefault().post(new BleStatusChangeEvent());
        }
        if (isHaveDeviceConnecting()) {
            return;
        }
        connect();

    }

    /**
     * Disconnects from the device or cancels the pending connection attempt. Does nothing if
     * device was not connected.
     *
     * @return true if device is to be disc onnected. False if it was already disconnected.
     */
    public boolean disconnect(String macAddr) {
        BlueModel model = getModelByDeviceAddr(macAddr);
        if (model != null) {
            BluetoothGatt bluetoothGatt = model.getGatt();
            model.setmConnectionState(BlueStatus.STATE_INIT);
            Log.d("dddd", "disconnect ");
            EventBus.getDefault().post(new BleStatusChangeEvent());
            Log.v(TAG, mConnected ? "Disconnecting..." : "Cancelling connection...");
            final boolean wasConnected = mConnected;
            if (bluetoothGatt != null) {
                mCallbacks.onDeviceDisconnecting(bluetoothGatt.getDevice());
                Log.d(TAG, "gatt.disconnect()");
                bluetoothGatt.disconnect();
            }

            if (!wasConnected) {
                // There will be no callback, the connection attempt will be stopped
                Log.i(TAG, "Disconnected");
                mCallbacks.onDeviceDisconnected(bluetoothGatt.getDevice());
            }
        }
        return true;
    }


    public void close() {
        try {
            if (isInit) {
                mContext.unregisterReceiver(mBluetoothStateBroadcastReceiver);
                mContext.unregisterReceiver(mBondingBroadcastReceiver);
                mContext.unregisterReceiver(mPairingRequestBroadcastReceiver);
                isInit = false;
            }
        } catch (Exception e) {
            Log.d(TAG, "e.message = " + e.getMessage());
        }
        synchronized (mLock) {
            mConnected = false;
//            mGattCallback = null;
            mBluetoothDevice = null;
            if (mBleDevices == null || mBleDevices.size() == 0) {
                EventBus.getDefault().post(new BleStatusChangeEvent());
                return;
            }
            for (int i = 0; i < mBleDevices.size(); i++) {
                BlueModel bluetoothGatt = mBleDevices.get(i);
                if (bluetoothGatt != null && bluetoothGatt.getGatt() != null) {
                    try {
                        bluetoothGatt.getGatt().close();
                    } catch (Exception e) {

                    }
                }
            }
            mBleDevices.clear();
            EventBus.getDefault().post(new BleStatusChangeEvent());
        }
    }


    /**
     * Sets the manager callback listener
     *
     * @param callbacks the callback listener
     */
    public void setGattCallbacks(E callbacks) {
        mCallbacks = callbacks;
    }


    /**
     * Creates a bond with the device. The device must be first set using
     * {@link #connect(BluetoothDevice)} which will
     * try to connect to the device. If you need to pair with a device before connecting to it you
     * may do it without
     * the use of BleManager object and connect after bond is established.
     *
     * @return true if pairing has started, false if it was already paired or an immediate error
     * occur.
     */
    private boolean internalCreateBond() {
        final BluetoothDevice device = mBluetoothDevice;
        if (device == null) return false;

        if (device.getBondState() == BluetoothDevice.BOND_BONDED) {
            Log.v(TAG, "Create bond request on already bonded device...");
            Log.i(TAG, "Device bonded");
            return false;
        }

        Log.v(TAG, "Starting pairing...");

        boolean result = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Log.d(TAG, "device.createBond()");
            result = device.createBond();
        } else {
            /*
             * There is a createBond() method in BluetoothDevice class but for now it's hidden. We
             * will call it using reflections. It has been revealed in KitKat (Api19)
             */
            try {
                final Method createBond = device.getClass().getMethod("createBond");
                if (createBond != null) {
                    Log.d(TAG, "device.createBond() (hidden)");
                    result = (Boolean) createBond.invoke(device);
                }
            } catch (final Exception e) {
                Log.w(TAG, "An exception occurred while creating bond", e);
            }
        }

        if (!result) Log.w(TAG, "Creating bond failed");
        return result;
    }

    /**
     * When the device is bonded and has the Generic Attribute service and the Service Changed
     * characteristic this method enables indications on this characteristic.
     * In case one of the requirements is not fulfilled this method returns <code>false</code>.
     *
     * @return <code>true</code> when the request has been sent, <code>false</code> when the
     * device is not bonded, does not have the Generic Attribute service, the GA service does not
     * have
     * the Service Changed characteristic or this characteristic does not have the CCCD.
     */
    private boolean ensureServiceChangedEnabled(BluetoothGatt gatt) {
        if (gatt == null) return false;

        // The Service Changed indications have sense only on bonded devices
        final BluetoothDevice device = gatt.getDevice();
        if (device.getBondState() != BluetoothDevice.BOND_BONDED) return false;

        final BluetoothGattService gaService = gatt.getService(GENERIC_ATTRIBUTE_SERVICE);
        if (gaService == null) return false;

        final BluetoothGattCharacteristic scCharacteristic = gaService.getCharacteristic(SERVICE_CHANGED_CHARACTERISTIC);
        if (scCharacteristic == null) return false;

        Log.i(TAG, "Service Changed characteristic found on a bonded device");
        return internalEnableIndications(scCharacteristic, gatt);
    }

    private boolean internalEnableNotifications(final BluetoothGattCharacteristic characteristic, BluetoothGatt gatt) {
        if (gatt == null || characteristic == null) return false;

        // Check characteristic property
        final int properties = characteristic.getProperties();
        if ((properties & BluetoothGattCharacteristic.PROPERTY_NOTIFY) == 0) return false;

        Log.d(TAG, "gatt.setCharacteristicNotification(" + characteristic.getUuid() + ", " + "true)");
        gatt.setCharacteristicNotification(characteristic, true);
        final BluetoothGattDescriptor descriptor = characteristic.getDescriptor(CLIENT_CHARACTERISTIC_CONFIG_DESCRIPTOR_UUID);
        if (descriptor != null) {
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            Log.v(TAG, "Enabling notifications for " + characteristic.getUuid());
            Log.d(TAG, "gatt.writeDescriptor(" + CLIENT_CHARACTERISTIC_CONFIG_DESCRIPTOR_UUID + ", value=0x01-00)");
            return internalWriteDescriptorWorkaround(descriptor, gatt);
        }
        return false;
    }

    private boolean internalDisableNotifications(final BluetoothGattCharacteristic characteristic, BluetoothGatt gatt) {
        if (gatt == null || characteristic == null) return false;

        // Check characteristic property
        final int properties = characteristic.getProperties();
        if ((properties & BluetoothGattCharacteristic.PROPERTY_NOTIFY) == 0) return false;

        Log.d(TAG, "gatt.setCharacteristicNotification(" + characteristic.getUuid() + ", " + "false)");
        gatt.setCharacteristicNotification(characteristic, false);
        final BluetoothGattDescriptor descriptor = characteristic.getDescriptor(CLIENT_CHARACTERISTIC_CONFIG_DESCRIPTOR_UUID);
        if (descriptor != null) {
            descriptor.setValue(BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);
            Log.v(TAG, "Disabling notifications for " + characteristic.getUuid());
            Log.d(TAG, "gatt.writeDescriptor(" + CLIENT_CHARACTERISTIC_CONFIG_DESCRIPTOR_UUID + ", value=0x00-00)");
            return internalWriteDescriptorWorkaround(descriptor, gatt);
        }
        return false;
    }

    private boolean internalEnableIndications(final BluetoothGattCharacteristic characteristic, BluetoothGatt gatt) {
        if (gatt == null || characteristic == null) return false;

        // Check characteristic property
        final int properties = characteristic.getProperties();
        if ((properties & BluetoothGattCharacteristic.PROPERTY_INDICATE) == 0) return false;

        Log.d(TAG, "gatt.setCharacteristicNotification(" + characteristic.getUuid() + ", " + "true)");
        gatt.setCharacteristicNotification(characteristic, true);
        final BluetoothGattDescriptor descriptor = characteristic.getDescriptor(CLIENT_CHARACTERISTIC_CONFIG_DESCRIPTOR_UUID);
        if (descriptor != null) {
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_INDICATION_VALUE);
            Log.v(TAG, "Enabling indications for " + characteristic.getUuid());
            Log.d(TAG, "gatt.writeDescriptor(" + CLIENT_CHARACTERISTIC_CONFIG_DESCRIPTOR_UUID + ", value=0x02-00)");
            return internalWriteDescriptorWorkaround(descriptor, gatt);
        }
        return false;
    }


    private boolean internalDisableIndications(final BluetoothGattCharacteristic characteristic, BluetoothGatt gatt) {
        // This writes exactly the same settings so do not duplicate code
        return internalDisableNotifications(characteristic, gatt);
    }


    private boolean internalReadCharacteristic(final BluetoothGattCharacteristic characteristic, BluetoothGatt gatt) {
        if (gatt == null || characteristic == null) return false;

        // Check characteristic property
        final int properties = characteristic.getProperties();
        if ((properties & BluetoothGattCharacteristic.PROPERTY_READ) == 0) return false;

        Log.v(TAG, "Reading characteristic " + characteristic.getUuid());
        Log.d(TAG, "gatt.readCharacteristic(" + characteristic.getUuid() + ")");
        return gatt.readCharacteristic(characteristic);
    }

    private boolean internalWriteCharacteristic(final BluetoothGattCharacteristic characteristic, BluetoothGatt gatt) {
        if (gatt == null || characteristic == null) return false;

        // Check characteristic property
        final int properties = characteristic.getProperties();
        if ((properties & (BluetoothGattCharacteristic.PROPERTY_WRITE | BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE)) == 0) return false;

        Log.v(TAG, "Writing characteristic " + characteristic.getUuid() + " (" + getWriteType(characteristic.getWriteType()) + ")");
        Log.d(TAG, "gatt.writeCharacteristic(" + characteristic.getUuid() + ")");
        return gatt.writeCharacteristic(characteristic);
    }


    private boolean internalReadDescriptor(final BluetoothGattDescriptor descriptor, BluetoothGatt gatt) {
        if (gatt == null || descriptor == null) return false;

        Log.v(TAG, "Reading descriptor " + descriptor.getUuid());
        Log.d(TAG, "gatt.readDescriptor(" + descriptor.getUuid() + ")");
        return gatt.readDescriptor(descriptor);
    }


    private boolean internalWriteDescriptor(final BluetoothGattDescriptor descriptor, BluetoothGatt gatt) {
        if (gatt == null || descriptor == null) return false;

        Log.v(TAG, "Writing descriptor " + descriptor.getUuid());
        Log.d(TAG, "gatt.writeDescriptor(" + descriptor.getUuid() + ")");
        return internalWriteDescriptorWorkaround(descriptor, gatt);
    }

    private boolean internalReadBatteryLevel(BluetoothGatt gatt) {
        if (gatt == null) return false;

        final BluetoothGattService batteryService = gatt.getService(BATTERY_SERVICE);
        if (batteryService == null) return false;

        final BluetoothGattCharacteristic batteryLevelCharacteristic = batteryService.getCharacteristic(BATTERY_LEVEL_CHARACTERISTIC);
        if (batteryLevelCharacteristic == null) return false;

        // Check characteristic property
        final int properties = batteryLevelCharacteristic.getProperties();
        if ((properties & BluetoothGattCharacteristic.PROPERTY_READ) == 0) return false;

        Log.d(TAG, "Reading battery level...");
        return internalReadCharacteristic(batteryLevelCharacteristic, gatt);
    }

    private boolean internalSetBatteryNotifications(final boolean enable, BluetoothGatt gatt) {
        if (gatt == null) {
            return false;
        }

        final BluetoothGattService batteryService = gatt.getService(BATTERY_SERVICE);
        if (batteryService == null) return false;

        final BluetoothGattCharacteristic batteryLevelCharacteristic = batteryService.getCharacteristic(BATTERY_LEVEL_CHARACTERISTIC);
        if (batteryLevelCharacteristic == null) return false;

        // Check characteristic property
        final int properties = batteryLevelCharacteristic.getProperties();
        if ((properties & BluetoothGattCharacteristic.PROPERTY_NOTIFY) == 0) return false;

        gatt.setCharacteristicNotification(batteryLevelCharacteristic, enable);
        final BluetoothGattDescriptor descriptor = batteryLevelCharacteristic.getDescriptor(CLIENT_CHARACTERISTIC_CONFIG_DESCRIPTOR_UUID);
        if (descriptor != null) {
            if (enable) {
                descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                Log.d(TAG, "Enabling battery level notifications...");
                Log.v(TAG, "Enabling notifications for " + BATTERY_LEVEL_CHARACTERISTIC);
                Log.d(TAG, "gatt.writeDescriptor(" + CLIENT_CHARACTERISTIC_CONFIG_DESCRIPTOR_UUID + ", value=0x0100)");
            } else {
                descriptor.setValue(BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);
                Log.d(TAG, "Disabling battery level notifications...");
                Log.v(TAG, "Disabling notifications for " + BATTERY_LEVEL_CHARACTERISTIC);
                Log.d(TAG, "gatt.writeDescriptor(" + CLIENT_CHARACTERISTIC_CONFIG_DESCRIPTOR_UUID + ", value=0x0000)");
            }
            return internalWriteDescriptorWorkaround(descriptor, gatt);
        }
        return false;
    }

    /**
     * There was a bug in Android up to 6.0 where the descriptor was written using parent
     * characteristic's write type, instead of always Write With Response, as the spec says.
     * <p>
     * See:
     * <a href="https://android.googlesource.com/platform/frameworks/base/+/942aebc95924ab1e7ea1e92aaf4e7fc45f695a6c%5E%21/#F0">
     * https://android.googlesource
     * .com/platform/frameworks/base/+/942aebc95924ab1e7ea1e92aaf4e7fc45f695a6c%5E%21/#F0</a>
     * </p>
     *
     * @param descriptor the descriptor to be written
     * @return the result of {@link BluetoothGatt#writeDescriptor(BluetoothGattDescriptor)}
     */
    private boolean internalWriteDescriptorWorkaround(final BluetoothGattDescriptor descriptor, BluetoothGatt gatt) {
        if (gatt == null || descriptor == null) return false;

        final BluetoothGattCharacteristic parentCharacteristic = descriptor.getCharacteristic();
        final int originalWriteType = parentCharacteristic.getWriteType();
        parentCharacteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
        final boolean result = gatt.writeDescriptor(descriptor);
        parentCharacteristic.setWriteType(originalWriteType);
        return result;
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private boolean internalRequestMtu(final int mtu, BluetoothGatt gatt) {
        if (gatt == null) return false;

        Log.v(TAG, "Requesting new MTU...");
        Log.d(TAG, "gatt.requestMtu(" + mtu + ")");
        return gatt.requestMtu(mtu);
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private boolean internalRequestConnectionPriority(final int priority, BluetoothGatt gatt) {
        if (gatt == null) return false;

        String text, priorityText;
        switch (priority) {
            case BluetoothGatt.CONNECTION_PRIORITY_HIGH:
                text = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? "HIGH (11.25–15ms, 0, " + "20s)" : "HIGH (7.5–10ms, 0, 20s)";
                priorityText = "HIGH";
                break;
            case BluetoothGatt.CONNECTION_PRIORITY_LOW_POWER:
                text = "BALANCED (30–50ms, 0, 20s)";
                priorityText = "LOW POWER";
                break;
            default:
            case BluetoothGatt.CONNECTION_PRIORITY_BALANCED:
                text = "LOW POWER (100–125ms, 2, 20s)";
                priorityText = "BALANCED";
                break;
        }
        Log.v(TAG, "Requesting connection priority: " + text + "...");
        Log.d(TAG, "gatt.requestConnectionPriority(" + priorityText + ")");
        return gatt.requestConnectionPriority(priority);
    }

    /**
     * Enqueues a new request. The request will be handled immediately if there is no operation
     * in progress,
     * or automatically after the last enqueued one will finish.
     * <p>This method should be used to read and write data from the target device as it ensures
     * that the last operation has finished
     * before a new one will be called.</p>
     *
     * @param request new request to be added to the queue.
     * @return true if request has been enqueued, false if the {@link #connect(BluetoothDevice)}
     * method was not called before,
     * or the manager was closed using {@link #close()}.
     */
    public boolean enqueue(final Request request) {
        if (request != null && request.gatt != null && request.gatt.getDevice() != null) {
            BleManagerGattCallback gatt = findGattCallback(request.gatt.getDevice().getAddress());
            if (gatt != null) {
                // Add the new task to the end of the queue
                gatt.mTaskQueue.add(request);
                gatt.nextRequest();
                return true;
            }
        }
        return false;
    }

    /**
     * On Android, when multiple BLE operations needs to be done, it is required to wait for a
     * proper
     * {@link BluetoothGattCallback BluetoothGattCallback} callback before calling
     * another operation. In order to make BLE operations easier the BleManager allows to enqueue
     * a request
     * containing all data necessary for a given operation. Requests are performed one after
     * another until the
     * queue is empty. Use static methods from below to instantiate a request and then enqueue
     * them using {@link #enqueue(Request)}.
     */
    protected static final class Request {
        private enum Type {
            CREATE_BOND, WRITE, READ, WRITE_DESCRIPTOR, READ_DESCRIPTOR, ENABLE_NOTIFICATIONS, ENABLE_INDICATIONS, DISABLE_NOTIFICATIONS, DISABLE_INDICATIONS, READ_BATTERY_LEVEL, ENABLE_BATTERY_LEVEL_NOTIFICATIONS, DISABLE_BATTERY_LEVEL_NOTIFICATIONS, ENABLE_SERVICE_CHANGED_INDICATIONS, REQUEST_MTU, REQUEST_CONNECTION_PRIORITY,
        }

        private final Type type;
        private final BluetoothGattCharacteristic characteristic;
        private final BluetoothGattDescriptor descriptor;
        private final byte[] data;
        private final int writeType;
        private final int value;
        private BluetoothGatt gatt;

        private Request(final Type type) {
            this.type = type;
            this.characteristic = null;
            this.descriptor = null;
            this.data = null;
            this.writeType = 0;
            this.value = 0;
        }

        private Request(final Type type, BluetoothGatt gatt) {
            this.type = type;
            this.characteristic = null;
            this.descriptor = null;
            this.data = null;
            this.writeType = 0;
            this.value = 0;
            this.gatt = gatt;
        }

        private Request(final Type type, final BluetoothGattCharacteristic characteristic, BluetoothGatt gatt) {
            this.type = type;
            this.characteristic = characteristic;
            this.descriptor = null;
            this.data = null;
            this.writeType = 0;
            this.value = 0;
            this.gatt = gatt;
        }

        private Request(final Type type, final BluetoothGattCharacteristic characteristic, final int writeType, final byte[] data, final int offset, final int length, BluetoothGatt device) {
            this.type = type;
            this.characteristic = characteristic;
            this.descriptor = null;
            this.data = copy(data, offset, length);
            this.writeType = writeType;
            this.value = 0;
            this.gatt = device;
        }

        private static byte[] copy(final byte[] value, final int offset, final int length) {
            if (value == null || offset > value.length) return null;
            final int maxLength = Math.min(value.length - offset, length);
            final byte[] copy = new byte[maxLength];
            System.arraycopy(value, offset, copy, 0, maxLength);
            return copy;
        }

        /**
         * Creates new Write Characteristic request. The request will not be executed if given
         * characteristic
         * is null or does not have WRITE property. After the operation is complete a proper
         * callback will be invoked.
         *
         * @param characteristic characteristic to be written
         * @param value          value to be written. The array is copied into another buffer so
         *                       it's safe to reuse the array again.
         * @param offset         the offset from which value has to be copied
         * @param length         number of bytes to be copied from the value buffer
         * @return the new request that can be enqueued using {@link #enqueue(Request)} method.
         */
        public static Request newWriteRequest(final BluetoothGattCharacteristic characteristic, final byte[] value, final int offset, final int length, BluetoothGatt gatt) {
            return new Request(Type.WRITE, characteristic, characteristic.getWriteType(), value, offset, length, gatt);
        }

        /**
         * Reads the first found Battery Level characteristic value from the first found Battery
         * Service.
         * If any of them is not found, or the characteristic does not have the READ property
         * this operation will not execute.
         *
         * @return the new request that can be enqueued using {@link #enqueue(Request)} method.
         */
        static Request newReadBatteryLevelRequest(BluetoothGatt gatt) {
            return new Request(Type.READ_BATTERY_LEVEL, gatt); // the first Battery Level char from
            // the
            // first Battery Service is used
        }

        /**
         * Enables notifications on the first found Battery Level characteristic from the first
         * found Battery Service.
         * If any of them is not found, or the characteristic does not have the NOTIFY property
         * this operation will not execute.
         *
         * @return the new request that can be enqueued using {@link #enqueue(Request)} method.
         */
        static Request newEnableBatteryLevelNotificationsRequest(BluetoothGatt gatt) {
            return new Request(Type.ENABLE_BATTERY_LEVEL_NOTIFICATIONS, gatt); // the first Battery
            // Level char from the first Battery Service is used
        }

        /**
         * Creates new Enable Notification request. The request will not be executed if given
         * characteristic
         * is null, does not have NOTIFY property or the CCCD. After the operation is complete a
         * proper callback will be invoked.
         *
         * @param characteristic characteristic to have notifications enabled
         * @return the new request that can be enqueued using {@link #enqueue(Request)} method.
         */
        public static Request newEnableNotificationsRequest(final BluetoothGattCharacteristic characteristic, BluetoothGatt gatt) {
            return new Request(Type.ENABLE_NOTIFICATIONS, characteristic, gatt);
        }

        /**
         * Enables indications on Service Changed characteristic if such exists in the Generic
         * Attribute service.
         * It is required to enable those notifications on bonded devices on older Android
         * versions to be
         * informed about attributes changes. Android 7+ (or 6+) handles this automatically and
         * no action is required.
         *
         * @return the new request that can be enqueued using {@link #enqueue(Request)} method.
         */
        private static Request newEnableServiceChangedIndicationsRequest(BluetoothGatt gatt) {
            return new Request(Type.ENABLE_SERVICE_CHANGED_INDICATIONS, gatt); // the only Service
            // Changed char is used (if such exists)
        }

    }

    protected abstract class BleManagerGattCallback extends BluetoothGattCallback {
        private final static String ERROR_CONNECTION_STATE_CHANGE = "Error on connection state " + "change";
        private final static String ERROR_DISCOVERY_SERVICE = "Error on discovering services";
        private final static String ERROR_AUTH_ERROR_WHILE_BONDED = "Phone has lost bonding " + "information";
        private final static String ERROR_READ_CHARACTERISTIC = "Error on reading characteristic";
        private final static String ERROR_WRITE_CHARACTERISTIC = "Error on writing characteristic";
        private final static String ERROR_READ_DESCRIPTOR = "Error on reading descriptor";
        private final static String ERROR_WRITE_DESCRIPTOR = "Error on writing descriptor";
        private final static String ERROR_MTU_REQUEST = "Error on mtu request";

        private final Queue<Request> mTaskQueue = new LinkedList<>();
        private Deque<Request> mInitQueue;
        private boolean mInitInProgress;
        private boolean mOperationInProgress = true; // Initially true to block operations before

        /**
         * This method should return <code>true</code> when the gatt device supports the required
         * services.
         *
         * @param gatt the gatt device with services discovered
         * @return <code>true</code> when the device has teh required service
         */
        protected abstract boolean isRequiredServiceSupported(final BluetoothGatt gatt);

        /**
         * This method should return <code>true</code> when the gatt device supports the optional
         * services.
         * The default implementation returns <code>false</code>.
         *
         * @return <code>true</code> when the device has teh optional service
         */
        private boolean isOptionalServiceSupported() {
            return false;
        }

        /**
         * This method should return a list of requests needed to initialize the profile.
         * Enabling Service Change indications for bonded devices and reading the Battery Level
         * value and enabling Battery Level notifications
         * is handled before executing this queue. The queue should not have requests that are not
         * available, e.g. should not
         * read an optional service when it is not supported by the connected device.
         * <p>This method is called when the services has been discovered and the device is
         * supported (has required service).</p>
         *
         * @param gatt the gatt device with services discovered
         * @return the queue of requests
         */
        protected abstract Deque<Request> initGatt(final BluetoothGatt gatt);

        /**
         * Called then the initialization queue is complete.
         */
        void onDeviceReady() {
//            mCallbacks.onDeviceReady(mBluetoothGatt.getDevice());
        }

        /**
         * This method should nullify all services and characteristics of the device.
         * It's called when the device is no longer connected, either due to user action
         * or a link loss.
         */
        protected abstract void onDeviceDisconnected(final BluetoothDevice device);

        private void notifyDeviceDisconnected(final BluetoothDevice device) {
            if (device != null) {
                BlueModel model = getModelByDeviceAddr(device.getAddress());
                if (model != null) {
                    model.setmConnectionState(BlueStatus.STATE_INIT);
                    Log.d("dddd", "notifyDeviceDisconnected " + device.getAddress());
                    mBleDevices.remove(model);
                    mCallbacks.onDeviceDisconnected(device);
                    EventBus.getDefault().post(new BleStatusChangeEvent());
                    onDeviceDisconnected(device);
                } else {
                    Log.i(TAG, device.getAddress() + " is not exist");
                }
            }
        }

        private void notifyAllDeviceDisconnected() {
            close();
        }

        /**
         * Callback reporting the result of a characteristic read operation.
         *
         * @param gatt           GATT client
         * @param characteristic Characteristic that was read from the associated remote device.
         */
        void onCharacteristicRead(final BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic) {
            Log.w(TAG, "gatt = " + gatt + ",characteristic =" + characteristic);
        }

        /**
         * Callback indicating the result of a characteristic write operation.
         * <p>If this callback is invoked while a reliable write transaction is
         * in progress, the value of the characteristic represents the value
         * reported by the remote device. An application should compare this
         * value to the desired value to be written. If the values don't match,
         * the application must abort the reliable write transaction.
         *
         * @param gatt           GATT client
         * @param characteristic Characteristic that was written to the associated remote device.
         */
        protected void onCharacteristicWrite(final BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic) {
            Log.w(TAG, "gatt = " + gatt + ",characteristic =" + characteristic);
        }

        /**
         * Callback reporting the result of a descriptor read operation.
         *
         * @param gatt       GATT client
         * @param descriptor Descriptor that was read from the associated remote device.
         */
        void onDescriptorRead(final BluetoothGatt gatt, final BluetoothGattDescriptor descriptor) {
            Log.w(TAG, "gatt = " + gatt + ",descriptor =" + descriptor);
        }

        /**
         * Callback indicating the result of a descriptor write operation.
         * <p>If this callback is invoked while a reliable write transaction is in progress,
         * the value of the characteristic represents the value reported by the remote device.
         * An application should compare this value to the desired value to be written.
         * If the values don't match, the application must abort the reliable write transaction.
         *
         * @param gatt       GATT client
         * @param descriptor Descriptor that was written to the associated remote device.
         */
        void onDescriptorWrite(final BluetoothGatt gatt, final BluetoothGattDescriptor descriptor) {
            Log.d(TAG, "gatt = " + gatt + ",descriptor = " + descriptor);
        }

        /**
         * Callback reporting the value of Battery Level characteristic which could have
         * been received by Read or Notify operations.
         *
         * @param gatt  GATT client
         * @param value the battery value in percent
         */
        void onBatteryValueReceived(final BluetoothGatt gatt, final int value) {
            Log.d(TAG, "gatt = " + gatt + ",value = " + value);
        }

        /**
         * Callback indicating a notification has been received.
         *
         * @param gatt           GATT client
         * @param characteristic Characteristic from which the notification came.
         */
        protected void onCharacteristicNotified(final BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic) {
        }

        /**
         * Method called when the MTU request has finished with success. The MTU value may
         * be different than requested one.
         *
         * @param mtu the new MTU (Maximum Transfer Unit)
         */
        void onMtuChanged(final int mtu) {
            Log.d(TAG, "mtu = " + mtu);
        }


        private void onError(final BluetoothDevice device, final String message, final int errorCode) {
            Log.e(TAG, "Error (0x" + Integer.toHexString(errorCode) + "): " + GattError.parse(errorCode));
            mCallbacks.onError(device, message, errorCode);
        }

        @Override
        public final void onConnectionStateChange(final BluetoothGatt gatt, final int status, final int newState) {
            Log.d(TAG, "[Callback] Connection state changed with status: " + status + " and " + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "new " + "" + "" + "state: " + "" + "" + "" + newState + " " + "" + "(" + stateToString(newState) + ") addr = " + gatt.getDevice().getAddress());

            if (status == BluetoothGatt.GATT_SUCCESS && newState == BluetoothProfile.STATE_CONNECTED) {
                // Notify the parent activity/service
                Log.i(TAG, "Connected to " + gatt.getDevice().getAddress());
                mConnected = true;
                mCallbacks.onDeviceConnected(gatt.getDevice());
                int status2 = gatt.getDevice().getBondState();
                final boolean bonded = gatt.getDevice().getBondState() == BluetoothDevice.BOND_BONDED;
                final int delay = bonded ? 1600 : 0; // around 1600 ms is required when
                // connection interval is ~45ms.
                if (delay > 0) Log.d(TAG, "wait(" + delay + ")");
                mHandler.postDelayed(() -> {
                    if (gatt.getDevice().getBondState() != BluetoothDevice.BOND_BONDING) {
                        Log.v(TAG, "Discovering Services...");
                        Log.d(TAG, "gatt.discoverServices()");
                        gatt.discoverServices();
                    }
                }, delay);
            } else {
                if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                    if (status != BluetoothGatt.GATT_SUCCESS) Log.w(TAG, "Error: (0x" + Integer.toHexString(status) + "): " + GattError.parseConnectionError(status));

                    mOperationInProgress = true; // no more calls are possible
                    mInitQueue = null;
                    mTaskQueue.clear();
                    final boolean wasConnected = mConnected;
                    notifyDeviceDisconnected(gatt.getDevice()); // This sets the mConnected flag
                    if (wasConnected || status == BluetoothGatt.GATT_SUCCESS) return;
                } else {
                    if (status != BluetoothGatt.GATT_SUCCESS) Log.e(TAG, "Error (0x" + Integer.toHexString(status) + "): " + GattError.parseConnectionError(status));
                }
                mCallbacks.onError(gatt.getDevice(), ERROR_CONNECTION_STATE_CHANGE, status);
            }
        }

        @Override
        public final void onServicesDiscovered(final BluetoothGatt gatt, final int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.i(TAG, "Services Discovered");
                BlueModel model = getModelByDeviceAddr(gatt.getDevice().getAddress());
                model.setmConnectionState(BlueStatus.STATE_CONNECTED);
                EventBus.getDefault().post(new BleStatusChangeEvent());
                connect();
                if (isRequiredServiceSupported(gatt)) {
                    Log.v(TAG, "Primary service found");
                    final boolean optionalServicesFound = isOptionalServiceSupported();
                    if (optionalServicesFound) Log.v(TAG, "Secondary service found");

                    mCallbacks.onServicesDiscovered(gatt.getDevice(), optionalServicesFound);

                    mInitInProgress = true;
                    mInitQueue = initGatt(gatt);

                    if (mInitQueue == null) mInitQueue = new LinkedList<>();

                    if (mCallbacks.shouldEnableBatteryLevelNotifications(gatt.getDevice())) mInitQueue.addFirst(Request.newEnableBatteryLevelNotificationsRequest(gatt));
                    mInitQueue.addFirst(Request.newReadBatteryLevelRequest(gatt));
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) mInitQueue.addFirst(Request.newEnableServiceChangedIndicationsRequest(gatt));

                    mOperationInProgress = false;
                    nextRequest();
                    mCallbacks.onReady(gatt);

                } else {
                    Log.w(TAG, "Device is not supported");
                    mCallbacks.onDeviceNotSupported(gatt.getDevice());
                    disconnect(gatt.getDevice().getAddress());
                }
            } else {
                Log.e(TAG, "onServicesDiscovered error " + status);
                onError(gatt.getDevice(), ERROR_DISCOVERY_SERVICE, status);
            }
        }

        @Override
        public final void onCharacteristicRead(final BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic, final int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.i(TAG, "Read Response received from " + characteristic.getUuid() + ", " + "value: " + ParserUtils.parse(characteristic));

                if (isBatteryLevelCharacteristic(characteristic)) {
                    final int batteryValue = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 0);
                    Log.d(TAG, "Battery level received: " + batteryValue + "%");
                    onBatteryValueReceived(gatt, batteryValue);
                    mCallbacks.onBatteryValueReceived(gatt.getDevice(), batteryValue);
                } else {
                    // The value has been read. Notify the manager and proceed with the
                    // initialization queue.
                    onCharacteristicRead(gatt, characteristic);
                }
            } else if (status == BluetoothGatt.GATT_INSUFFICIENT_AUTHENTICATION) {
                if (gatt.getDevice().getBondState() != BluetoothDevice.BOND_NONE) {
                    // This should never happen but it used to: http://stackoverflow
                    // .com/a/20093695/2115352
                    Log.w(TAG, ERROR_AUTH_ERROR_WHILE_BONDED);
                    mCallbacks.onError(gatt.getDevice(), ERROR_AUTH_ERROR_WHILE_BONDED, status);
                }
            } else {
                Log.e(TAG, "onCharacteristicRead error " + status);
                onError(gatt.getDevice(), ERROR_READ_CHARACTERISTIC, status);
            }
            mOperationInProgress = false;
            nextRequest();
        }

        @Override
        public final void onCharacteristicWrite(final BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic, final int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                // The value has been written. Notify the manager and proceed with the
                // initialization queue.
                onCharacteristicWrite(gatt, characteristic);
            } else if (status == BluetoothGatt.GATT_INSUFFICIENT_AUTHENTICATION) {
                if (gatt.getDevice().getBondState() != BluetoothDevice.BOND_NONE) {
                    // This should never happen but it used to: http://stackoverflow
                    // .com/a/20093695/2115352
                    Log.w(TAG, ERROR_AUTH_ERROR_WHILE_BONDED);
                    mCallbacks.onError(gatt.getDevice(), ERROR_AUTH_ERROR_WHILE_BONDED, status);
                }
            } else {
                Log.e(TAG, "onCharacteristicWrite error " + status);
                onError(gatt.getDevice(), ERROR_WRITE_CHARACTERISTIC, status);
            }
            mOperationInProgress = false;
            nextRequest();
        }

        @Override
        public void onDescriptorRead(final BluetoothGatt gatt, final BluetoothGattDescriptor descriptor, final int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.i(TAG, "Read Response received from descr. " + descriptor.getUuid() + "," + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + " " + "value:" + "" + "" + " " + ParserUtils.parse(descriptor));

                // The value has been read. Notify the manager and proceed with the
                // initialization queue.
                onDescriptorRead(gatt, descriptor);
            } else if (status == BluetoothGatt.GATT_INSUFFICIENT_AUTHENTICATION) {
                if (gatt.getDevice().getBondState() != BluetoothDevice.BOND_NONE) {
                    // This should never happen but it used to: http://stackoverflow
                    // .com/a/20093695/2115352
                    Log.w(TAG, ERROR_AUTH_ERROR_WHILE_BONDED);
                    mCallbacks.onError(gatt.getDevice(), ERROR_AUTH_ERROR_WHILE_BONDED, status);
                }
            } else {
                Log.e(TAG, "onDescriptorRead error " + status);
                onError(gatt.getDevice(), ERROR_READ_DESCRIPTOR, status);
            }
            mOperationInProgress = false;
            nextRequest();
        }

        @Override
        public final void onDescriptorWrite(final BluetoothGatt gatt, final BluetoothGattDescriptor descriptor, final int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.i(TAG, "Data written to descr. " + descriptor.getUuid() + ", value: " + ParserUtils.parse(descriptor));

                if (isServiceChangedCCCD(descriptor)) {
                    Log.d(TAG, "Service Changed notifications enabled");
                } else if (isBatteryLevelCCCD(descriptor)) {
                    final byte[] value = descriptor.getValue();
                    if (value != null && value.length == 2 && value[1] == 0x00) {
                        if (value[0] == 0x01) {
                            Log.d(TAG, "Battery Level notifications enabled");
                        } else {
                            Log.d(TAG, "Battery Level notifications disabled");
                        }
                    } else {
                        onDescriptorWrite(gatt, descriptor);
                    }
                } else if (isCCCD(descriptor)) {
                    final byte[] value = descriptor.getValue();
                    if (value != null && value.length == 2 && value[1] == 0x00) {
                        switch (value[0]) {
                            case 0x00:
                                Log.d(TAG, "Notifications and indications disabled");
                                break;
                            case 0x01:
                                Log.d(TAG, "Notifications enabled");
                                break;
                            case 0x02:
                                Log.d(TAG, "Indications enabled");
                                break;
                        }
                    } else {
                        onDescriptorWrite(gatt, descriptor);
                    }
                } else {
                    onDescriptorWrite(gatt, descriptor);
                }
            } else if (status == BluetoothGatt.GATT_INSUFFICIENT_AUTHENTICATION) {
                if (gatt.getDevice().getBondState() != BluetoothDevice.BOND_NONE) {
                    // This should never happen but it used to: http://stackoverflow
                    // .com/a/20093695/2115352
                    Log.w(TAG, ERROR_AUTH_ERROR_WHILE_BONDED);
                    mCallbacks.onError(gatt.getDevice(), ERROR_AUTH_ERROR_WHILE_BONDED, status);
                }
            } else {
                Log.e(TAG, "onDescriptorWrite error " + status);
                onError(gatt.getDevice(), ERROR_WRITE_DESCRIPTOR, status);
            }
            mOperationInProgress = false;
            nextRequest();
        }

        @Override
        public final void onCharacteristicChanged(final BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic) {
            final String data = ParserUtils.parse(characteristic);

            if (isBatteryLevelCharacteristic(characteristic)) {
                Log.i(TAG, "Notification received from " + characteristic.getUuid() + ", " + "value: " + data);
                final int batteryValue = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 0);
                Log.d(TAG, "Battery level received: " + batteryValue + "%");
                onBatteryValueReceived(gatt, batteryValue);
                mCallbacks.onBatteryValueReceived(gatt.getDevice(), batteryValue);
            } else {
                final BluetoothGattDescriptor cccd = characteristic.getDescriptor(CLIENT_CHARACTERISTIC_CONFIG_DESCRIPTOR_UUID);
                final boolean notifications = cccd == null || cccd.getValue() == null || cccd.getValue().length != 2 || cccd.getValue()[0] == 0x01;

                if (notifications) {
                    Log.i(TAG, "Notification received from " + characteristic.getUuid() + "," + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + " " + "value: " + "" + "" + "" + "" + "" + data);
                    onCharacteristicNotified(gatt, characteristic);
                } else { // indications
                    Log.i(TAG, "Indication received from " + characteristic.getUuid() + ", " + "value: " + data);
                }
            }
        }

        @Override
        public void onMtuChanged(final BluetoothGatt gatt, final int mtu, final int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.i(TAG, "MTU changed to: " + mtu);
                onMtuChanged(mtu);
            } else {
                Log.e(TAG, "onMtuChanged error: " + status + ", mtu: " + mtu);
                onError(gatt.getDevice(), ERROR_MTU_REQUEST, status);
            }
            mOperationInProgress = false;
            nextRequest();
        }


        /**
         * Executes the next request. If the last element from the initialization queue has been
         * executed
         * the {@link #onDeviceReady()} callback is called.
         */
        private void nextRequest() {
            if (mOperationInProgress) return;

            // Get the first request from the initDevice queue
            Request request = mInitQueue != null ? mInitQueue.poll() : null;

            // Are we done with initializing?
            if (request == null) {
                if (mInitInProgress) {
                    mInitQueue = null; // release the queue
                    mInitInProgress = false;
                    onDeviceReady();
                }
                // If so, we can continue with the task queue
                request = mTaskQueue.poll();
                if (request == null) {
                    // Nothing to be done for now
                    return;
                }
            }
            Log.i(TAG, "@@@ request deal ");
            mOperationInProgress = true;
            boolean result = false;
            switch (request.type) {
                case CREATE_BOND: {
                    result = internalCreateBond();
                    break;
                }
                case READ: {
                    result = internalReadCharacteristic(request.characteristic, request.gatt);
                    break;
                }
                case WRITE: {
                    final BluetoothGattCharacteristic characteristic = request.characteristic;
                    assert characteristic != null;
                    characteristic.setValue(request.data);
                    characteristic.setWriteType(request.writeType);
                    result = internalWriteCharacteristic(characteristic, request.gatt);
                    break;
                }
                case READ_DESCRIPTOR: {
                    result = internalReadDescriptor(request.descriptor, request.gatt);
                    break;
                }
                case WRITE_DESCRIPTOR: {
                    final BluetoothGattDescriptor descriptor = request.descriptor;
                    assert descriptor != null;
                    descriptor.setValue(request.data);
                    result = internalWriteDescriptor(descriptor, request.gatt);
                    break;
                }
                case ENABLE_NOTIFICATIONS: {
                    result = internalEnableNotifications(request.characteristic, request.gatt);
                    break;
                }
                case ENABLE_INDICATIONS: {
                    result = internalEnableIndications(request.characteristic, request.gatt);
                    break;
                }
                case DISABLE_NOTIFICATIONS: {
                    result = internalDisableNotifications(request.characteristic, request.gatt);
                    break;
                }
                case DISABLE_INDICATIONS: {
                    result = internalDisableIndications(request.characteristic, request.gatt);
                    break;
                }
                case READ_BATTERY_LEVEL: {
                    result = internalReadBatteryLevel(request.gatt);
                    break;
                }
                case ENABLE_BATTERY_LEVEL_NOTIFICATIONS: {
                    result = internalSetBatteryNotifications(true, request.gatt);
                    break;
                }
                case DISABLE_BATTERY_LEVEL_NOTIFICATIONS: {
                    result = internalSetBatteryNotifications(false, request.gatt);
                    break;
                }
                case ENABLE_SERVICE_CHANGED_INDICATIONS: {
                    result = ensureServiceChangedEnabled(request.gatt);
                    break;
                }
                case REQUEST_MTU: {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        result = internalRequestMtu(request.value, request.gatt);
                    }
                    break;
                }
                case REQUEST_CONNECTION_PRIORITY: {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        result = internalRequestConnectionPriority(request.value, request.gatt);
                    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        result = internalRequestConnectionPriority(request.value, request.gatt);
                        // There is no callback for requestConnectionPriority(...) before Android
                        // Oreo.\
                        // Let's give it some time to finish as the request is an asynchronous
                        // operation.
                        if (result) {
                            mHandler.postDelayed(() -> {
                                mOperationInProgress = false;
                                nextRequest();
                            }, 100);
                        }
                    }
                    break;
                }
            }
            // The result may be false if given characteristic or descriptor were not found on
            // the device,
            // or the feature is not supported on the Android.
            // In that case, proceed with next operation and ignore the one that failed.
            mOperationInProgress = false;
            nextRequest();
        }

        /**
         * Returns true if this descriptor is from the Service Changed characteristic.
         *
         * @param descriptor the descriptor to be checked
         * @return true if the descriptor belongs to the Service Changed characteristic
         */
        private boolean isServiceChangedCCCD(final BluetoothGattDescriptor descriptor) {
            return descriptor != null && SERVICE_CHANGED_CHARACTERISTIC.equals(descriptor.getCharacteristic().getUuid());

        }

        /**
         * Returns true if the characteristic is the Battery Level characteristic.
         *
         * @param characteristic the characteristic to be checked
         * @return true if the characteristic is the Battery Level characteristic.
         */
        private boolean isBatteryLevelCharacteristic(final BluetoothGattCharacteristic characteristic) {
            return characteristic != null && BATTERY_LEVEL_CHARACTERISTIC.equals(characteristic.getUuid());

        }

        /**
         * Returns true if this descriptor is from the Battery Level characteristic.
         *
         * @param descriptor the descriptor to be checked
         * @return true if the descriptor belongs to the Battery Level characteristic
         */
        private boolean isBatteryLevelCCCD(final BluetoothGattDescriptor descriptor) {
            return descriptor != null && BATTERY_LEVEL_CHARACTERISTIC.equals(descriptor.getCharacteristic().getUuid());

        }

        /**
         * Returns true if this descriptor is a Client Characteristic Configuration descriptor
         * (CCCD).
         *
         * @param descriptor the descriptor to be checked
         * @return true if the descriptor is a CCCD
         */
        private boolean isCCCD(final BluetoothGattDescriptor descriptor) {
            return descriptor != null && CLIENT_CHARACTERISTIC_CONFIG_DESCRIPTOR_UUID.equals(descriptor.getUuid());

        }
    }

    private static final int PAIRING_VARIANT_PIN = 0;
    private static final int PAIRING_VARIANT_PASSKEY = 1;
    private static final int PAIRING_VARIANT_PASSKEY_CONFIRMATION = 2;
    private static final int PAIRING_VARIANT_CONSENT = 3;
    private static final int PAIRING_VARIANT_DISPLAY_PASSKEY = 4;
    private static final int PAIRING_VARIANT_DISPLAY_PIN = 5;
    private static final int PAIRING_VARIANT_OOB_CONSENT = 6;

    private String pairingVariantToString(final int variant) {
        switch (variant) {
            case PAIRING_VARIANT_PIN:
                return "PAIRING_VARIANT_PIN";
            case PAIRING_VARIANT_PASSKEY:
                return "PAIRING_VARIANT_PASSKEY";
            case PAIRING_VARIANT_PASSKEY_CONFIRMATION:
                return "PAIRING_VARIANT_PASSKEY_CONFIRMATION";
            case PAIRING_VARIANT_CONSENT:
                return "PAIRING_VARIANT_CONSENT";
            case PAIRING_VARIANT_DISPLAY_PASSKEY:
                return "PAIRING_VARIANT_DISPLAY_PASSKEY";
            case PAIRING_VARIANT_DISPLAY_PIN:
                return "PAIRING_VARIANT_DISPLAY_PIN";
            case PAIRING_VARIANT_OOB_CONSENT:
                return "PAIRING_VARIANT_OOB_CONSENT";
            default:
                return "UNKNOWN";
        }
    }

    private String bondStateToString(final int state) {
        switch (state) {
            case BluetoothDevice.BOND_NONE:
                return "BOND_NONE";
            case BluetoothDevice.BOND_BONDING:
                return "BOND_BONDING";
            case BluetoothDevice.BOND_BONDED:
                return "BOND_BONDED";
            default:
                return "UNKNOWN";
        }
    }

    private String getWriteType(final int type) {
        switch (type) {
            case BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT:
                return "WRITE REQUEST";
            case BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE:
                return "WRITE COMMAND";
            case BluetoothGattCharacteristic.WRITE_TYPE_SIGNED:
                return "WRITE SIGNED";
            default:
                return "UNKNOWN: " + type;
        }
    }

    /**
     * Converts the connection state to String value
     *
     * @param state the connection state
     * @return state as String
     */
    private String stateToString(final int state) {
        switch (state) {
            case BluetoothProfile.STATE_CONNECTED:
                return "CONNECTED";
            case BluetoothProfile.STATE_CONNECTING:
                return "CONNECTING";
            case BluetoothProfile.STATE_DISCONNECTING:
                return "DISCONNECTING";
            default:
                return "DISCONNECTED";
        }
    }

    public List<BlueModel> getmBleDevices() {
        return mBleDevices;
    }

}
