/*
 * Copyright (c) 2010 Remko Tronçon
 * All rights reserved.
 */
/*
 * Copyright (c) 2010 Isode Limited, London, England.
 * All rights reserved.
 */

package com.isode.stroke.elements;


public class SoftwareVersion extends Payload {
    private String name_ = "";
    private String version_ = "";
    private String os_ = "";

    public SoftwareVersion(final String name, final String version, final String os) {
        name_ = name;
        version_ = version;
        os_ = os;
    }

    public SoftwareVersion() {
        this("","","");
    }

    public SoftwareVersion(final String name) {
        this(name, "", "");
    }

    public SoftwareVersion(final String name, final String version) {
        this(name, version, "");
    }

    public String getName() {
        return name_;
    }

    public String getVersion() {
        return version_;
    }

    public String getOS() {
        return os_;
    }

    public void setName(final String name) {
        name_ = name;
    }

    public void setVersion(final String version) {
        version_ = version;
    }

    public void setOS(final String os) {
        os_ = os;
    }
}
