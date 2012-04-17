/*
 * Copyright (c) 2012, Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2011, Kevin Smith
 * All rights reserved.
 */
package com.isode.stroke.serializer.payloadserializers;

import com.isode.stroke.elements.MUCInvitationPayload;
import com.isode.stroke.serializer.GenericPayloadSerializer;
import com.isode.stroke.serializer.xml.XMLElement;

/**
 * Serializer for {@link MUCInvitationPayload} element.
 */
public class MUCInvitationPayloadSerializer extends GenericPayloadSerializer<MUCInvitationPayload> {
    /**
     * Constructor
     */
    public MUCInvitationPayloadSerializer()  {
        super(MUCInvitationPayload.class);
    }

    @Override
    public String serializePayload(MUCInvitationPayload payload) {
        XMLElement mucElement = new XMLElement("x", "jabber:x:conference");
        if (payload.getIsContinuation()) {
            mucElement.setAttribute("continue", "true");
        }
        if (payload.getJID() != null && payload.getJID().isValid()) {
            mucElement.setAttribute("jid", payload.getJID().toString());
        }
        if (payload.getPassword() != null && !payload.getPassword().isEmpty()) {
            mucElement.setAttribute("password", payload.getPassword());
        }
        if (payload.getReason() != null && !payload.getReason().isEmpty()) {
            mucElement.setAttribute("reason", payload.getReason());
        }
        if (payload.getThread() != null && !payload.getThread().isEmpty()) {
            mucElement.setAttribute("thread", payload.getThread());
        }
        return mucElement.serialize();
    }
}
