/*  Copyright (c) 2016, Isode Limited, London, England.
 *  All rights reserved.
 *
 *  Acquisition and use of this software and related materials for any
 *  purpose requires a written license agreement from Isode Limited,
 *  or a written license from an organisation licensed by Isode Limited
 *  to grant such a license.
 *
 */
package com.isode.stroke.elements;

import java.util.Vector;

import com.isode.stroke.jid.JID;

/**
 * Parent abstract class for the Block pay load classes {@link BlockPayload},
 * {@link BlockListPayload} and {@link UnblockPayload}.
 */
public abstract class AbstractBlockPayload extends Payload {
    /*
     * Note this is slightly different to Swiften code as templates in C++ work
     * different to Java Generics.  In Swiften there exits a BlockParser template
     * for which instances can be created of type <BlockPayload>, <BlockListPayload> and
     * <UnblockPayload>.  To get this to work in java we have to create a parent
     * abstract class for all these Block elements types.
     */
    
    
    /**
     * Constructor
     */
    protected AbstractBlockPayload() {
        super();
    }
    
    /**
     * Adds an item
     * @param item item, not {@code null}
     */
    public abstract void addItem(JID item);
    
    /**
     * Gets the items
     * @return items, NotNull.
     */
    public abstract Vector<JID> getItems();

}
