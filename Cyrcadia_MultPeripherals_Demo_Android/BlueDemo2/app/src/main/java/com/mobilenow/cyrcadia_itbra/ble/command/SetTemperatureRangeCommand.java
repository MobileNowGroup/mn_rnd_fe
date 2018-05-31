package com.mobilenow.cyrcadia_itbra.ble.command;


import com.mobilenow.cyrcadia_itbra.ble.base.ITBraBleManager;

import io.reactivex.ObservableEmitter;


public class SetTemperatureRangeCommand extends DefaultSetCommand {
    public static final String COMMAND_ID = "11";

    @Override
    public void sendCommand() {
        ITBraBleManager.getInstance().send(getCommandID() + Command.DEFAULT_MEASUREMENT_RANGE);
    }

    @Override
    public String getCommandID() {
        return COMMAND_ID;
    }

    @Override
    public void dealForSpecialCommand(String data, ObservableEmitter emitter) {

    }


}
