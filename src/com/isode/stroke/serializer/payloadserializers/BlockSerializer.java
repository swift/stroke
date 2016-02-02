/*  Copyright (c) 2016, Isode Limited, London, England.
 *  All rights reserved.
 *
 *  Acquisition and use of this software and related materials for any
 *  purpose requires a written license agreement from Isode Limited,
 *  or a written license from an organisation licensed by Isode Limited
 *  to grant such a license.
 *
 */
package com.isode.stroke.serializer.payloadserializers;

import com.isode.stroke.elements.AbstractBlockPayload;
import com.isode.stroke.jid.JID;
import com.isode.stroke.serializer.GenericPayloadSerializer;
import com.isode.stroke.serializer.xml.XMLElement;

/**
 * Abstract class for serializing pay loads of type {@link AbstractBlockPayload}
 * 
 * @param <T> Type of {@link AbstractBlockPayload} to serialize
 */
public abstract class BlockSerializer<T extends AbstractBlockPayload> extends GenericPayloadSerializer<T> {
    /*
     * Note this is slightly different to Swiften code as templates in C++ work
     * different to Java Generics.  In Swiften there exits a BlockSerialize template
     * for which instances can be created of type <BlockPayload>, <BlockListPayload> and
     * <UnblockPayload>.  In Java as we need to pass an instance of the class to the constructor
     * (which we can't do with generics) we need to create specific sub types for each of 
     * the different Block payloads.
     */
    
    private final String tag;
    
    /**
     * Constructor
     * @param c Class of the {@link AbstractBlockPayload} that this will serialize
     * @param tag Tag for the XML element to serialize
     */
    protected BlockSerializer(Class<T> c,String tag) {
        super(c);
        this.tag = tag;
    }

    @Override
    protected String serializePayload(T payload) {
        XMLElement element = new XMLElement(tag,"urn:xmpp:blocking");
        for (JID jid : payload.getItems()) {
            XMLElement item = new XMLElement("item");
            item.setAttribute("jid", jid.toString());
            element.addNode(item);
        }
        return element.serialize();
    }

}
