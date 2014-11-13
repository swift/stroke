/*
* Copyright (c) 2014-2015, Isode Limited, London, England.
* All rights reserved.
*/

package com.isode.stroke.parser.payloadparsers;

import com.isode.stroke.elements.Form;
import com.isode.stroke.elements.MAMQuery;
import com.isode.stroke.elements.ResultSet;
import com.isode.stroke.parser.AttributeMap;
import com.isode.stroke.parser.GenericPayloadParser;

public class MAMQueryParser extends GenericPayloadParser<MAMQuery> {

    public MAMQueryParser() {
        super(new MAMQuery());
    }

    public void handleStartElement(String element, String ns, AttributeMap attributes) {
        if (level_ == 0) {
            MAMQuery payloadInternal = getPayloadInternal();
            String queryIDValue = attributes.getAttributeValue("queryid");
            if (queryIDValue != null) {
                payloadInternal.setQueryID(queryIDValue);
            }
            String nodeValue = attributes.getAttributeValue("node");
            if (nodeValue != null) {
                payloadInternal.setNode(nodeValue);
            }
        } else if (level_ == 1) {
            if ("x".equals(element) && "jabber:x:data".equals(ns)) {
                formParser_ = new FormParser();
            } else if ("set".equals(element) && "http://jabber.org/protocol/rsm".equals(ns)) {
                resultSetParser_ = new ResultSetParser();
            }
        }
    
        if (formParser_ != null) { /* parsing a nested Form */
            formParser_.handleStartElement(element, ns, attributes);
        }
    
        if (resultSetParser_ != null) { /* parsing a nested ResultSet */
            resultSetParser_.handleStartElement(element, ns, attributes);
        }
    
        ++level_;
    }
    
    public void handleEndElement(String element, String ns) {
        --level_;
    
        if (formParser_ != null && level_>= 1) {
            formParser_.handleEndElement(element, ns);
        }
        if (formParser_ != null && level_==1) { /* done parsing nested Form? */
            getPayloadInternal().setForm((Form)formParser_.getPayload());
            formParser_ = null;
        }
    
        if (resultSetParser_ != null && level_>= 1) {
            resultSetParser_.handleEndElement(element, ns);
        }
        if (resultSetParser_ != null && level_==1) { /* done parsing nested ResultSet? */
            getPayloadInternal().setResultSet((ResultSet)resultSetParser_.getPayload());
            resultSetParser_ = null;
        }
    }
    
    public void handleCharacterData(String data) {
        if (formParser_ != null) {
            formParser_.handleCharacterData(data);
        }
        if (resultSetParser_ != null) {
            resultSetParser_.handleCharacterData(data);
        }
    }

    private FormParser formParser_;
    private ResultSetParser resultSetParser_;
    private int level_;
}
