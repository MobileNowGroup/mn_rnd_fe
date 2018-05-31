package com.mobilenow.cyrcadia_itbra.ble.command;

import com.mobilenow.cyrcadia_itbra.data.ble.DeviceStatusModel;
import com.mobilenow.cyrcadia_itbra.data.ble.NotifyWarmAmdMeasurementModel;

import io.reactivex.ObservableEmitter;


public class NotifyWarmAndMeasurementCommand extends NotificationCommand {
    public static final String COMMAND_ID = "50";

    @Override
    public String getCommandID() {
        return COMMAND_ID;
    }

    @Override
    public void dealForSpecialCommand(String data, ObservableEmitter emitter) {
        NotifyWarmAmdMeasurementModel model = new NotifyWarmAmdMeasurementModel();
        String sensorsValidation = data.substring(0, 2);
        model.setSensorsValidation(sensorsValidation);
        String sensorsReadingInRange = data.substring(2, 4);
        model.setSensorsReadingInRange(sensorsReadingInRange);
        String statusByte = data.substring(12, 14);
        if (statusByte.equalsIgnoreCase("00")) {
            model.setStatusByte(DeviceStatusModel.StatusByte.WARMUP);
        } else if (statusByte.equalsIgnoreCase("01")) {
            model.setStatusByte(DeviceStatusModel.StatusByte.MEASUREMENTCYCLEINPROGRESS);
        } else if (statusByte.equalsIgnoreCase("02")) {
            model.setStatusByte(DeviceStatusModel.StatusByte.MEASUREMENTCYCLEABORTED);
        } else if (statusByte.equalsIgnoreCase("03")) {
            model.setStatusByte(DeviceStatusModel.StatusByte.MEASUREMENTCYCLECOMPLETED);
        }
    }
}
