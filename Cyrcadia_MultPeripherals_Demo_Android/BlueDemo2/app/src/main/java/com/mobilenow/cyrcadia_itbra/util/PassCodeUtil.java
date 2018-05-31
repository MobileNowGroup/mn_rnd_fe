package com.mobilenow.cyrcadia_itbra.util;

import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.StringUtils;

public class PassCodeUtil {
    private final static String PASSCODE_KEY = "passcode";

    public static void writePasscode(String passcode) {
        if (!StringUtils.isEmpty(passcode)) {
            SPUtils.getInstance().put(PASSCODE_KEY, passcode);
        }
    }

    public static String readPasscode() {
        return SPUtils.getInstance().getString(PASSCODE_KEY, "");
    }

    public static boolean checkPasscode(String passcode) {
        String saveCode = SPUtils.getInstance().getString(PASSCODE_KEY, "2222");
        return !StringUtils.isEmpty(passcode) && !StringUtils.isEmpty(saveCode) && saveCode
                .equals(passcode);
    }
}
