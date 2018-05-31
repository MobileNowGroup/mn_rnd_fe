package com.mobilenow.cyrcadia_itbra.ble.command;

import com.mobilenow.cyrcadia_itbra.data.ble.TemperatureDataModel;

import io.reactivex.ObservableEmitter;


public class GetTemperatureDataCommand extends DefaultGetCommand {
    public static final String COMMAND_ID = "36";

    @Override
    public String getCommandID() {
        return COMMAND_ID;
    }

    @Override
    public void dealForSpecialCommand(String data, ObservableEmitter emitter) {
        ////00 0000 0000 00 03
        TemperatureDataModel model = new TemperatureDataModel();
        String cycleIndex = data.substring(0, 4);
        model.setCycleIndex(cycleIndex);
        String patchConnection = data.substring(4, 6);
        if (patchConnection.equalsIgnoreCase("00")) {
            model.setPatchConnection(TemperatureDataModel.PatchConnection.BOTH);
        } else if (patchConnection.equalsIgnoreCase("01")) {
            model.setPatchConnection(TemperatureDataModel.PatchConnection.LEFT);
        } else if (patchConnection.equalsIgnoreCase("02")) {
            model.setPatchConnection(TemperatureDataModel.PatchConnection.RIGHT);
        }
        if (data.length() > 6) {
            model.setData(data.substring(6).getBytes());
        }
        emitter.onNext(model);
    }
}
