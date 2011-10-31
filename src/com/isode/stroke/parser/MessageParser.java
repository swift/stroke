/*
 * Copyright (c) 2010, Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2010, Remko Tronçon.
 * All rights reserved.
 */
package com.isode.stroke.parser;

import com.isode.stroke.elements.Message;

public class MessageParser extends GenericStanzaParser<Message> {

    public MessageParser(PayloadParserFactoryCollection factories) {
        super(factories, new Message());
    }

    @Override
    void handleStanzaAttributes(AttributeMap attributes) {
        String type = attributes.getAttribute("type");
        if ("chat".equals(type)) {
            getStanzaGeneric().setType(Message.Type.Chat);
        } else if ("error".equals(type)) {
            getStanzaGeneric().setType(Message.Type.Error);
        } else if ("groupchat".equals(type)) {
            getStanzaGeneric().setType(Message.Type.Groupchat);
        } else if ("headline".equals(type)) {
            getStanzaGeneric().setType(Message.Type.Headline);
        } else {
            getStanzaGeneric().setType(Message.Type.Normal);
        }
    }
}
