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
 * Because of the lack of multiple inheritance in Java, this implements
 * the abstract methods that should have been implemented in
 * LowLayer and HighLayer.
 */
public abstract class StreamLayer implements LowLayer, HighLayer {

    public HighLayer getParentLayer() {
        return parentLayer;
    }

    public void setParentLayer(final HighLayer parentLayer) {
        this.parentLayer = parentLayer;
    }

    public void writeDataToParentLayer(final ByteArray data) {
        assert parentLayer != null;
        parentLayer.handleDataRead(data);
    }

    public LowLayer getChildLayer() {
        return childLayer;
    }

    public void setChildLayer(final LowLayer childLayer) {
        this.childLayer = childLayer;
    }

    public void writeDataToChildLayer(final ByteArray data) {
        assert childLayer != null;
        childLayer.writeData(data);
    }

    private HighLayer parentLayer;
    private LowLayer childLayer;
}
