/*
 * Copyright (c) 2010, Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2010, Remko Tronçon.
 * All rights reserved.
 */

package com.isode.stroke.serializer;

import com.isode.stroke.elements.Element;
import com.isode.stroke.base.SafeByteArray;

public interface ElementSerializer {
    SafeByteArray serialize(Element element);
    boolean canSerialize(Element element);
}
