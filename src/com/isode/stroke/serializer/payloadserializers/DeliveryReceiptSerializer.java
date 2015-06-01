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

import com.isode.stroke.elements.DeliveryReceipt;
import com.isode.stroke.serializer.GenericPayloadSerializer;
import com.isode.stroke.serializer.xml.XMLElement;

class DeliveryReceiptSerializer extends GenericPayloadSerializer<DeliveryReceipt> {

    public DeliveryReceiptSerializer() {
        super(DeliveryReceipt.class);
    }

    @Override
    protected String serializePayload(DeliveryReceipt receipt) {
        XMLElement received = new XMLElement("received", "urn:xmpp:receipts");
        received.setAttribute("id", receipt.getReceivedID());
        return received.serialize();
    }

}
