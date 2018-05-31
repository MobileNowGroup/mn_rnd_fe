package com.mobilenow.cyrcadia_itbra.ble.command;

import com.blankj.utilcode.util.LogUtils;
import com.mobilenow.cyrcadia_itbra.ble.base.ITBraBleManager;

import io.reactivex.ObservableEmitter;

public class GetDeviceTemperatureRangeCommand extends DefaultGetCommand {
    public static final String COMMAND_ID = "32";

    @Override
    public String getCommandID() {
        return COMMAND_ID;
    }

    @Override
    public void dealForSpecialCommand(String data, ObservableEmitter emitter) {
        ////00 0000 0000 00 03
        LogUtils.d("ddd GetDeviceTemperatureRangeCommand, data = " + data);
        if (data == null || !data.equalsIgnoreCase(Command.DEFAULT_TEMPERATURERQANGE)) { //45 -- 10
            ITBraBleManager.getInstance().setDeviceTemperatureRange(null);
        }
    }
}
