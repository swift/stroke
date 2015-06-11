/*
 * Copyright (c) 2013 Isode Limited.
 * All rights reserved.
 * See the COPYING file for more information.
 */
/*
 * Copyright (c) 2015 Tarun Gupta.
 * Licensed under the simplified BSD license.
 * See Documentation/Licenses/BSD-simplified.txt for more information.
 */

package com.isode.stroke.serializer.payloadserializers;

import com.isode.stroke.serializer.GenericPayloadSerializer;
import com.isode.stroke.serializer.xml.XMLTextNode;
import com.isode.stroke.serializer.xml.XMLElement;
import com.isode.stroke.serializer.PayloadSerializerCollection;
import com.isode.stroke.elements.UserLocation;
import com.isode.stroke.base.NotNull;
import com.isode.stroke.base.DateTime;

public class UserLocationSerializer extends GenericPayloadSerializer<UserLocation> {

	private PayloadSerializerCollection serializers;

	public UserLocationSerializer(PayloadSerializerCollection serializers) {
		super(UserLocation.class);
		this.serializers = serializers;
	}

	public String serializePayload(UserLocation payload) {
		if (payload == null) {
			return "";
		}

		XMLElement element = new XMLElement("geoloc", "http://jabber.org/protocol/geoloc");
		if (payload.getArea() != null) {
			element.addNode(new XMLElement("area", "", payload.getArea()));
		}
		if (payload.getAltitude() != null) {
			element.addNode(new XMLElement("alt", "", Float.toString(payload.getAltitude())));
		}
		if (payload.getLocality() != null) {
			element.addNode(new XMLElement("locality", "", payload.getLocality()));
		}
		if (payload.getLatitude() != null) {
			element.addNode(new XMLElement("lat", "", Float.toString(payload.getLatitude())));
		}
		if (payload.getAccuracy() != null) {
			element.addNode(new XMLElement("accuracy", "", Float.toString(payload.getAccuracy())));
		}
		if (payload.getDescription() != null) {
			element.addNode(new XMLElement("description", "", payload.getDescription()));
		}
		if (payload.getCountryCode() != null) {
			element.addNode(new XMLElement("countrycode", "", payload.getCountryCode()));
		}
		if (payload.getTimestamp() != null) {
			element.addNode(new XMLElement("timestamp", "", DateTime.dateToString(payload.getTimestamp())));
		}
		if (payload.getFloor() != null) {
			element.addNode(new XMLElement("floor", "", payload.getFloor()));
		}
		if (payload.getBuilding() != null) {
			element.addNode(new XMLElement("building", "", payload.getBuilding()));
		}
		if (payload.getRoom() != null) {
			element.addNode(new XMLElement("room", "", payload.getRoom()));
		}
		if (payload.getCountry() != null) {
			element.addNode(new XMLElement("country", "", payload.getCountry()));
		}
		if (payload.getRegion() != null) {
			element.addNode(new XMLElement("region", "", payload.getRegion()));
		}
		if (payload.getURI() != null) {
			element.addNode(new XMLElement("uri", "", payload.getURI()));
		}
		if (payload.getLongitude() != null) {
			element.addNode(new XMLElement("lon", "", Float.toString(payload.getLongitude())));
		}
		if (payload.getError() != null) {
			element.addNode(new XMLElement("error", "", Float.toString(payload.getError())));
		}
		if (payload.getPostalCode() != null) {
			element.addNode(new XMLElement("postalcode", "", payload.getPostalCode()));
		}
		if (payload.getBearing() != null) {
			element.addNode(new XMLElement("bearing", "", Float.toString(payload.getBearing())));
		}
		if (payload.getText() != null) {
			element.addNode(new XMLElement("text", "", payload.getText()));
		}
		if (payload.getDatum() != null) {
			element.addNode(new XMLElement("datum", "", payload.getDatum()));
		}
		if (payload.getStreet() != null) {
			element.addNode(new XMLElement("street", "", payload.getStreet()));
		}
		if (payload.getSpeed() != null) {
			element.addNode(new XMLElement("speed", "", Float.toString(payload.getSpeed())));
		}
		return element.serialize();
	}
}