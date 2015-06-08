/*
 * Copyright (c) 2010 Isode Limited.
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
import com.isode.stroke.elements.Bytestreams;
import com.isode.stroke.base.NotNull;

public class BytestreamsSerializer extends GenericPayloadSerializer<Bytestreams> {

	/**
	* Constructor.
	*/
	public BytestreamsSerializer() {
		super(Bytestreams.class);
	}

	/**
	* @param bytestreams, notnull
	*/
	@Override
	public String serializePayload(Bytestreams bytestreams) {
		NotNull.exceptIfNull(bytestreams, "bytestreams");
		XMLElement queryElement = new XMLElement("query", "http://jabber.org/protocol/bytestreams");
		queryElement.setAttribute("sid", bytestreams.getStreamID());
		for(Bytestreams.StreamHost streamHost: bytestreams.getStreamHosts()) {
			XMLElement streamHostElement = new XMLElement("streamhost");
			streamHostElement.setAttribute("host", streamHost.host);
			streamHostElement.setAttribute("jid", streamHost.jid.toString());
			streamHostElement.setAttribute("port", Integer.toString(streamHost.port));
			queryElement.addNode(streamHostElement);
		}

		if (bytestreams.getUsedStreamHost() != null) {
			XMLElement streamHostElement = new XMLElement("streamhost-used");
			streamHostElement.setAttribute("jid", bytestreams.getUsedStreamHost().toString());
			queryElement.addNode(streamHostElement);
		}
		return queryElement.serialize();
	}
}