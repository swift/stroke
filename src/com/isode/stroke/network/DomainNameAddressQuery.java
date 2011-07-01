/*
 * Copyright (c) 2010, Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2010, Remko Tronçon.
 * All rights reserved.
 */

package com.isode.stroke.network;

import com.isode.stroke.signals.Signal2;
import java.util.Collection;

public abstract class DomainNameAddressQuery {
    public abstract void run();

    public final Signal2<Collection<HostAddress>, DomainNameResolveError> onResult = new Signal2<Collection<HostAddress>, DomainNameResolveError>();
}
