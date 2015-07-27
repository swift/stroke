/*
 * Copyright (c) 2012-2015 Isode Limited.
 * All rights reserved.
 * See the COPYING file for more information.
 */
/*
 * Copyright (c) 2015 Tarun Gupta.
 * Licensed under the simplified BSD license.
 * See Documentation/Licenses/BSD-simplified.txt for more information.
 */

package com.isode.stroke.network;

import com.isode.stroke.network.DomainNameResolver;
import com.isode.stroke.network.StaticDomainNameResolver;
import com.isode.stroke.eventloop.EventLoop;

/*
 * FIXME: Does not do any caching yet.
 */
public class CachingDomainNameResolver extends DomainNameResolver {

	private DomainNameResolver realResolver;

	public CachingDomainNameResolver(DomainNameResolver realResolver, EventLoop eventLoop) {
		this.realResolver = realResolver;
	}

	public DomainNameServiceQuery createServiceQuery(final String serviceLookupPrefix, final String domain) {
		//TODO: Cache
		return realResolver.createServiceQuery(serviceLookupPrefix, domain);
	}

	public DomainNameAddressQuery createAddressQuery(final String name) {
		//TODO: Cache
		return realResolver.createAddressQuery(name);
	}
}