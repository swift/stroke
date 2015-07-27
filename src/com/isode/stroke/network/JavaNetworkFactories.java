/*
 * Copyright (c) 2011-2015 Isode Limited, London, England.
 * All rights reserved.
 */
package com.isode.stroke.network;

import com.isode.stroke.crypto.CryptoProvider;
import com.isode.stroke.crypto.JavaCryptoProvider;
import com.isode.stroke.eventloop.EventLoop;
import com.isode.stroke.tls.PlatformTLSFactories;
import com.isode.stroke.tls.TLSContextFactory;
import com.isode.stroke.idn.IDNConverter;
import com.isode.stroke.idn.ICUConverter;

public class JavaNetworkFactories implements NetworkFactories {

    public JavaNetworkFactories(EventLoop eventLoop) {
        eventLoop_ = eventLoop;
        timers_ = new JavaTimerFactory(eventLoop_);
        connections_ = new JavaConnectionFactory(eventLoop_);
        platformTLSFactories_ = new PlatformTLSFactories();
        cryptoProvider_ = new JavaCryptoProvider();
        idnConverter_ = new ICUConverter();
        dns_ = new PlatformDomainNameResolver(idnConverter_, eventLoop_);
        proxyProvider_ = new JavaProxyProvider();
    }

    public TimerFactory getTimerFactory() {
        return timers_;
    }

    public ConnectionFactory getConnectionFactory() {
        return connections_;
    }

    public DomainNameResolver getDomainNameResolver() {
        return dns_;
    }
    
    public TLSContextFactory getTLSContextFactory() {
        return platformTLSFactories_.getTLSContextFactory();        
    }

    public ProxyProvider getProxyProvider() {
        return proxyProvider_;
    }

    public EventLoop getEventLoop() {
        return eventLoop_;
    }

    @Override
    public CryptoProvider getCryptoProvider() {
        return cryptoProvider_;
    }

    @Override
    public IDNConverter getIDNConverter() {
        return idnConverter_;
    }

    private final EventLoop eventLoop_;
    private final JavaTimerFactory timers_;
    private final JavaConnectionFactory connections_;
    private final PlatformDomainNameResolver dns_;
    private final PlatformTLSFactories platformTLSFactories_;
    private final ProxyProvider proxyProvider_;
    private final CryptoProvider cryptoProvider_;
    private final IDNConverter idnConverter_;   
}
