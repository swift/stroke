/*
 * Copyright (c) 2010, Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2010, Remko Tronçon.
 * All rights reserved.
 */
package com.isode.stroke.parser;

import java.util.HashMap;

/**
 * XML element attributes.
 */
public class AttributeMap extends HashMap<String, String> {
    public String getAttribute(String attribute) {
        return this.get(attribute);
    }

    public String getAttributeValue(String attribute) {
        return this.containsKey(attribute) ? this.get(attribute) : null;
    }

    public boolean getBoolAttribute(String attribute) {
        return getBoolAttribute(attribute, false);
    }

    public boolean getBoolAttribute(String attribute, boolean defaultValue) {
        String value = getAttribute(attribute);
        return "true".equals(value) || "1".equals(value);
    }
}
