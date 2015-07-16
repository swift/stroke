/*
 * Copyright (c) 2010-2015, Isode Limited, London, England.
 * All rights reserved.
 */

package com.isode.stroke.serializer;

import com.isode.stroke.elements.Message;
import com.isode.stroke.serializer.xml.XMLElement;

public class MessageSerializer extends GenericStanzaSerializer<Message>{

    public MessageSerializer(PayloadSerializerCollection payloadSerializers) {
        this(payloadSerializers, null);
    }

    public MessageSerializer(PayloadSerializerCollection payloadSerializers, String explicitNS) {
        super(Message.class, "message", payloadSerializers, explicitNS);
    }

    @Override
    void setStanzaSpecificAttributesGeneric(Message message, XMLElement element) {
        if (message.getType().equals(Message.Type.Chat)) {
		element.setAttribute("type", "chat");
	}
	else if (message.getType().equals(Message.Type.Groupchat)) {
		element.setAttribute("type", "groupchat");
	}
	else if (message.getType().equals(Message.Type.Headline)) {
		element.setAttribute("type", "headline");
	}
	else if (message.getType().equals(Message.Type.Error)) {
		element.setAttribute("type", "error");
	}
    }
    
}
