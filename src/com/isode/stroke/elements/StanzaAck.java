/*
 * Copyright (c) 2010, Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2010, Remko Tronçon.
 * All rights reserved.
 */
package com.isode.stroke.elements;

public class StanzaAck implements Element {
    //FIXME: parser/serialiser
    public StanzaAck() {
    }

    public StanzaAck(long handledStanzasCount) {
        valid = true;
        this.handledStanzasCount = handledStanzasCount;
    }

    public long getHandledStanzasCount() {
        return handledStanzasCount;
    }

    public void setHandledStanzasCount(long i) {
        handledStanzasCount = i;
        valid = true;
    }

    public boolean isValid() {
        return valid;
    }
    private boolean valid = false;
    private long handledStanzasCount = 0;
}
