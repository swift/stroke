/*
 * Copyright (c) 2010-2015 Isode Limited, London, England.
 * All rights reserved.
 */
package com.isode.stroke.network;

import com.isode.stroke.crypto.CryptoProvider;
import com.isode.stroke.tls.TLSContextFactory;
import com.isode.stroke.idn.IDNConverter;
import com.isode.stroke.eventloop.EventLoop;

public interface NetworkFactories {

    TimerFactory getTimerFactory();
    ConnectionFactory getConnectionFactory();
    DomainNameResolver getDomainNameResolver();
    TLSContextFactory getTLSContextFactory();
    ProxyProvider getProxyProvider();
	EventLoop getEventLoop();    
    CryptoProvider getCryptoProvider();
    IDNConverter getIDNConverter();
}
