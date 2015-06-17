/*
 * Copyright (c) 2014 Isode Limited.
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
import com.isode.stroke.elements.JingleFileTransferFileInfo;
import com.isode.stroke.base.NotNull;
import com.isode.stroke.base.DateTime;
import com.isode.stroke.base.ByteArray;
import com.isode.stroke.stringcodecs.Base64;
import java.util.Map;
import java.util.HashMap;
import java.util.Date;

public class JingleFileTransferFileInfoSerializer extends GenericPayloadSerializer<JingleFileTransferFileInfo> {

	public JingleFileTransferFileInfoSerializer() {
		super(JingleFileTransferFileInfo.class);
	}

	public String serializePayload(JingleFileTransferFileInfo fileInfo) {
		XMLElement fileElement = new XMLElement("file", "");

		if (fileInfo.getDate().getTime() != 0L) {
			fileElement.addNode(new XMLElement("date", "", DateTime.dateToString(fileInfo.getDate())));
		}

		if (fileInfo.getDescription().length() != 0) {
			fileElement.addNode(new XMLElement("desc", "", fileInfo.getDescription()));
		}

		if (fileInfo.getMediaType().length() != 0) {
			fileElement.addNode(new XMLElement("media-type", "", fileInfo.getMediaType()));
		}

		if (fileInfo.getName().length() != 0) {
			fileElement.addNode(new XMLElement("name", "", fileInfo.getName()));
		}

		if (fileInfo.getSupportsRangeRequests()) {
			XMLElement range = new XMLElement("range");
			if (fileInfo.getRangeOffset() != 0) {
				range.setAttribute("offset", Long.toString(fileInfo.getRangeOffset()));
			}
			fileElement.addNode(range);
		}

		if (fileInfo.getSize() > 0) {
			fileElement.addNode(new XMLElement("size", "", Long.toString(fileInfo.getSize())));
		}

		for (Map.Entry<String, ByteArray> entry : fileInfo.getHashes().entrySet()) {
			XMLElement hash = new XMLElement("hash", "urn:xmpp:hashes:1", Base64.encode(entry.getValue()));
			hash.setAttribute("algo", entry.getKey());
			fileElement.addNode(hash);
		}

		return fileElement.serialize();
	}
}