/*
 * Copyright (c) 2015 Tarun Gupta.
 * Licensed under the simplified BSD license.
 * See Documentation/Licenses/BSD-simplified.txt for more information.
 */

package com.isode.stroke.parser.payloadparsers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import org.junit.Test;
import com.isode.stroke.elements.UserLocation;
import com.isode.stroke.parser.payloadparsers.UserLocationParser;
import com.isode.stroke.parser.payloadparsers.PayloadsParserTester;
import com.isode.stroke.eventloop.DummyEventLoop;
import java.util.Date;
import com.isode.stroke.base.DateTime;
import java.util.TimeZone;

public class UserLocationParserTest {

	public UserLocationParserTest() {
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
	}

	@Test
	public void testParse_with_all_variables() {
		DummyEventLoop eventLoop = new DummyEventLoop();
		PayloadsParserTester parser = new PayloadsParserTester(eventLoop);
		assertNotNull(parser.parse("<geoloc xmlns=\"http://jabber.org/protocol/geoloc\">" +
								"<area>Barbaric</area><alt>5.75</alt><locality>Near</locality>" +
								"<lat>1.67</lat><accuracy>0.95</accuracy><description>Nice</description>" +
								"<countrycode>+91</countrycode><timestamp>2015-06-11T20:55:50Z</timestamp><floor>3</floor>" +
								"<building>First</building><room>E315</room><country>USA</country>" +
								"<region>NewSode</region><uri>URIs</uri><lon>6.7578</lon><error>5.66</error>" +
								"<postalcode>67</postalcode><bearing>12.89</bearing><text>Hello</text>" +
								"<datum>Datee</datum><street>Highway</street><speed>56.77</speed></geoloc>"));

		UserLocation payload = (UserLocation)parser.getPayload();

		assertEquals ("Barbaric", payload.getArea());
		assertEquals (Float.valueOf(5.75F), payload.getAltitude());
		assertEquals ("Near", payload.getLocality());
		assertEquals (Float.valueOf(1.670F), payload.getLatitude());
		assertEquals (Float.valueOf(0.95F), payload.getAccuracy());
		assertEquals ("Nice", payload.getDescription());
		assertEquals ("+91", payload.getCountryCode());
		assertEquals (DateTime.dateToString(new Date(1434056150620L)), DateTime.dateToString(payload.getTimestamp()));
		assertEquals ("3", payload.getFloor());
		assertEquals ("First", payload.getBuilding());
		assertEquals ("E315", payload.getRoom());
		assertEquals ("USA", payload.getCountry());
		assertEquals ("NewSode", payload.getRegion());
		assertEquals ("URIs", payload.getURI());
		assertEquals (Float.valueOf(6.7578F), payload.getLongitude());
		assertEquals (Float.valueOf(5.66F), payload.getError());
		assertEquals ("67", payload.getPostalCode());
		assertEquals (Float.valueOf(12.89F), payload.getBearing());
		assertEquals ("Hello", payload.getText());
		assertEquals ("Datee", payload.getDatum());
		assertEquals ("Highway", payload.getStreet());
		assertEquals (Float.valueOf(56.77F), payload.getSpeed());
	}

	@Test
	public void testParse_with_Some_variables() {
		DummyEventLoop eventLoop = new DummyEventLoop();
		PayloadsParserTester parser = new PayloadsParserTester(eventLoop);
		assertNotNull(parser.parse("<geoloc xmlns=\"http://jabber.org/protocol/geoloc\">" +
								"<area>Barbaric</area><alt>5.75</alt><locality>Near</locality>" +
								"<accuracy>0.95</accuracy><description>Nice</description>" +
								"<countrycode>+91</countrycode><timestamp>2015-06-11T20:55:50Z</timestamp><floor>3</floor>" +
								"<region>NewSode</region><uri>URIs</uri><lon>6.7578</lon><error>5.66</error>" +
								"<postalcode>67</postalcode><bearing>12.89</bearing><text>Hello</text></geoloc>"));

		UserLocation payload = (UserLocation)parser.getPayload();

		assertEquals ("Barbaric", payload.getArea());
		assertEquals (Float.valueOf(5.75F), payload.getAltitude());
		assertEquals ("Near", payload.getLocality());
		assertNull(payload.getLatitude());
		assertEquals (Float.valueOf(0.95F), payload.getAccuracy());
		assertEquals ("Nice", payload.getDescription());
		assertEquals ("+91", payload.getCountryCode());
		assertEquals (DateTime.dateToString(new Date(1434056150620L)), DateTime.dateToString(payload.getTimestamp()));
		assertEquals ("3", payload.getFloor());
		assertNull(payload.getBuilding());
		assertNull(payload.getRoom());
		assertNull(payload.getCountry());
		assertEquals ("NewSode", payload.getRegion());
		assertEquals ("URIs", payload.getURI());
		assertEquals (Float.valueOf(6.7578F), payload.getLongitude());
		assertEquals (Float.valueOf(5.66F), payload.getError());
		assertEquals ("67", payload.getPostalCode());
		assertEquals (Float.valueOf(12.89F), payload.getBearing());
		assertEquals ("Hello", payload.getText());
		assertNull(payload.getDatum());
		assertNull(payload.getStreet());
		assertNull(payload.getSpeed());
	}
}