/*
 * Copyright (c) 2012, Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2010, Remko Tronçon
 * All rights reserved.
 */
package com.isode.stroke.queries;

import java.util.Vector;

import com.isode.stroke.elements.IQ;

/**
 * Dummy IQ Channel
 *
 */
public class DummyIQChannel extends IQChannel {

    /**
     * Constructor 
     */
    public DummyIQChannel() {}

    @Override
    public void sendIQ(IQ iq) {
        iqs_.add(iq);
    }

    @Override
    public String getNewIQID() {
        return "test-id";
    }

    @Override
    public boolean isAvailable() {
        return true;
    }

    public Vector<IQ> iqs_ = new Vector<IQ>();
}
