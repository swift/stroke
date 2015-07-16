/*
 * Copyright (c) 2010-2015, Isode Limited, London, England.
 * All rights reserved.
 */
package com.isode.stroke.serializer;

import com.isode.stroke.elements.Element;
import com.isode.stroke.elements.Payload;
import com.isode.stroke.elements.Stanza;
import com.isode.stroke.serializer.xml.XMLElement;
import com.isode.stroke.serializer.xml.XMLRawTextNode;
import java.util.logging.Logger;
import com.isode.stroke.base.SafeByteArray;

public abstract class StanzaSerializer implements ElementSerializer {

    private final String tag_;
    private final PayloadSerializerCollection payloadSerializers_;
    private String explicitDefaultNS_;
    private final Logger logger_ = Logger.getLogger(this.getClass().getName());

    public StanzaSerializer(String tag, PayloadSerializerCollection payloadSerializers) {
        this(tag, payloadSerializers, null);
    }

    public StanzaSerializer(String tag, PayloadSerializerCollection payloadSerializers, String explicitNS) {
        payloadSerializers_ = payloadSerializers;
        tag_ = tag;
        explicitDefaultNS_ = explicitNS;
    }

    public SafeByteArray serialize(Element element) {
        if (explicitDefaultNS_ != null) {
            return serialize(element, explicitDefaultNS_);
        }
        else {
            return serialize(element, "");
        }
    }

    public SafeByteArray serialize(Element element, String xmlns) {
        assert element != null;
        assert payloadSerializers_ != null;
        Stanza stanza = (Stanza) element;
        XMLElement stanzaElement = new XMLElement(tag_, (explicitDefaultNS_ != null) ? explicitDefaultNS_ : xmlns);
        if (stanza.getFrom() != null && stanza.getFrom().isValid()) {
            stanzaElement.setAttribute("from", stanza.getFrom().toString());
        }
        if (stanza.getTo() != null && stanza.getTo().isValid()) {
            stanzaElement.setAttribute("to", stanza.getTo().toString());
        }
        if (stanza.getID() != null && (stanza.getID().length()!=0)) {
            stanzaElement.setAttribute("id", stanza.getID());
        }
        setStanzaSpecificAttributes(stanza, stanzaElement);

        StringBuilder serializedPayloads = new StringBuilder();
        for (Payload payload : stanza.getPayloads()) {
            PayloadSerializer serializer = payloadSerializers_.getPayloadSerializer(payload);
            if (serializer != null) {
                serializedPayloads.append(serializer.serialize(payload));
            } else {
                /*TODO: port*/
                assert false;
                throw new UnsupportedOperationException("No serializer for payload: " + payload.getClass().getSimpleName());
                //std::cerr << "Could not find serializer for " << typeid(*(payload.get())).name() << std::endl;
            }
        }
        if (serializedPayloads.toString().length()!=0) {
            stanzaElement.addNode(new XMLRawTextNode(serializedPayloads.toString()));
        }
        return new SafeByteArray(stanzaElement.serialize());
    }

    public abstract void setStanzaSpecificAttributes(Element element, XMLElement xmlElement);
}
