package com.mobilenow.cyrcadia_itbra.ble.command;

import android.bluetooth.BluetoothDevice;

import io.reactivex.ObservableEmitter;

public abstract class BaseCommand {
    private static String COMMAND_OK = "81";

    public abstract String getCommandID();

    public abstract boolean isNotify();

    public abstract void dealForSpecialCommand(String data, ObservableEmitter emitter);

    public abstract void sendCommand();

    public void sendCommand(BluetoothDevice device) {
    }

    public void parseCommand(String data, ObservableEmitter emitter) {

        if (isNotify() || isCommandSuccess(data)) {
            int startIndex = isNotify() ? 10 : 12;
            String res = data.substring(startIndex);
            dealForSpecialCommand(res, emitter);
        } else {
            if (emitter != null) {
                emitter.onError(null);
            }
        }
    }

    private boolean isCommandSuccess(String result) {
        if (result != null && result.length() >= 12) {
            String code = result.substring(10, 12);
            if (code.equalsIgnoreCase(COMMAND_OK)) {
                return true;
            }

        }
        return false;
    }
}
