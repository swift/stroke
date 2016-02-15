/*  Copyright (c) 2016, Isode Limited, London, England.
 *  All rights reserved.
 *
 *  Acquisition and use of this software and related materials for any
 *  purpose requires a written license agreement from Isode Limited,
 *  or a written license from an organisation licensed by Isode Limited
 *  to grant such a license.
 *
 */
package com.isode.stroke.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.isode.stroke.base.ByteArray;

/**
 * Tests for {@link BOSHBodyExtractor}
 */
public class BOSHBodyExtractorTest {

    private final PlatformXMLParserFactory parserFactory = new PlatformXMLParserFactory();

    @Test
    public void testGetBody() {
        ByteArray data = new ByteArray("<body a1='a\"1' a2=\"a'2\" boo='bar'   >"
              +"foo <message> <body> bar"
          +"</body   >  ");
        BOSHBodyExtractor testling = new BOSHBodyExtractor(parserFactory, data);
        assertNotNull(testling.getBody());
        assertEquals("a\"1",testling.getBody().getAttributes().getAttribute("a1"));
        assertEquals("foo <message> <body> bar",testling.getBody().getContent());
    }

    @Test
    public void testGetBody_EmptyContent() {
        ByteArray data = new ByteArray("<body foo='bar'/>");
        BOSHBodyExtractor testling = new BOSHBodyExtractor(parserFactory, data);
        
        assertNotNull(testling.getBody());
        assertEquals("bar",testling.getBody().getAttributes().getAttribute("foo"));
        assertTrue(testling.getBody().getContent().isEmpty());
    }

    @Test
    public void testGetBody_EmptyContent2() {
        ByteArray data = new ByteArray("<body foo='bar'></body>");
        BOSHBodyExtractor testling = new BOSHBodyExtractor(parserFactory, data);
        
        assertNotNull(testling.getBody());
        assertEquals("bar",testling.getBody().getAttributes().getAttribute("foo"));
        assertTrue(testling.getBody().getContent().isEmpty());
    }

    @Test
    public void testGetBody_EmptyElementEmptyContent() {
        ByteArray data = new ByteArray("<body/>");
        BOSHBodyExtractor testling = new BOSHBodyExtractor(parserFactory, data);
        assertNotNull(testling.getBody());
    }

    @Test
    public void testGetBody_InvalidStartTag() {
        ByteArray data = new ByteArray("<bodi></body>");
        BOSHBodyExtractor testling = new BOSHBodyExtractor(parserFactory, data);
        assertNull(testling.getBody());
    }

    @Test
    public void testGetBody_InvalidStartTag2() {
        ByteArray data = new ByteArray("<bodyy></body>");
        BOSHBodyExtractor testling = new BOSHBodyExtractor(parserFactory, data);
        assertNull(testling.getBody());
    }

    @Test
    public void testGetBody_IncompleteStartTag() {
        ByteArray data = new ByteArray("<body");
        BOSHBodyExtractor testling = new BOSHBodyExtractor(parserFactory, data);
        assertNull(testling.getBody());
    }

    @Test
    public void testGetBody_InvalidEndTag() {
        ByteArray data = new ByteArray("<body></bodi>");
        BOSHBodyExtractor testling = new BOSHBodyExtractor(parserFactory, data);
        assertNull(testling.getBody());
    }
    
    @Test
    public void testGetBody_InvalidEndTag2() {
        ByteArray data = new ByteArray("<body><b/body>");
        BOSHBodyExtractor testling = new BOSHBodyExtractor(parserFactory, data);
        assertNull(testling.getBody());
    }

}
