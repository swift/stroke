/*
 * Copyright (c) 2010, Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2010, Remko Tronçon.
 * All rights reserved.
 */

package com.isode.stroke.elements;

/**
 * Resource priority from presence stanzas.
 */
public class Priority extends Payload {
    private int priority_ = 0;

    public Priority(){

    }

    public Priority (int priority) {
        priority_ = priority;
    }

    public void setPriority(int priority) {
        priority_ = priority;
    }

    public int getPriority() {
        return priority_;
    }
}
