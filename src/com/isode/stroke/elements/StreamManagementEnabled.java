/*
 * Copyright (c) 2011, Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2010, Remko Tronçon.
 * All rights reserved.
 */
package com.isode.stroke.elements;

public class StreamManagementEnabled implements Element {

    public void setResumeSupported() {
        resumeSupported = true;
    }

    public boolean getResumeSupported() {
        return resumeSupported;
    }

    public void setResumeID(String id) {
        resumeID = id;
    }

    public String getResumeID() {
        return resumeID;
    }
    private boolean resumeSupported;
    private String resumeID;
}
