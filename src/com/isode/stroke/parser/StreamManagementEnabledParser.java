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

import com.isode.stroke.elements.StreamManagementEnabled;

class StreamManagementEnabledParser extends GenericElementParser<StreamManagementEnabled> {

    private int level = 0;
    private final static int TopLevel = 0;

    public StreamManagementEnabledParser() {
        super(StreamManagementEnabled.class);
    }

    @Override
    public void handleStartElement(String el, String ns, AttributeMap attributes) {
        if (level == TopLevel) {
            if (attributes.getBoolAttribute("resume", false)) {
                getElementGeneric().setResumeSupported();
            }
            getElementGeneric().setResumeID(attributes.getAttribute("id"));
        }
        ++level;
    }

    @Override
    public void handleEndElement(String el, String ns) {
        --level;
    }
}
