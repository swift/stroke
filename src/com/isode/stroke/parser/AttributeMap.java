/*
 * Copyright (c) 2010-2015, Isode Limited, London, England.
 * All rights reserved.
 */
package com.isode.stroke.parser;

import com.isode.stroke.base.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * XML element attributes.
 */
public class AttributeMap {

    /**
     * Internal class.
     */
    public class Entry {

        public Entry(Attribute attribute, String value) {
            NotNull.exceptIfNull(attribute, "attribute");
            NotNull.exceptIfNull(value, "value");
            this.attribute = attribute;
            this.value = value;
        }

        public Attribute getAttribute() {
            return attribute;
        }

        public String getValue() {
            return value;
        }
        private final Attribute attribute;
        private final String value;
    };

    public AttributeMap() {
    }

    /** Not null */
    public String getAttribute(String attribute) {
        NotNull.exceptIfNull(attribute, "attribute");
        return getAttribute(attribute, "");
    }

    /** Not null*/
    public String getAttribute(String attribute, String ns) {
        String value = getInternal(attribute, ns);
        return value != null ? value : "";
    }

    /**
     *
     * @param attribute attribute name.
     * @return boolean value, defaulting to false if missing.
     */
    public boolean getBoolAttribute(String attribute) {
        return getBoolAttribute(attribute, false);
    }

    public boolean getBoolAttribute(String attribute, boolean defaultValue) {
        String value = getInternal(attribute, "");
        if (value == null) {
            return defaultValue;
        }
        return "true".equals(value) || "1".equals(value);
    }

    /** @return Attribute or null if missing.*/
    public String getAttributeValue(String attribute) {
        return getInternal(attribute, "");
    }

    /**
     *
     * @param name Attribute name (non-null).
     * @param ns Attribute namespace (non-null).
     * @param value Attribute value (non-null).
     */
    public void addAttribute(String name, String ns, String value) {
        NotNull.exceptIfNull(name, "name");
        NotNull.exceptIfNull(ns, "ns");
        NotNull.exceptIfNull(value, "value");
        attributes.add(new Entry(new Attribute(name, ns), value));
    }

    /**
     * Internal method (used for unit tests).
     */
    public List<Entry> getEntries() {
        return new ArrayList<Entry>(attributes);
    }

    private String getInternal(String name, String ns) {
        Attribute attribute = new Attribute(name, ns);
        for (Entry entry : attributes) {
            if (entry.getAttribute().equals(attribute)) {
                return entry.value;
            }
        }
        return null;
    }
    private final List<Entry> attributes = new ArrayList<Entry>();
}
