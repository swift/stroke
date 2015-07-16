/*
 * Copyright (c) 2010, Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2010, Remko Tronçon.
 * All rights reserved.
 */
package com.isode.stroke.serializer;

import com.isode.stroke.elements.Element;
import com.isode.stroke.elements.Stanza;
import com.isode.stroke.serializer.xml.XMLElement;

public abstract class GenericStanzaSerializer<T extends Stanza> extends StanzaSerializer {

    private final Class stanzaClass_;

    public GenericStanzaSerializer(Class stanzaClass, String tag, PayloadSerializerCollection payloadSerializers) {
        this(stanzaClass, tag, payloadSerializers, null);
    }

    public GenericStanzaSerializer(Class stanzaClass, String tag, PayloadSerializerCollection payloadSerializers, String explicitNS) {
        super(tag, payloadSerializers, explicitNS);
        stanzaClass_ = stanzaClass;
    }

    public boolean canSerialize(Element element) {
        return stanzaClass_.isAssignableFrom(element.getClass());
    }

    public void setStanzaSpecificAttributes(Element stanza, XMLElement element) {
        setStanzaSpecificAttributesGeneric((T)stanza, element);
    }

    abstract void setStanzaSpecificAttributesGeneric(T stanza, XMLElement element);
}
