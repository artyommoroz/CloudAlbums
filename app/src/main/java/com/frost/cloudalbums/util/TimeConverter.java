package com.frost.cloudalbums.util;


import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class TimeConverter {

    public static String millisToMMSS(int millis) {
        return String.format(Locale.FRENCH, "%d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(millis),
                TimeUnit.MILLISECONDS.toSeconds(millis)
                        - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis))
        );
    }
}
