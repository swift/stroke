/*
 * Copyright (c) 2010-2015, Isode Limited, London, England.
 * All rights reserved.
 */
package com.isode.stroke.disco;

import com.isode.stroke.elements.DiscoInfo;
import com.isode.stroke.signals.Signal1;

public abstract class CapsProvider {
    abstract DiscoInfo getCaps(final String hash);

    public final Signal1<String> onCapsAvailable = new Signal1<String>();
}
