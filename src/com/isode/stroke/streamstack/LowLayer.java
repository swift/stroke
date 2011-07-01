/*
 * Copyright (c) 2010 Remko Tronçon
 * Licensed under the GNU General Public License v3.
 * See Documentation/Licenses/GPLv3.txt for more information.
 */
/*
 * Copyright (c) 2010-2011, Isode Limited, London, England.
 * All rights reserved.
 */
package com.isode.stroke.streamstack;

import com.isode.stroke.base.ByteArray;

/**
 * Because of the lack of multiple inheritance in Java, this has to be done
 * slightly differently from Swiften. What happens is that the methods in Swiften
 * are provided abstract here, and implemented in the StreamLayer instead.
 */
public interface LowLayer {

    void writeData(ByteArray data);

    /* Should be protected */

    HighLayer getParentLayer();

    void setParentLayer(HighLayer parentLayer);

    void writeDataToParentLayer(ByteArray data);
}
