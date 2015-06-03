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
import com.isode.stroke.elements.VCardUpdate;
import com.isode.stroke.base.NotNull;

public class VCardUpdateSerializer extends GenericPayloadSerializer<VCardUpdate> {

	/**
	* Constructor.
	*/
	public VCardUpdateSerializer() {
		super(VCardUpdate.class);
	}

	/**
	* @param vcardUpdate, notnull
	*/
	@Override
	public String serializePayload(VCardUpdate vcardUpdate) {
		NotNull.exceptIfNull(vcardUpdate, "vcardUpdate");
		XMLElement updateElement = new XMLElement("x", "vcard-temp:x:update");
		XMLElement photoElement = new XMLElement("photo");
		photoElement.addNode(new XMLTextNode(vcardUpdate.getPhotoHash()));
		updateElement.addNode(photoElement);
		return updateElement.serialize();
	}
}