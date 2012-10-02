/*
 * Copyright (c) 2010 Remko Tronçon
 * All rights reserved.
 */
/*
 * Copyright (c) 2010 Isode Limited, London, England.
 * All rights reserved.
 */

package com.isode.stroke.serializer.payloadserializers;

import com.isode.stroke.elements.SoftwareVersion;
import com.isode.stroke.serializer.GenericPayloadSerializer;

public class SoftwareVersionSerializer extends GenericPayloadSerializer<SoftwareVersion>{

    public SoftwareVersionSerializer() {
        super(SoftwareVersion.class);
    }

    @Override
    protected String serializePayload(SoftwareVersion version) {
        StringBuilder result = new StringBuilder();
        result.append("<query xmlns=\"jabber:iq:version\">");
        if (version.getName() != null && version.getName().length() > 0) {
                result.append("<name>").append(version.getName()).append("</name>");
        }
        if (version.getVersion() != null && version.getVersion().length() > 0) {
                result.append("<version>").append(version.getVersion()).append("</version>");
        }
        if (version.getOS() != null && version.getOS().length() > 0) {
                result.append("<os>").append(version.getOS()).append("</os>");
        }
        result.append("</query>");
        return result.toString();

    }

}
