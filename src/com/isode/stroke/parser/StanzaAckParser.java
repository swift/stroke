/*
 * Copyright (c) 2010 Remko Tronçon
 * Licensed under the GNU General Public License v3.
 * See Documentation/Licenses/GPLv3.txt for more information.
 */
/*
 * Copyright (c) 2011, Isode Limited, London, England.
 * All rights reserved.
 */
package com.isode.stroke.parser;

import com.isode.stroke.elements.StanzaAck;

class StanzaAckParser extends GenericElementParser<StanzaAck> {

    private int depth = 0;

    public StanzaAckParser() {
        super(StanzaAck.class);
    }

    @Override
    public void handleStartElement(String el, String ns, AttributeMap attributes) {
        if (depth == 0) {
            String handledStanzasString = attributes.getAttribute("h");
            try {
                getElementGeneric().setHandledStanzasCount(Long.parseLong(handledStanzasString));
            } catch (NumberFormatException e) {
                
            }
        }
        ++depth;
    }

    @Override
    public void handleEndElement(String el, String ns) {
        --depth;
    }
}
