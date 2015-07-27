/*
 * Copyright (c) 2010, Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2010, Remko Tronçon.
 * All rights reserved.
 */

package com.isode.stroke.network;


public abstract class DomainNameResolver {

    public abstract DomainNameServiceQuery createServiceQuery(String serviceLookupPrefix, String domain);
    public abstract DomainNameAddressQuery createAddressQuery(String name);
}
