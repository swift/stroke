/*
 * Copyright (c) 2010, Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2010, Remko Tronçon.
 * All rights reserved.
 */

package com.isode.stroke.client;

/**
 *
 */
public class IDGenerator {
    private int next_ = 42;
    public String generateID() {
        next_++;
        return String.valueOf(next_);

    }
}
