/*
 * Copyright (c) 2010, Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2010, Remko Tronçon.
 * All rights reserved.
 */

package com.isode.stroke.queries;

import com.isode.stroke.elements.IQ;
import com.isode.stroke.signals.Signal1;

public abstract class IQChannel {

    public abstract void sendIQ(IQ iq);

    public abstract String getNewIQID();

    public abstract boolean isAvailable();

    public final Signal1<IQ> onIQReceived = new Signal1<IQ>();
}
