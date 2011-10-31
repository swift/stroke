/*
 * Copyright (c) 2010 Remko Tronçon
 * Licensed under the GNU General Public License v3.
 * See Documentation/Licenses/GPLv3.txt for more information.
 */
/*
 * Copyright (c) 2010, Isode Limited, London, England.
 * All rights reserved.
 */
package com.isode.stroke.streamstack;

import java.util.ArrayList;
import java.util.List;

public class StreamStack {

    public StreamStack(XMPPLayer xmppLayer, LowLayer physicalLayer) {
        xmppLayer_ = xmppLayer;
        physicalLayer_ = physicalLayer;
        physicalLayer_.setParentLayer(xmppLayer_);
        xmppLayer.setChildLayer(physicalLayer_);
    }

    public void addLayer(final StreamLayer newLayer) {
        final LowLayer lowLayer = (layers_.isEmpty() ? physicalLayer_ : layers_.get(layers_.size() - 1));

	xmppLayer_.setChildLayer(newLayer);
        newLayer.setParentLayer(xmppLayer_);
        
        lowLayer.setParentLayer(newLayer);
        newLayer.setChildLayer(lowLayer);

	layers_.add(newLayer);
    }

    public XMPPLayer getXMPPLayer() {
        return xmppLayer_;
    }

    public Object getLayer(Class layerClass) {
        for (StreamLayer layer : layers_) {
            if (layerClass.isAssignableFrom(layer.getClass())) {
                return layer;
            }
        }
        return null;
    }

    private XMPPLayer xmppLayer_;
    private LowLayer physicalLayer_;
    private List<StreamLayer> layers_ = new ArrayList<StreamLayer>();
}
