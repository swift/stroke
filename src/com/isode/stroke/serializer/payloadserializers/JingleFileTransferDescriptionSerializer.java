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
import com.isode.stroke.elements.JingleFileTransferDescription;
import com.isode.stroke.elements.JingleFileTransferFileInfo;
import com.isode.stroke.base.NotNull;

public class JingleFileTransferDescriptionSerializer extends GenericPayloadSerializer<JingleFileTransferDescription> {

	public JingleFileTransferDescriptionSerializer() {
		super(JingleFileTransferDescription.class);
	}

	public String serializePayload(JingleFileTransferDescription payload) {
		XMLElement description = new XMLElement("description", "urn:xmpp:jingle:apps:file-transfer:4");

		JingleFileTransferFileInfoSerializer fileInfoSerializer = new JingleFileTransferFileInfoSerializer();
		XMLRawTextNode fileInfoXML = new XMLRawTextNode(fileInfoSerializer.serialize((JingleFileTransferFileInfo)(payload.getFileInfo())));
		description.addNode(fileInfoXML);
		return description.serialize();
	}
}