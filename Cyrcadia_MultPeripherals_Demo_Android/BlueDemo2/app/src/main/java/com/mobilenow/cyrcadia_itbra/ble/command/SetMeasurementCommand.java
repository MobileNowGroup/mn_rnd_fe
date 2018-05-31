package com.mobilenow.cyrcadia_itbra.ble.command;


import com.mobilenow.cyrcadia_itbra.ble.base.ITBraBleManager;

import io.reactivex.ObservableEmitter;


public class SetMeasurementCommand extends DefaultSetCommand {
    public static final String COMMAND_ID = "12";

    @Override
    public String getCommandID() {
        return COMMAND_ID;
    }

    @Override
    public void dealForSpecialCommand(String data, ObservableEmitter emitter) {

    }

    @Override
    public void sendCommand() {
        ITBraBleManager.getInstance().send(getCommandID() + Command.DEFAULT_MEASUREMENT);
    }


}
