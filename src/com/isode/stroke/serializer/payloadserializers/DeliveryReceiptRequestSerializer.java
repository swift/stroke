/*
 * Copyright (c) 2015, Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2011 Tobias Markmann
 * Licensed under the BSD license.
 * See http://www.opensource.org/licenses/bsd-license.php for more information.
 */

package com.isode.stroke.serializer.payloadserializers;

import com.isode.stroke.elements.DeliveryReceiptRequest;
import com.isode.stroke.serializer.GenericPayloadSerializer;
import com.isode.stroke.serializer.xml.XMLElement;

class DeliveryReceiptRequestSerializer extends GenericPayloadSerializer<DeliveryReceiptRequest> {

    public DeliveryReceiptRequestSerializer() {
        super(DeliveryReceiptRequest.class);
    }

    @Override
    protected String serializePayload(DeliveryReceiptRequest payload) {
        return new XMLElement("request", "urn:xmpp:receipts").serialize();
    }

}
