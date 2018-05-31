package com.mobilenow.cyrcadia_itbra.ble.command;

public class Command {
    public static final String COMMAND_HEAD = "AA55";
    static final String COMMAND_EMPTY_DATA = "0000";
    static final String COMMAND_NOTIFY_REPLY_DATA = "000181";
    static final String DEFAULT_TEMPERATURERQANGE = "119403E8119403E8";
    static final String DEFAULT_MEASUREMENT = "0002780A";//testing one minutes And 20s get
    public static final int DEFAULT_MEASUREMENT_TIME = 60 * 60 * 2;//testing one minutes And 20s get
    // a 0002780A
    // data
    static final String DEFAULT_MEASUREMENT_RANGE = "0008119403E8119403E8";

    public static String getCommandIDFromRes(String result) {
        if (result != null && result.length() > 6) {
            return result.substring(4, 6);

        }
        return null;
    }

}
