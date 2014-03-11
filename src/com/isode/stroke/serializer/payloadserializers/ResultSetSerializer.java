/*
* Copyright (c) 2014 Kevin Smith and Remko Tronçon
* All rights reserved.
*/

/*
* Copyright (c) 2014, Isode Limited, London, England.
* All rights reserved.
*/

package com.isode.stroke.serializer.payloadserializers;

import com.isode.stroke.elements.ResultSet;
import com.isode.stroke.serializer.GenericPayloadSerializer;
import com.isode.stroke.serializer.xml.XMLElement;

public class ResultSetSerializer extends GenericPayloadSerializer<ResultSet> {

    public ResultSetSerializer() {
        super(ResultSet.class);
    }

    protected String serializePayload(ResultSet payload) {
        if (payload == null) {
            return "";
        }
    
        XMLElement element = new XMLElement("set", "http://jabber.org/protocol/rsm");
    
        if (payload.getMaxItems() != null) {
            element.addNode(new XMLElement("max", "", payload.getMaxItems().toString()));
        }
    
        if (payload.getCount() != null) {
            element.addNode(new XMLElement("count", "", payload.getCount().toString()));
        }
    
        if (payload.getFirstID() != null) {
            XMLElement firstElement = new XMLElement("first", "", payload.getFirstID());
            if (payload.getFirstIDIndex() != null) {
                firstElement.setAttribute("index", payload.getFirstIDIndex().toString());
            }
            element.addNode(firstElement);
        }
    
        if (payload.getLastID() != null) {
            element.addNode(new XMLElement("last", "", payload.getLastID()));
        }
    
        if (payload.getAfter() != null) {
            element.addNode(new XMLElement("after", "", payload.getAfter()));
        }
        
        if (payload.getBefore() != null) {
            element.addNode(new XMLElement("before", "", payload.getBefore()));
        }
    
        return element.serialize();
    }
}
