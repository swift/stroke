/*
 * Copyright (c) 2011 Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2010 Remko Tronçon.
 * All rights reserved.
 */
package com.isode.stroke.network;

public interface NetworkFactories {

    TimerFactory getTimerFactory();
    ConnectionFactory getConnectionFactory();
    DomainNameResolver getDomainNameResolver();

}
