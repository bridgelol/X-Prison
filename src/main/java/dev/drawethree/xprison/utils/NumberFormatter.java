package dev.drawethree.xprison.utils;

import java.text.DecimalFormat;

public class NumberFormatter {

    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#,###");

    public static String formatNumber(Number number) {
        return DECIMAL_FORMAT.format(number.doubleValue());
    }
}
