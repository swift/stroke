/*
 * Copyright (c) 2011 Tobias Markmann
 * Licensed under the simplified BSD license.
 * See Documentation/Licenses/BSD-simplified.txt for more information.
 */
/*
 * Copyright (c) 2015 Isode Limited.
 * All rights reserved.
 * See the COPYING file for more information.
 */
/*
 * Copyright (c) 2015 Tarun Gupta.
 * Licensed under the simplified BSD license.
 * See Documentation/Licenses/BSD-simplified.txt for more information.
 */

package com.isode.stroke.serializer.payloadserializers;

import com.isode.stroke.serializer.payloadserializers.JingleFileTransferFileInfoSerializer;
import com.isode.stroke.serializer.GenericPayloadSerializer;
import com.isode.stroke.serializer.xml.XMLRawTextNode;
import com.isode.stroke.serializer.xml.XMLElement;
import com.isode.stroke.elements.JingleFileTransferHash;
import com.isode.stroke.elements.JingleFileTransferFileInfo;
import com.isode.stroke.base.NotNull;

public class JingleFileTransferHashSerializer extends GenericPayloadSerializer<JingleFileTransferHash> {

	public JingleFileTransferHashSerializer() {
		super(JingleFileTransferHash.class);
	}

	public String serializePayload(JingleFileTransferHash payload) {
		// code for version urn:xmpp:jingle:apps:file-transfer:2
		//XMLElement hash("hash", "urn:xmpp:jingle:apps:file-transfer:info:2", payload->getHash());

		// code for version urn:xmpp:jingle:apps:file-transfer:4
		XMLElement checksum = new XMLElement("checksum", "urn:xmpp:jingle:apps:file-transfer:4");

		JingleFileTransferFileInfoSerializer fileSerializer = new JingleFileTransferFileInfoSerializer();

		XMLRawTextNode file = new XMLRawTextNode(fileSerializer.serialize((JingleFileTransferFileInfo)(payload.getFileInfo())));

		checksum.addNode(file);

		return checksum.serialize();
	}
}