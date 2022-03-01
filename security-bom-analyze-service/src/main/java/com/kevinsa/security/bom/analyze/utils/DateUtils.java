package com.kevinsa.security.bom.analyze.utils;

import java.util.Calendar;

import org.apache.commons.lang3.time.FastDateFormat;
import org.springframework.stereotype.Component;

@Component
public class DateUtils {
    private final Calendar calendar;

    DateUtils() {
        calendar = Calendar.getInstance();
    }

    private static final FastDateFormat FAST_DATE_FORMAT = FastDateFormat.getInstance("yyyyMMdd");

    public String currentTime() {
        return FAST_DATE_FORMAT.format(calendar.getTime());
    }

}
