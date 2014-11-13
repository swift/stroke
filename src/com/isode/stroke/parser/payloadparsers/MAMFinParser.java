/*
* Copyright (c) 2014 Kevin Smith and Remko Tronçon
* All rights reserved.
*/

/*
* Copyright (c) 2014, Isode Limited, London, England.
* All rights reserved.
*/

package com.isode.stroke.parser.payloadparsers;

import com.isode.stroke.elements.MAMFin;
import com.isode.stroke.elements.Payload;
import com.isode.stroke.elements.ResultSet;
import com.isode.stroke.parser.AttributeMap;
import com.isode.stroke.parser.GenericPayloadParser;

public class MAMFinParser extends GenericPayloadParser<MAMFin> {

    private static final String QUERYID_ATTRIBUTE = "queryid";

    private static final String STABLE_ATTRIBUTE = "stable";

    private static final String COMPLETE_ATTRIBUTE = "complete";
    
    private static final String SET_ELEMENT = "set";
    
    private static final String SET_NS = "http://jabber.org/protocol/rsm";

    private static final int TOP_LEVEL = 0;
    
    private static final int PAYLOAD_LEVEL = 1;
    
    private ResultSetParser resultSetParser_ = null;
    
    private int level_ = 0;

    public MAMFinParser() {
        super(new MAMFin());
    }

    @Override
    public void handleStartElement(String element, String ns,
            AttributeMap attributes) {
        if (level_ == TOP_LEVEL) {
            MAMFin internalPayload = getPayloadInternal();
            internalPayload.setComplete(attributes.getBoolAttribute(COMPLETE_ATTRIBUTE, false));
            internalPayload.setStable(attributes.getBoolAttribute(STABLE_ATTRIBUTE, true));
            String idAttributeValue = attributes.getAttributeValue(QUERYID_ATTRIBUTE);
            if (idAttributeValue != null) {
                internalPayload.setQueryID(idAttributeValue);
            }
        }
        else if (level_ == PAYLOAD_LEVEL) {
            if (element == SET_ELEMENT && ns == SET_NS) {
                resultSetParser_ = new ResultSetParser();
            }
        }
        
        if (resultSetParser_ != null) {
            // Parsing a nested result set
            resultSetParser_.handleStartElement(element, ns, attributes);
        }
        
        ++level_;
    }

    @Override
    public void handleEndElement(String element, String ns) {
        --level_;
        
        if (resultSetParser_ != null && level_ >= PAYLOAD_LEVEL) {
            resultSetParser_.handleEndElement(element, ns);
        }
        
        if (resultSetParser_ != null && level_ == PAYLOAD_LEVEL) {
            MAMFin internalPayload = getPayloadInternal();
            Payload resultSetParserPayload = resultSetParser_.getPayload();
            if (resultSetParserPayload instanceof ResultSet) {
                internalPayload.setResultSet((ResultSet) resultSetParser_.getPayload());
            }
            resultSetParser_ = null;
        }
    }

    @Override
    public void handleCharacterData(String data) {
        if (resultSetParser_ != null) {
            resultSetParser_.handleCharacterData(data);
        }
    }

}
