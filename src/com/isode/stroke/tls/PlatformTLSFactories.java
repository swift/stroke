/*
 * Copyright (c) 2012-2013 Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2010 Remko Tronçon.
 * All rights reserved.
 */
package com.isode.stroke.tls;

import com.isode.stroke.tls.java.JSSEContextFactory;

public class PlatformTLSFactories {
    private JSSEContextFactory contextFactory = new JSSEContextFactory();
    
    public TLSContextFactory getTLSContextFactory() {        
        return contextFactory;
    }

    public CertificateFactory getCertificateFactory() {
        /*FIXME: Implement*/
        return null;
    }
}
