/*
 * Copyright (c) 2011 Tobias Markmann
 * Licensed under the simplified BSD license.
 * See Documentation/Licenses/BSD-simplified.txt for more information.
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
import com.isode.stroke.elements.StreamInitiationFileInfo;
import com.isode.stroke.base.NotNull;
import com.isode.stroke.base.DateTime;

public class StreamInitiationFileInfoSerializer extends GenericPayloadSerializer<StreamInitiationFileInfo> {

	public StreamInitiationFileInfoSerializer() {
		super(StreamInitiationFileInfo.class);
	}

	public String serializePayload(StreamInitiationFileInfo fileInfo) {
		XMLElement fileElement = new XMLElement("file", "http://jabber.org/protocol/si/profile/file-transfer");
		
		if (fileInfo.getDate() != null) {
			fileElement.setAttribute("date", DateTime.dateToString(fileInfo.getDate()));
		}
		fileElement.setAttribute("hash", fileInfo.getHash());
		if (!fileInfo.getAlgo().equals("md5")) {
			fileElement.setAttribute("algo", fileInfo.getAlgo());
		}
		if (fileInfo.getName().length() != 0) {
			fileElement.setAttribute("name", fileInfo.getName());
		}
		if (fileInfo.getSize() != 0) {
			fileElement.setAttribute("size", Long.toString(fileInfo.getSize()));
		}
		if (fileInfo.getDescription().length() != 0) {
			XMLElement desc = new XMLElement("desc", "", fileInfo.getDescription());
			fileElement.addNode(desc);
		}
		if (fileInfo.getSupportsRangeRequests()) {
			XMLElement range = new XMLElement("range");
			if (fileInfo.getRangeOffset() != 0) {
				range.setAttribute("offset", Long.toString(fileInfo.getRangeOffset()));
			}
			fileElement.addNode(range);
		}
		return fileElement.serialize();
	}
}