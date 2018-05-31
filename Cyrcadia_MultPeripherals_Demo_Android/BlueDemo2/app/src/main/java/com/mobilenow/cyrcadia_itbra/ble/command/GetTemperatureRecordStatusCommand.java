package com.mobilenow.cyrcadia_itbra.ble.command;

import com.mobilenow.cyrcadia_itbra.data.ble.TemperatureRecordStatusModel;

import io.reactivex.ObservableEmitter;

public class GetTemperatureRecordStatusCommand extends DefaultGetCommand {
    public static final String COMMAND_ID = "38";

    @Override
    public String getCommandID() {
        return COMMAND_ID;
    }

    @Override
    public void dealForSpecialCommand(String data, ObservableEmitter emitter) {
        ////00 0000 0000 00 03
        TemperatureRecordStatusModel model = new TemperatureRecordStatusModel();
        model.setTotal(data.substring(0, 2));
        model.setStatus(data.substring(2, 4));
        emitter.onNext(model);
    }
}
