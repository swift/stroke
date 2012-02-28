/*
 * Copyright (c) 2012 Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2010 Remko Tronçon.
 * All rights reserved.
 */
package com.isode.stroke.tls;

import com.isode.stroke.tls.java.JSSEContextFactory;

public class PlatformTLSFactories {
    public TLSContextFactory getTLSContextFactory() {        
        return new JSSEContextFactory();
    }

    public CertificateFactory getCertificateFactory() {
        /*FIXME: Implement*/
        return null;
    }
}
