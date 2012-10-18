/*
 * Copyright (c) 2012, Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2011, Kevin Smith
 * All rights reserved.
 */
package com.isode.stroke.parser.tree;

import com.isode.stroke.parser.AttributeMap;

/**
 * Class representing a Null Parser element
 *
 */
public class NullParserElement extends ParserElement{
    /**
     * Create the object 
     */
    public NullParserElement() {
        super("", "", new AttributeMap());
    }

    /**
     * Empty/Null Parser element 
     */
    public static final NullParserElement element = new NullParserElement();
}
