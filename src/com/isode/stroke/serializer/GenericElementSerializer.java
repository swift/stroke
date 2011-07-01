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

public abstract class GenericElementSerializer<T> implements ElementSerializer {

    private final Class elementClass_;

    GenericElementSerializer(Class elementClass) {
        elementClass_ = elementClass;
    }

    public boolean canSerialize(Element element) {
        return elementClass_.isAssignableFrom(element.getClass());
    }


}