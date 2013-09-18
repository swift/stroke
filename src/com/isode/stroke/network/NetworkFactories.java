/*
 * Copyright (c) 2011-2013 Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2010 Remko Tronçon.
 * All rights reserved.
 */
package com.isode.stroke.network;

import com.isode.stroke.tls.TLSContextFactory;

public interface NetworkFactories {

    TimerFactory getTimerFactory();
    ConnectionFactory getConnectionFactory();
    DomainNameResolver getDomainNameResolver();
    TLSContextFactory getTLSContextFactory();

}
