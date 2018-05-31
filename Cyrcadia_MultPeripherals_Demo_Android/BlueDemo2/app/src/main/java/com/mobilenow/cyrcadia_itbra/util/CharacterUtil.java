package com.mobilenow.cyrcadia_itbra.util;

import java.util.Locale;

public class CharacterUtil {
    public static String patchZero(int realNumber) {
        Locale locale = Locale.getDefault();
        return realNumber < 10 && realNumber >= 0 ? String.format(locale, "0%d", realNumber) : String.valueOf(realNumber);
    }
}
