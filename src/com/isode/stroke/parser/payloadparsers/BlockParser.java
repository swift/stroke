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

import com.isode.stroke.elements.AbstractBlockPayload;
import com.isode.stroke.elements.BlockListPayload;
import com.isode.stroke.elements.BlockPayload;
import com.isode.stroke.elements.Payload;
import com.isode.stroke.elements.UnblockPayload;
import com.isode.stroke.jid.JID;
import com.isode.stroke.parser.AttributeMap;
import com.isode.stroke.parser.GenericPayloadParser;


/**
 * Base class for parser that pass Block pay loads such as 
 * {@link BlockPayload}, {@link BlockListPayload} and {@link UnblockPayload}
 * @param <T> Type of {@link Payload} that will be parsed.
 */
public abstract class BlockParser<T extends AbstractBlockPayload> extends GenericPayloadParser<T> {
    /*
     * Note this is slightly different to Swiften code as templates in C++ work
     * different to Java Generics.  In Swiften there exits a BlockParser template
     * for which instances can be created of type <BlockPayload>, <BlockListPayload> and
     * <UnblockPayload>.  In Java as we need to pass an instance of the class to the constructor
     * (which we can't do with generics) we need to create specific sub types for each of 
     * the different Block payloads.
     */
    
    private int level = 0;
    
    /**
     * Constructor
     * @param payload New instance of T to create the parsed
     * version from.
     */
    protected BlockParser(T payload) {
        super(payload);
    }

    @Override
    public void handleStartElement(String element, String ns,
            AttributeMap attributes) {
        if (level == 1 && "item".equals(element)) {
            JID jid = new JID(attributes.getAttribute("jid"));
            if (jid.isValid()) {
                AbstractBlockPayload payload = getPayloadInternal();
                payload.addItem(jid);
            }
        }
        ++level;
    }

    @Override
    public void handleEndElement(String element, String ns) {
        --level;
    }

    @Override
    public void handleCharacterData(String data) {
        // Empty method
    }

}
