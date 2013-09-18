/*
 * Copyright (c) 2011-2013 Isode Limited, London, England.
 * All rights reserved.
 */
package com.isode.stroke.network;

import com.isode.stroke.eventloop.EventLoop;
import com.isode.stroke.tls.PlatformTLSFactories;
import com.isode.stroke.tls.TLSContextFactory;

public class JavaNetworkFactories implements NetworkFactories {

    public JavaNetworkFactories(EventLoop eventLoop) {
        eventLoop_ = eventLoop;
        timers_ = new JavaTimerFactory(eventLoop_);
        connections_ = new JavaConnectionFactory(eventLoop_);
        dns_ = new PlatformDomainNameResolver(eventLoop_);
        platformTLSFactories_ = new PlatformTLSFactories();
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
    
    private final EventLoop eventLoop_;
    private final JavaTimerFactory timers_;
    private final JavaConnectionFactory connections_;
    private final PlatformDomainNameResolver dns_;
    private final PlatformTLSFactories platformTLSFactories_;
}
