package com.mobilenow.cyrcadia_itbra.ble.command;

import io.reactivex.ObservableEmitter;

public class ClearCommand extends DefaultSetCommand {
    public static final String COMMAND_ID = "18";

    @Override
    public String getCommandID() {
        return COMMAND_ID;
    }

    @Override
    public void dealForSpecialCommand(String data, ObservableEmitter emitter) {
    }
}
