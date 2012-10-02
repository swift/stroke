/*
 * Copyright (c) 2010-2012, Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2010, Remko Tronçon.
 * All rights reserved.
 */

package com.isode.stroke.queries;

import com.isode.stroke.elements.IQ;

/**
 * Thing reacting to IQs.
 */
public interface IQHandler {
    public boolean handleIQ(IQ iq);
}
