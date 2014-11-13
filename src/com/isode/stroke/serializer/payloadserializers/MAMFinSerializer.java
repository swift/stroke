package com.isode.stroke.serializer.payloadserializers;

import com.isode.stroke.elements.MAMFin;
import com.isode.stroke.elements.ResultSet;
import com.isode.stroke.serializer.GenericPayloadSerializer;
import com.isode.stroke.serializer.xml.XMLElement;
import com.isode.stroke.serializer.xml.XMLRawTextNode;

public class MAMFinSerializer extends GenericPayloadSerializer<MAMFin> {

    public MAMFinSerializer() {
        super(MAMFin.class);
    }

    @Override
    protected String serializePayload(MAMFin payload) {
        if (payload == null) {
            return "";
        }
        
        XMLElement element = new XMLElement("fin","urn:xmpp:mam:0");
        
        if (payload.isComplete()) {
            element.setAttribute("complete", "true");
        }
        if (!payload.isStable()) {
            element.setAttribute("stable", "false");
        }
        String queryIDValue = payload.getQueryID();
        if (queryIDValue != null) {
            element.setAttribute("queryid", queryIDValue);
        }
        ResultSet resultSet = payload.getResultSet();
        if (resultSet != null) {
            ResultSetSerializer resultSetSerialized = new ResultSetSerializer();
            String serializedResultSet = resultSetSerialized.serialize(resultSet);
            XMLRawTextNode rawTextNode = new XMLRawTextNode(serializedResultSet);
            element.addNode(rawTextNode);
        }
        
        return element.serialize();
    }

}
