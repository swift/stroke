/*
 * Copyright (c) 2010-2015 Isode Limited, London, England.
 * All rights reserved.
 */
package com.isode.stroke.network;

import com.isode.stroke.crypto.CryptoProvider;
import com.isode.stroke.tls.TLSContextFactory;

public interface NetworkFactories {

    TimerFactory getTimerFactory();
    ConnectionFactory getConnectionFactory();
    DomainNameResolver getDomainNameResolver();
    TLSContextFactory getTLSContextFactory();
    CryptoProvider getCryptoProvider();

}
