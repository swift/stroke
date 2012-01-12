/*
 * Copyright (c) 2012, Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2010, Remko Tronçon.
 * All rights reserved.
 */
package com.isode.stroke.parser;

/*
 * Internal parsing class.
 */
public class Attribute {

    public Attribute(String name, String ns) {
        this.name = name;
        this.ns = ns;
    }

    public String getName() {
        return name;
    }

    public String getNamespace() {
        return ns;
    }

    @Override
    public boolean equals(Object other) {
        return other != null
                && other instanceof Attribute
                && name.equals(((Attribute) other).getName())
                && ns.equals(((Attribute) other).getNamespace());
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 19 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 19 * hash + (this.ns != null ? this.ns.hashCode() : 0);
        return hash;
    }
    private final String name;
    private final String ns;
}
