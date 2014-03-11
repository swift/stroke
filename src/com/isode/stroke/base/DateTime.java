/*
* Copyright (c) 2014 Kevin Smith and Remko Tronçon
* All rights reserved.
*/

/*
* Copyright (c) 2014, Isode Limited, London, England.
* All rights reserved.
*/

package com.isode.stroke.base;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class DateTime {
    public static Date stringToDate(String date) {
        SimpleDateFormat parser = new SimpleDateFormat(format);
        parser.setTimeZone(TimeZone.getTimeZone("UTC")); 
        try {
            return parser.parse(date);
        } catch (ParseException e) {
            return null;
        }
    }

    public static String dateToString(Date date) {
        SimpleDateFormat parser = new SimpleDateFormat(format);
        parser.setTimeZone(TimeZone.getTimeZone("UTC")); 
        return parser.format(date);
    }
    
    static private String format = "yyyy-MM-dd'T'HH:mm:ss'Z'";
}
