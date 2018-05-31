package com.mobilenow.cyrcadia_itbra.ble.command;

import io.reactivex.ObservableEmitter;

public class WarnUpCommand extends DefaultSetCommand {
    public static final String COMMAND_ID = "17";

    @Override
    public String getCommandID() {
        return COMMAND_ID;
    }

    @Override
    public void dealForSpecialCommand(String data, ObservableEmitter emitter) {
    }
}
