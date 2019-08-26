package com.northmeter.meshbluecontrol.bluetooth.bluetooth.tools;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by benjamin on 16/5/25.
 */
public class DateTool {

    private static SimpleDateFormat sfMinutes = new SimpleDateFormat("HH:mm", Locale.getDefault());

    public static SimpleDateFormat getSfMinutes() {
        return sfMinutes;
    }
}
