/*
 * Copyright (c) 2011, Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2010, Remko Tronçon.
 * All rights reserved.
 */

package com.isode.stroke.tls;

public interface TLSContextFactory {
    boolean canCreate();
    TLSContext createTLSContext(TLSOptions tlsOptions);
    //void setCheckCertificateRevocation(boolean b);
	//void setDisconnectOnCardRemoval(boolean b);
}
