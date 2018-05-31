package com.mobilenow.cyrcadia_itbra.ble.command;

import com.mobilenow.cyrcadia_itbra.data.ble.DeviceUDIModel;

import io.reactivex.ObservableEmitter;


public class GetDeviceUDIommand extends DefaultGetCommand {
    public static final String COMMAND_ID = "30";

    @Override
    public String getCommandID() {
        return COMMAND_ID;
    }

    @Override
    public void dealForSpecialCommand(String data, ObservableEmitter emitter) {
        ////00 0000 0000 00 03
        DeviceUDIModel model = new DeviceUDIModel();
        model.setRecorder(data.substring(0, 46));
        model.setLeftPatch(data.substring(46, 46 * 2));
        model.setRightPatch(data.substring(46 * 2, 46 * 3));
        emitter.onNext(model);
    }
}
