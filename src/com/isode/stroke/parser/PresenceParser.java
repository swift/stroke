/*
 * Copyright (c) 2010, Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2010, Remko Tronçon.
 * All rights reserved.
 */
package com.isode.stroke.parser;

import com.isode.stroke.elements.Presence;

public class PresenceParser extends GenericStanzaParser<Presence> {

    public PresenceParser(PayloadParserFactoryCollection factories) {
        super(factories, new Presence());
    }

    @Override
    void handleStanzaAttributes(AttributeMap attributes) {
        String type = attributes.getAttribute("type");
        if (type != null) {
            if (type.equals("unavailable")) {
                getStanzaGeneric().setType(Presence.Type.Unavailable);
            } else if (type.equals("probe")) {
                getStanzaGeneric().setType(Presence.Type.Probe);
            } else if (type.equals("subscribe")) {
                getStanzaGeneric().setType(Presence.Type.Subscribe);
            } else if (type.equals("subscribed")) {
                getStanzaGeneric().setType(Presence.Type.Subscribed);
            } else if (type.equals("unsubscribe")) {
                getStanzaGeneric().setType(Presence.Type.Unsubscribe);
            } else if (type.equals("unsubscribed")) {
                getStanzaGeneric().setType(Presence.Type.Unsubscribed);
            } else if (type.equals("error")) {
                getStanzaGeneric().setType(Presence.Type.Error);
            } else {
                getStanzaGeneric().setType(Presence.Type.Available);
            }
        } else {
            getStanzaGeneric().setType(Presence.Type.Available);
        }
    }
}
