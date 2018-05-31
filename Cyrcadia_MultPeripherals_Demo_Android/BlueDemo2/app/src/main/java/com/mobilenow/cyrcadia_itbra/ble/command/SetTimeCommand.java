package com.mobilenow.cyrcadia_itbra.ble.command;

import com.mobilenow.cyrcadia_itbra.ble.base.ITBraBleManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import io.reactivex.ObservableEmitter;


public class SetTimeCommand extends DefaultSetCommand {
    public static final String COMMAND_ID = "10";

    private String getTimeFormatString(String time, int length) {
        StringBuilder data = new StringBuilder(Long.toHexString(Integer.parseInt(time))
                .toUpperCase());
        while (data.length() < length) data.insert(0, "0");
        return data.toString();
    }

    @Override
    public String getCommandID() {
        return COMMAND_ID;
    }

    @Override
    public void dealForSpecialCommand(String data, ObservableEmitter emitter) {

    }

    @Override
    public void sendCommand() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale
                .getDefault());
        Date curDate = new Date(System.currentTimeMillis());
        String str = formatter.format(curDate);
        String time[] = str.split("-");
        ITBraBleManager.getInstance().send(getCommandID() + "0007" + getTimeFormatString(time[0],
                4) + getTimeFormatString(time[1], 2) + getTimeFormatString(time[2], 2) +
                getTimeFormatString(time[3], 2) + getTimeFormatString(time[4], 2) +
                getTimeFormatString(time[5], 2));
    }


}
