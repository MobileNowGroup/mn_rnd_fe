package com.mobilenow.cyrcadia_itbra.ble.uart;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.text.TextUtils;

import com.blankj.utilcode.util.LogUtils;
import com.mobilenow.cyrcadia_itbra.BlueModel;
import com.mobilenow.cyrcadia_itbra.ble.base.BleManager;
import com.mobilenow.cyrcadia_itbra.util.ParserUtils;

import java.io.UnsupportedEncodingException;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.UUID;


public class UARTManager extends BleManager<UARTManagerCallbacks> {
    private static UARTManager instance;
    /**
     * Nordic UART Service UUID
     */
    private final static UUID UART_SERVICE_UUID = UUID.fromString
            ("6E400001-B5A3-F393-E0A9-E50E24DCCA9E");
    /**
     * RX characteristic UUID
     */
    private final static UUID UART_RX_CHARACTERISTIC_UUID = UUID.fromString
            ("6E400002-B5A3-F393-E0A9-E50E24DCCA9E");
    /**
     * TX characteristic UUID
     */
    private final static UUID UART_TX_CHARACTERISTIC_UUID = UUID.fromString
            ("6E400003-B5A3-F393-E0A9-E50E24DCCA9E");
    /**
     * The maximum packet size is 20 bytes.
     */
    private static final int MAX_PACKET_SIZE = 20;


    private HashMap<String, GattCallBack> mGattCallBackMap = new HashMap<>();

    public static UARTManager getInstance(Context context) {
        if (instance == null) {
            instance = new UARTManager(context);
        }
        return instance;
    }


    private UARTManager(final Context context) {
        super(context);
    }


    @Override
    protected BleManagerGattCallback getGattCallback(String addr) {
        GattCallBack callBack = new GattCallBack();
        mGattCallBackMap.put(addr, callBack);
        return callBack;
    }
    boolean isStart = true;
    public void start() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (isStart){
                    connect();
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

    }
    public void stop(){
        isStart=false;
    }

    /**
     * BluetoothGatt callbacks for connection/disconnection, service discovery, receiving
     * indication, etc
     */
    private class GattCallBack extends BleManagerGattCallback {
        private BluetoothGattCharacteristic mRXCharacteristic, mTXCharacteristic;
        private byte[] mOutgoingBuffer;
        private int mBufferOffset;

        @Override
        protected Deque<Request> initGatt(final BluetoothGatt gatt) {
            LogUtils.d("ddd initGatt");
            final LinkedList<Request> requests = new LinkedList<>();
            requests.add(Request.newEnableNotificationsRequest(mTXCharacteristic, gatt));
            return requests;
        }

        @Override
        public boolean isRequiredServiceSupported(final BluetoothGatt gatt) {
            LogUtils.d("ddd isRequiredServiceSupported ");
            final BluetoothGattService service = gatt.getService(UART_SERVICE_UUID);
            if (service != null) {
                mRXCharacteristic = service.getCharacteristic(UART_RX_CHARACTERISTIC_UUID);
                mTXCharacteristic = service.getCharacteristic(UART_TX_CHARACTERISTIC_UUID);
            }

            boolean writeRequest = false;
            boolean writeCommand = false;
            if (mRXCharacteristic != null) {
                final int rxProperties = mRXCharacteristic.getProperties();
                writeRequest = (rxProperties & BluetoothGattCharacteristic.PROPERTY_WRITE) > 0;
                writeCommand = (rxProperties & BluetoothGattCharacteristic
                        .PROPERTY_WRITE_NO_RESPONSE) > 0;

                // Set the WRITE REQUEST type when the characteristic supports it. This will
                // allow to send long write (also if the characteristic support it).
                // In case there is no WRITE REQUEST property, this manager will divide texts
                // longer then 20 bytes into up to 20 bytes chunks.
                if (writeRequest)
                    mRXCharacteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
            }
            return mRXCharacteristic != null && mTXCharacteristic != null && (writeRequest ||
                    writeCommand);
        }

        @Override
        protected void onDeviceDisconnected(final BluetoothDevice device) {
            mRXCharacteristic = null;
            mTXCharacteristic = null;
        }

        @Override
        public void onCharacteristicWrite(final BluetoothGatt gatt, final
        BluetoothGattCharacteristic characteristic) {
            // When the whole buffer has been sent
            final byte[] buffer = mOutgoingBuffer;
            if (mBufferOffset == buffer.length) {
                try {
                    final String data = new String(buffer, "UTF-8");
                    mCallbacks.onDataSent(gatt.getDevice(), data);
                } catch (final UnsupportedEncodingException e) {
                    // do nothing
                }
                mOutgoingBuffer = null;
            } else { // Otherwise...
                final int length = Math.min(buffer.length - mBufferOffset, MAX_PACKET_SIZE);
                enqueue(Request.newWriteRequest(mRXCharacteristic, buffer, mBufferOffset, length,
                        gatt));
                mBufferOffset += length;
            }
        }

        @Override
        public void onCharacteristicNotified(final BluetoothGatt gatt, final
        BluetoothGattCharacteristic characteristic) {
            final String data = ParserUtils.parse(characteristic);
            LogUtils.d("@@@ onCharacteristicNotified data = " + data);
            mCallbacks.onDataReceived(gatt.getDevice(), data);
        }

        /**
         * Sends the given text to RX characteristic.
         *
         * @param text the text to be sent
         */
        public void send(BluetoothDevice device, final String text) {
            LogUtils.d("ddd", "command = " + text);
            // Are we connected?
            if (mRXCharacteristic == null) return;

            // An outgoing buffer may not be null if there is already another packet being sent. We
            // do nothing in this case.
            if (!TextUtils.isEmpty(text) && mOutgoingBuffer == null) {
                final byte[] buffer = mOutgoingBuffer = hexStringToByte(text);
                mBufferOffset = 0;

                // Depending on whether the characteristic has the WRITE REQUEST property or not, we
                // will either send it as it is (hoping the long write is implemented),
                // or divide it into up to 20 bytes chunks and send them one by one.
                final boolean writeRequest = (mRXCharacteristic.getProperties() &
                        BluetoothGattCharacteristic.PROPERTY_WRITE) > 0;

                if (!writeRequest) { // no WRITE REQUEST property
                    final int length = Math.min(buffer.length, MAX_PACKET_SIZE);
                    mBufferOffset += length;
                    enqueue(Request.newWriteRequest(mRXCharacteristic, buffer, 0, length,
                            getGattByDeviceAddr(device.getAddress())));
                } else { // there is WRITE REQUEST property, let's try Long Write
                    mBufferOffset = buffer.length;
                    enqueue(Request.newWriteRequest(mRXCharacteristic, buffer, 0, buffer.length,
                            getGattByDeviceAddr(device.getAddress())));
                }
            }
        }

    }


    @Override
    protected boolean shouldAutoConnect() {
        // We want the connection to be kept
        return true;
    }

    private static int toByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    private static byte[] hexStringToByte(String hex) {
        int len = (hex.length() / 2);
        byte[] result = new byte[len];
        char[] achar = hex.toCharArray();
        for (int i = 0; i < len; i++) {
            int pos = i * 2;
            result[i] = (byte) (toByte(achar[pos]) << 4 | toByte(achar[pos + 1]));
        }
        return result;
    }

    public void send(final String text) {
    }

    public void send(BluetoothDevice device, final String text) {
        if (mGattCallBackMap.get(device.getAddress()) != null) {
            mGattCallBackMap.get(device.getAddress()).send(device, text);
        }
    }

}
