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
    public abstract DomainNameServiceQuery createServiceQuery(String name);
    public abstract DomainNameAddressQuery createAddressQuery(String name);

    protected String getNormalized(String domain) {
        return domain;
        //FIXME: port idna
//        char* output;
//	if (idna_to_ascii_8z(domain.getUTF8Data(), &output, 0) == IDNA_SUCCESS) {
//		String result(output);
//		free(output);
//		return result;
//	}
//	else {
//		return domain;
//	}
    }
}
