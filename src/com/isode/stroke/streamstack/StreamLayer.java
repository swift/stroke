/*
 * Copyright (c) 2010 Remko Tronçon
 * All rights reserved.
 */
/*
 * Copyright (c) 2010-2012, Isode Limited, London, England.
 * All rights reserved.
 */

package com.isode.stroke.streamstack;

import com.isode.stroke.base.SafeByteArray;

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

    public void writeDataToParentLayer(final SafeByteArray data) {
        assert parentLayer != null;
        parentLayer.handleDataRead(data);
    }

    public LowLayer getChildLayer() {
        return childLayer;
    }

    public void setChildLayer(final LowLayer childLayer) {
        this.childLayer = childLayer;
    }

    public void writeDataToChildLayer(final SafeByteArray data) {
        assert childLayer != null;
        childLayer.writeData(data);
    }
    @Override
    public String toString() {
        String className = this.getClass().getSimpleName();

        // Include actual StreamLayer type based on class name of the object
        return className + 
        "; " +
        " parentLayer: " + parentLayer +
        "; childLayer: " + childLayer;       
    }

    private HighLayer parentLayer;
    private LowLayer childLayer;
}
