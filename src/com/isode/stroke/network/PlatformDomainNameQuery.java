/*
 * Copyright (c) 2010 Isode Limited.
 * All rights reserved.
 * See the COPYING file for more information.
 */
/*
 * Copyright (c) 2015 Tarun Gupta.
 * Licensed under the simplified BSD license.
 * See Documentation/Licenses/BSD-simplified.txt for more information.
 */

package com.isode.stroke.network;

public class PlatformDomainNameQuery {

	private PlatformDomainNameResolver resolver;

	protected PlatformDomainNameResolver getResolver() {
		return resolver;
	}

	public PlatformDomainNameQuery(PlatformDomainNameResolver resolver) {
		this.resolver = resolver;
	}

	public void runBlocking() {
		
	}
}