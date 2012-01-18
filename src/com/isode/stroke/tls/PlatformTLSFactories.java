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
        // TODO: JSSEContextFactory is implemented, and so uncommenting
        // this line will result in the client attempting TLS handshakes, but
        // other support is required inside CoreClient etc. and so for the
        // moment we just return null
        //return new JSSEContextFactory();
        return null;
    }

    public CertificateFactory getCertificateFactory() {
        /*FIXME: Implement*/
        return null;
    }
}
