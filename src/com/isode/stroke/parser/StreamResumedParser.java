/*
 * Copyright (c) 2011 Remko Tronçon
 * Licensed under the GNU General Public License v3.
 * See Documentation/Licenses/GPLv3.txt for more information.
 */
/*
 * Copyright (c) 2011, Isode Limited, London, England.
 * All rights reserved.
 */
package com.isode.stroke.parser;

import com.isode.stroke.elements.StreamResumed;

class StreamResumedParser extends GenericElementParser<StreamResumed> {

    private int level = 0;
    private final static int TopLevel = 0;

    public StreamResumedParser() {
        super(StreamResumed.class);
    }

    @Override
    public void handleStartElement(String el, String ns, AttributeMap attributes) {
        if (level == TopLevel) {
            String handledStanzasCount = attributes.getAttributeValue("h");
            if (handledStanzasCount != null) {
                try {
                    getElementGeneric().setHandledStanzasCount(Long.parseLong(handledStanzasCount));
                } catch (NumberFormatException e) {
                }
            }
            getElementGeneric().setResumeID(attributes.getAttribute("previd"));
        }
        ++level;
    }

    @Override
    public void handleEndElement(String el, String ns) {
        --level;
    }
}
