/*
 * Copyright (c) 2012-2016 Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2010 Remko Tronçon.
 * All rights reserved.
 */
package com.isode.stroke.tls;

import com.isode.stroke.tls.java.JSSEContextFactory;

public class PlatformTLSFactories {
    private final JSSEContextFactory contextFactory = new JSSEContextFactory();
    private final CertificateFactory certificateFactory = new JavaCertificateFactory();
    
    public TLSContextFactory getTLSContextFactory() {        
        return contextFactory;
    }

    public CertificateFactory getCertificateFactory() {
        return certificateFactory;
    }
}
