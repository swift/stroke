/*
 * Copyright (c) 2010-2011, Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2010, Remko Tronçon.
 * All rights reserved.
 */
package com.isode.stroke.parser;

import com.isode.stroke.elements.StreamFeatures;

class StreamFeaturesParser extends GenericElementParser<StreamFeatures> {

    public StreamFeaturesParser() {
        super(StreamFeatures.class);
    }

    @Override
    public void handleStartElement(String element, String ns, AttributeMap unused) {
        if (currentDepth_ == 1) {
            if (element.equals("starttls") && ns.equals("urn:ietf:params:xml:ns:xmpp-tls")) {
                getElementGeneric().setHasStartTLS();
            } else if (element.equals("session") && ns.equals("urn:ietf:params:xml:ns:xmpp-session")) {
                getElementGeneric().setHasSession();
            } else if (element.equals("bind") && ns.equals("urn:ietf:params:xml:ns:xmpp-bind")) {
                getElementGeneric().setHasResourceBind();
            } else if (element.equals("sm") && ns.equals("urn:xmpp:sm:2")) {
                getElementGeneric().setHasStreamManagement();
            } else if (element.equals("mechanisms") && ns.equals("urn:ietf:params:xml:ns:xmpp-sasl")) {
                inMechanisms_ = true;
            } else if (element.equals("compression") && ns.equals("http://jabber.org/features/compress")) {
                inCompression_ = true;
            }
        } else if (currentDepth_ == 2) {
            if (inCompression_ && element.equals("method")) {
                inCompressionMethod_ = true;
                currentText_ = "";
            } else if (inMechanisms_ && element.equals("mechanism")) {
                inMechanism_ = true;
                currentText_ = "";
            }
        }
        ++currentDepth_;
    }

    @Override
    public void handleEndElement(String unused1, String unused2) {
        --currentDepth_;
        if (currentDepth_ == 1) {
            inCompression_ = false;
            inMechanisms_ = false;
        } else if (currentDepth_ == 2) {
            if (inCompressionMethod_) {
                getElementGeneric().addCompressionMethod(currentText_);
                inCompressionMethod_ = false;
            } else if (inMechanism_) {
                getElementGeneric().addAuthenticationMechanism(currentText_);
                inMechanism_ = false;
            }
        }
    }

    @Override
    public void handleCharacterData(String data) {
        currentText_ = currentText_ + data;
    }
    private int currentDepth_ = 0;
    private String currentText_ = "";
    private boolean inMechanisms_ = false;
    private boolean inMechanism_ = false;
    private boolean inCompression_ = false;
    private boolean inCompressionMethod_ = false;
}
