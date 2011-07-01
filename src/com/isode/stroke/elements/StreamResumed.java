/*
 * Copyright (c) 2011, Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2011, Remko Tronçon.
 * All rights reserved.
 */
package com.isode.stroke.elements;

public class StreamResumed implements Element {

    public void setResumeID(String id) {
        resumeID = id;
    }

    public String getResumeID() {
        return resumeID;
    }

    public Long getHandledStanzasCount() {
        return handledStanzasCount;
    }

    public void setHandledStanzasCount(long i) {
        handledStanzasCount = i;
    }
    private String resumeID;
    private Long handledStanzasCount;
}
