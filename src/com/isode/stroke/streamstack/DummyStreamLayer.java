/*  Copyright (c) 2016, Isode Limited, London, England.
 *  All rights reserved.
 *
 *  Acquisition and use of this software and related materials for any
 *  purpose requires a written license agreement from Isode Limited,
 *  or a written license from an organisation licensed by Isode Limited
 *  to grant such a license.
 *
 */
package com.isode.stroke.streamstack;

import com.isode.stroke.base.SafeByteArray;

/**
 *  The {@link DummyStreamLayer} can be used to use a {@link LowLayer} on its own, 
 *  without a functioning parent layer. The {@link DummyStreamLayer} will serve as the 
 *  parent layer to the {@link LowLayer} and is called when the {@link LowLayer} wants 
 *  to write data to its parent layer.
 */
public class DummyStreamLayer implements HighLayer {

    private LowLayer childLayer;
    
    public DummyStreamLayer(LowLayer lowLayer) {
        childLayer = lowLayer;
        childLayer.setParentLayer(this);
    }
    
    @Override
    public void handleDataRead(SafeByteArray data) {
        // Empty Method
    }

    @Override
    public LowLayer getChildLayer() {
        return childLayer;
    }

    @Override
    public void setChildLayer(LowLayer childLayer) {
        this.childLayer = childLayer;
    }

    @Override
    public void writeDataToChildLayer(SafeByteArray data) {
        if (childLayer != null) {
            childLayer.writeData(data);
        }
    }

}
