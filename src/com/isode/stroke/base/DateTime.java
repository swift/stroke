/*
 * Copyright (c) 2014-2016, Isode Limited, London, England.
 * All rights reserved.
 */

package com.isode.stroke.base;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateTime {
	
	// Cannot use Java 7 'X' format specifier for timezone
	// (not supported on Android)
    public static Date stringToDate(String date) {
        if (date == null || date.length() < 1) return null;
        SimpleDateFormat parser;
        if (date.charAt(date.length()-1) == 'Z') {
            parser = new SimpleDateFormat(parseFormatZ, Locale.US);
        } else {
            parser = new SimpleDateFormat(parseFormatTz, Locale.US);
        }
        parser.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            return parser.parse(date);
        } catch (ParseException e) {
            if (date.matches(".*[+-]\\d\\d:\\d\\d")) {    // strip ':' from timezone
                date = date.substring(0, date.length()-3) + date.substring(date.length()-2);
            } else if (date.matches(".*[+-]\\d\\d")) {    // make timezone 4 digits (append minutes)
                date += "00";
            } else {
                parser = new SimpleDateFormat(parseFormatNoTz, Locale.US);
                parser.setTimeZone(TimeZone.getTimeZone("UTC"));
            }
            try {
                return parser.parse(date);
            } catch (ParseException e1) {
                return null;
            }
        }
    }

    public static String dateToString(Date date) {
        SimpleDateFormat parser = new SimpleDateFormat(format, Locale.US);
        parser.setTimeZone(TimeZone.getTimeZone("UTC"));
        return parser.format(date);
    }
    
    static private String format = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    static private String parseFormatTz = "yyyy-MM-dd'T'HH:mm:ssZ";
    static private String parseFormatZ = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    static private String parseFormatNoTz = "yyyy-MM-dd'T'HH:mm:ss";
}
