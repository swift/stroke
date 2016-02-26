/*  Copyright (c) 2016, Isode Limited, London, England.
 *  All rights reserved.
 *
 *  Acquisition and use of this software and related materials for any
 *  purpose requires a written license agreement from Isode Limited,
 *  or a written license from an organisation licensed by Isode Limited
 *  to grant such a license.
 *
 */
package com.isode.stroke.base;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.junit.Before;
import org.junit.Test;

/**
 * @author ac
 * @since 16.5
 *
 */
public class DateTimeTest {

    private final SimpleDateFormat isoFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

    @Before
    public void setUp() {
        isoFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));
    }
    
    @Test
    public void testStringToDateTime_UTC() {
        Date time = DateTime.stringToDate("1969-07-21T02:56:15Z");
        assertNotNull(time);
        assertEquals("1969-07-21T02:56:15", isoFormatter.format(time));
    }
    
    @Test
    public void testStringToDateTime_WithTimezone() {
        Date time = DateTime.stringToDate("1969-07-20T21:56:15-05:00");
        assertNotNull(time);
        assertEquals("1969-07-21T02:56:15", isoFormatter.format(time));
    }
    
    @Test
    public void testDateTimeToString() {
        Date time = DateTime.stringToDate("1969-07-20T21:56:15-05:00");
        assertEquals("1969-07-21T02:56:15Z", DateTime.dateToString(time));
    }
    
}
