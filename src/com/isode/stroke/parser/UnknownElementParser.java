/*
 * Copyright (c) 2010-2011, Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2010, Remko Tronçon.
 * All rights reserved.
 */

package com.isode.stroke.parser;

import com.isode.stroke.elements.UnknownElement;

public class UnknownElementParser extends GenericElementParser<UnknownElement> {
    public UnknownElementParser() {
        super(UnknownElement.class);
    }
}
