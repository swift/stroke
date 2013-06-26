/*
 * Copyright (c) 2011-2013, Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2011, Kevin Smith.
 * All rights reserved.
 */

package com.isode.stroke.elements;

public class Last extends Payload {
    Long seconds_;

    public Last() {}

    public Last(final Long seconds) {
        setSeconds(seconds);
    }

    public void setSeconds(final Long seconds) {
        seconds_ = seconds;
    }

    public Long getSeconds() {
        return seconds_;
    }
}
