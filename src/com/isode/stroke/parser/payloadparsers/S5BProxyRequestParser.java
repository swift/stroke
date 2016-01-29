/*  Copyright (c) 2016, Isode Limited, London, England.
 *  All rights reserved.
 *                                                                       
 *  Acquisition and use of this software and related materials for any      
 *  purpose requires a written license agreement from Isode Limited,
 *  or a written license from an organisation licensed by Isode Limited
 *  to grant such a license.
 *
 */
package com.isode.stroke.parser.payloadparsers;

import com.isode.stroke.elements.S5BProxyRequest;
import com.isode.stroke.elements.S5BProxyRequest.StreamHost;
import com.isode.stroke.jid.JID;
import com.isode.stroke.parser.AttributeMap;
import com.isode.stroke.parser.GenericPayloadParser;

/**
 * S5BProxyRequestParaser
 */
public class S5BProxyRequestParser extends
        GenericPayloadParser<S5BProxyRequest> {

    private boolean parseActivate = false;
    
    private final StringBuilder activeJIDBuilder = new StringBuilder();
    
    /**
     * Constructor
     */
    public S5BProxyRequestParser() {
        super(new S5BProxyRequest());
    }

    @Override
    public void handleStartElement(String element, String ns,
            AttributeMap attributes) {
        if ("streamhost".equals(element)) {
            String hostValue = attributes.getAttributeValue("host");
            String jidValue = attributes.getAttributeValue("jid");
            String portValue = attributes.getAttributeValue("port");
            if (hostValue != null && jidValue != null && portValue != null) {
                int port = -1;
                try {
                    port = Integer.parseInt(portValue);
                }
                catch (NumberFormatException nfe) {
                    port = -1;
                }
                JID jid = new JID(jidValue);
                if (!hostValue.isEmpty() && port != -1 && jid.isValid()) {
                    StreamHost streamHost = new StreamHost();
                    streamHost.host = hostValue;
                    streamHost.port = port;
                    streamHost.jid = jid;
                    getPayloadInternal().setStreamHost(streamHost);
                }
            }
        }
        else if ("active".equals(element)) {
            parseActivate = true;
        }
        else if ("query".equals(element)) {
            String sidValue = attributes.getAttributeValue("sid");
            if (sidValue != null) {
                getPayloadInternal().setSID(sidValue);
            };
        }
        
    }

    @Override
    public void handleEndElement(String element, String ns) {
        if ("activate".equals(element)) {
            JID active = new JID(activeJIDBuilder.toString());
            if (active.isValid()) {
                getPayloadInternal().setActivate(active);
            }
            parseActivate = false;
        }
        
    }

    @Override
    public void handleCharacterData(String data) {
        if (parseActivate) {
            activeJIDBuilder.append(data);
        }
    }

}
