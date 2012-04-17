/*
 * Copyright (c) 2012, Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2010, Kevin Smith
 * All rights reserved.
 */
package com.isode.stroke.elements;

import java.util.Vector;

/**
 * Class representing MUC Admin Payload
 *
 */
public class MUCAdminPayload extends Payload {

    /**
     * Create the object 
     */
    public MUCAdminPayload() {
    }

    /**
     * Add a MUC Item to the payload
     * @param item MUC Item to be added, not null
     */
    public void addItem(MUCItem item) {
        items_.add(item);
    }

    /**
     * Get the MUC Items from the payload
     * @return list of MUC items, can be empty but not null
     */
    public Vector<MUCItem> getItems() {
        return items_;
    }

    private Vector<MUCItem> items_ = new Vector<MUCItem>();
}
