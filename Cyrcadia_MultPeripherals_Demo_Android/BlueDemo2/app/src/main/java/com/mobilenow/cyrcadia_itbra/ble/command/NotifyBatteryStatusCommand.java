package com.mobilenow.cyrcadia_itbra.ble.command;

import com.mobilenow.cyrcadia_itbra.data.ble.BatteryStatusModel;

import io.reactivex.ObservableEmitter;


public class NotifyBatteryStatusCommand extends NotificationCommand {
    public static final String COMMAND_ID = "51";

    @Override
    public String getCommandID() {
        return COMMAND_ID;
    }

    @Override
    public void dealForSpecialCommand(String data, ObservableEmitter emitter) {
        if (data.length() == 2) {
            BatteryStatusModel model = new BatteryStatusModel();
            String patchConnectStatus = data.substring(0, 2);
            if (patchConnectStatus.equalsIgnoreCase("00")) {
                model.setBatteryapacityResult(BatteryStatusModel.BatteryapacityResult.OVERTBD);
            } else if (patchConnectStatus.equalsIgnoreCase("01")) {
                model.setBatteryapacityResult(BatteryStatusModel.BatteryapacityResult.BELOWTBD);
            }
            emitter.onNext(model);
        }
    }
}
