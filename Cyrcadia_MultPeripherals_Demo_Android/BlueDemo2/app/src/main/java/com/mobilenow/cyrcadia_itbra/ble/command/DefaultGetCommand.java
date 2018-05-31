package com.mobilenow.cyrcadia_itbra.ble.command;

import android.bluetooth.BluetoothDevice;

import com.mobilenow.cyrcadia_itbra.ble.base.ITBraBleManager;

public abstract class DefaultGetCommand extends BaseCommand {

    @Override
    public boolean isNotify() {
        return false;
    }

    @Override
    public void sendCommand() {
        ITBraBleManager.getInstance().send(getCommandID() + Command.COMMAND_EMPTY_DATA);
    }

    @Override
    public void sendCommand(BluetoothDevice device) {
        ITBraBleManager.getInstance().send(device, getCommandID() + Command.COMMAND_EMPTY_DATA);
    }

    public abstract String getCommandID();


}
