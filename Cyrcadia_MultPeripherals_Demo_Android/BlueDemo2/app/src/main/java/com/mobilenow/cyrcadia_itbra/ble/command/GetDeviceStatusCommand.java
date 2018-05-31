package com.mobilenow.cyrcadia_itbra.ble.command;

import com.mobilenow.cyrcadia_itbra.data.ble.DeviceStatusModel;

import io.reactivex.ObservableEmitter;

public class GetDeviceStatusCommand extends DefaultGetCommand {
    public static final String COMMAND_ID = "35";

    @Override
    public String getCommandID() {
        return COMMAND_ID;
    }

    @Override
    public void dealForSpecialCommand(String data, ObservableEmitter emitter) {
        ////00 0000 0000 00 03
        if (data.length() == 14) {
            DeviceStatusModel model = new DeviceStatusModel();
            String patchConnectStatus = data.substring(0, 2);
            if (patchConnectStatus.equalsIgnoreCase("00")) {
                model.setPatchConnectStatus(DeviceStatusModel.PatchConnectStatus.BOTH);
            } else if (patchConnectStatus.equalsIgnoreCase("01")) {
                model.setPatchConnectStatus(DeviceStatusModel.PatchConnectStatus.LEFT);
            } else if (patchConnectStatus.equalsIgnoreCase("02")) {
                model.setPatchConnectStatus(DeviceStatusModel.PatchConnectStatus.RIGHT);
            }
            String sensorValid = data.substring(2, 6);
            if (sensorValid.equalsIgnoreCase("0000")) {
                model.setSensorsValidation(DeviceStatusModel.SensorsValidation.OK);
            } else if (sensorValid.equalsIgnoreCase("0001")) {
                model.setSensorsValidation(DeviceStatusModel.SensorsValidation.LOSE);
            }
            String isInRange = data.substring(6, 10);
            if (isInRange.equalsIgnoreCase("0000")) {
                model.setIsInRange(DeviceStatusModel.SensorsReadingInRange.INRANGE);
            } else {
                model.setIsInRange(DeviceStatusModel.SensorsReadingInRange.OVERTHERANGE);
            }
            String isConfigure = data.substring(10, 12);
            if (isConfigure.equalsIgnoreCase("00")) {
                model.setIsConfigure(DeviceStatusModel.ControlFlag.CONFIGURED);
            } else if (isConfigure.equalsIgnoreCase("01")) {
                model.setIsConfigure(DeviceStatusModel.ControlFlag.CONFIGURING);
            }
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
            emitter.onNext(model);
        }
    }
}
