/*
 * Copyright (c) 2011, Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2010, Remko Tronçon.
 * All rights reserved.
 */
package com.isode.stroke.serializer;

import com.isode.stroke.elements.CompressRequest;
import com.isode.stroke.elements.Element;
import com.isode.stroke.base.SafeByteArray;

public class CompressRequestSerializer implements ElementSerializer {

    public CompressRequestSerializer() {
    }

    public SafeByteArray serialize(Element element) {
        CompressRequest compressRequest = (CompressRequest) element;
        return new SafeByteArray("<compress xmlns='http://jabber.org/protocol/compress'><method>" + compressRequest.getMethod() + "</method></compress>");
    }

    public boolean canSerialize(Element element) {
        return element instanceof CompressRequest;
    }
}
