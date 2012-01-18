/*  Copyright (c) 2012, Isode Limited, London, England.
 *  All rights reserved.
 *
 *  Acquisition and use of this software and related materials for any
 *  purpose requires a written licence agreement from Isode Limited,
 *  or a written licence from an organisation licensed by Isode Limited Limited
 *  to grant such a licence.
 *
 */
 
package com.isode.stroke.tls.java;

import com.isode.stroke.tls.TLSContext;
import com.isode.stroke.tls.TLSContextFactory;

/**
 * Concrete implementation of a TLSContextFactory which uses SSLEngine 
 * and maybe other stuff? ..tbs...
 * 
 */
public class JSSEContextFactory implements TLSContextFactory {

    @Override
    public boolean canCreate() {
        return true;
    }

    @Override
    public TLSContext createTLSContext() {
        return new JSSEContext();
    }

}
