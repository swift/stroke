/*
 * Copyright (c) 2012, Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2010, Kevin Smith
 * All rights reserved.
 */
package com.isode.stroke.serializer.payloadserializers;

import com.isode.stroke.elements.MUCItem;
import com.isode.stroke.elements.MUCUserPayload;
import com.isode.stroke.elements.Payload;
import com.isode.stroke.serializer.GenericPayloadSerializer;
import com.isode.stroke.serializer.PayloadSerializer;
import com.isode.stroke.serializer.PayloadSerializerCollection;
import com.isode.stroke.serializer.xml.XMLElement;
import com.isode.stroke.serializer.xml.XMLRawTextNode;
import com.isode.stroke.serializer.xml.XMLTextNode;

/**
 * Serializer for {@link MUCUserPayload} element.
 */
public class MUCUserPayloadSerializer extends GenericPayloadSerializer<MUCUserPayload> {

    private PayloadSerializerCollection serializers_;

    /**
     * Constructor
     * @param serializers Payload Serializer Collection, not null
     */
    public MUCUserPayloadSerializer(PayloadSerializerCollection serializers) {
        super(MUCUserPayload.class);
        this.serializers_ = serializers;
    }

    @Override
    public String serializePayload(MUCUserPayload payload) {
        XMLElement mucElement = new XMLElement("x", "http://jabber.org/protocol/muc#user");
        for (MUCUserPayload.StatusCode statusCode : payload.getStatusCodes()) {
            XMLElement statusElement = new XMLElement("status");
            statusElement.setAttribute("code", String.valueOf(statusCode.code));
            mucElement.addNode(statusElement);
        }
        for (MUCItem item : payload.getItems()) {
            mucElement.addNode(MUCItemSerializer.itemToElement(item));
        }

        if (payload.getPassword() != null) {
            XMLElement passwordElement = new XMLElement("password");
            passwordElement.addNode(new XMLTextNode(payload.getPassword()));
        }

        if (payload.getInvite() != null) {
            MUCUserPayload.Invite invite = payload.getInvite();
            XMLElement inviteElement = new XMLElement("invite");
            if (invite.to != null && invite.to.isValid()) {
                inviteElement.setAttribute("to", invite.to.toString());
            }
            if (invite.from != null && invite.from.isValid()) {
                inviteElement.setAttribute("from", invite.from.toString());
            }
            if (invite.reason != null && !invite.reason.isEmpty()) {
                XMLElement reasonElement = new XMLElement("reason");
                reasonElement.addNode(new XMLTextNode(invite.reason));
            }
            mucElement.addNode(inviteElement);
        }

        Payload childPayload = payload.getPayload();
        if (childPayload != null) {
            PayloadSerializer serializer = serializers_.getPayloadSerializer(childPayload);
            if (serializers_ != null) {
                mucElement.addNode(new XMLRawTextNode(serializer.serialize(childPayload)));
            }
        }
        return mucElement.serialize();
    }
}
