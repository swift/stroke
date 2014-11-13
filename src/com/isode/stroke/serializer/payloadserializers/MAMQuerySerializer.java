package com.isode.stroke.serializer.payloadserializers;

import com.isode.stroke.elements.MAMQuery;
import com.isode.stroke.serializer.GenericPayloadSerializer;
import com.isode.stroke.serializer.xml.XMLElement;
import com.isode.stroke.serializer.xml.XMLRawTextNode;

public class MAMQuerySerializer extends GenericPayloadSerializer<MAMQuery> {

    public MAMQuerySerializer() {
        super(MAMQuery.class);
    }

    protected String serializePayload(MAMQuery payload) {
        if (payload == null) {
            return "";
        }
    
        XMLElement element = new XMLElement("query", "urn:xmpp:mam:0");
    
        if (payload.getQueryID() != null) {
            element.setAttribute("queryid", payload.getQueryID());
        }
        
        if (payload.getNode() != null) {
            element.setAttribute("node", payload.getNode());
        }
    
        if (payload.getForm() != null) {
            element.addNode(new XMLRawTextNode((new FormSerializer()).serialize(payload.getForm())));
        }
    
        if (payload.getResultSet() != null) {
            element.addNode(new XMLRawTextNode((new ResultSetSerializer()).serialize(payload.getResultSet())));
        }
    
        return element.serialize();
    }
}
