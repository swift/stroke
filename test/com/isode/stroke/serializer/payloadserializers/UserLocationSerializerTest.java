/*
 * Copyright (c) 2015 Tarun Gupta.
 * Licensed under the simplified BSD license.
 * See Documentation/Licenses/BSD-simplified.txt for more information.
 */

package com.isode.stroke.serializer.payloadserializers;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import com.isode.stroke.serializer.payloadserializers.UserLocationSerializer;
import com.isode.stroke.serializer.PayloadSerializerCollection;
import com.isode.stroke.elements.UserLocation;
import java.util.Date;
import java.util.TimeZone;

public class UserLocationSerializerTest {

	public UserLocationSerializerTest() {
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
	}

	@Test
	public void testSerialize_withAllVariablesSet() {
		PayloadSerializerCollection serializerCollection = new PayloadSerializerCollection();
		UserLocationSerializer testling = new UserLocationSerializer(serializerCollection);
		UserLocation userLocation = new UserLocation();
		userLocation.setArea("Barbaric");
		userLocation.setAltitude(5.75F);
		userLocation.setLocality("Near");
		userLocation.setLatitude(1.670F);
		userLocation.setAccuracy(0.95F);
		userLocation.setDescription("Nice");
		userLocation.setCountryCode("+91");
		userLocation.setTimestamp(new Date(1434056150620L));
		userLocation.setFloor("3");
		userLocation.setBuilding("First");
		userLocation.setRoom("E315");
		userLocation.setCountry("USA");
		userLocation.setRegion("NewSode");
		userLocation.setURI("URIs");
		userLocation.setLongitude(6.7578F);
		userLocation.setError(5.66F);
		userLocation.setPostalCode("67");
		userLocation.setBearing(12.89F);
		userLocation.setText("Hello");
		userLocation.setDatum("Datee");
		userLocation.setStreet("Highway");
		userLocation.setSpeed(56.77F);

		String expectedResult = "<geoloc xmlns=\"http://jabber.org/protocol/geoloc\">" +
								"<area>Barbaric</area><alt>5.75</alt><locality>Near</locality>" +
								"<lat>1.67</lat><accuracy>0.95</accuracy><description>Nice</description>" +
								"<countrycode>+91</countrycode><timestamp>2015-06-11T20:55:50Z</timestamp><floor>3</floor>" +
								"<building>First</building><room>E315</room><country>USA</country>" +
								"<region>NewSode</region><uri>URIs</uri><lon>6.7578</lon><error>5.66</error>" +
								"<postalcode>67</postalcode><bearing>12.89</bearing><text>Hello</text>" +
								"<datum>Datee</datum><street>Highway</street><speed>56.77</speed></geoloc>";
		assertEquals(expectedResult, testling.serialize(userLocation));
	}

	@Test
	public void testSerialize_withSomeVariablesSet() {
		PayloadSerializerCollection serializerCollection = new PayloadSerializerCollection();
		UserLocationSerializer testling = new UserLocationSerializer(serializerCollection);
		UserLocation userLocation = new UserLocation();
		userLocation.setArea("Barbaric");
		userLocation.setAltitude(5.75F);
		userLocation.setLocality("Near");
		userLocation.setAccuracy(0.95F);
		userLocation.setDescription("Nice");
		userLocation.setCountryCode("+91");
		userLocation.setTimestamp(new Date(1434056150620L));
		userLocation.setFloor("3");
		userLocation.setRegion("NewSode");
		userLocation.setURI("URIs");
		userLocation.setLongitude(6.7578F);
		userLocation.setError(5.66F);
		userLocation.setPostalCode("67");
		userLocation.setBearing(12.89F);
		userLocation.setText("Hello");

		String expectedResult = "<geoloc xmlns=\"http://jabber.org/protocol/geoloc\">" +
								"<area>Barbaric</area><alt>5.75</alt><locality>Near</locality>" +
								"<accuracy>0.95</accuracy><description>Nice</description>" +
								"<countrycode>+91</countrycode><timestamp>2015-06-11T20:55:50Z</timestamp><floor>3</floor>" +
								"<region>NewSode</region><uri>URIs</uri><lon>6.7578</lon><error>5.66</error>" +
								"<postalcode>67</postalcode><bearing>12.89</bearing><text>Hello</text></geoloc>";
		assertEquals(expectedResult, testling.serialize(userLocation));
	}
}