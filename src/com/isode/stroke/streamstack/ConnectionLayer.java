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

import com.isode.stroke.base.ByteArray;
import com.isode.stroke.network.Connection;
import com.isode.stroke.signals.Signal1;
import com.isode.stroke.signals.Slot1;

public class ConnectionLayer implements LowLayer {

    public ConnectionLayer(Connection connection) {
        this.connection = connection;
        connection.onDataRead.connect(new Slot1<ByteArray>() {

            public void call(ByteArray p1) {
                writeDataToParentLayer(p1);
            }
        });
    }

    public void writeData(ByteArray data) {
        connection.write(data);
    }

    private Connection connection;

    /* Work around multiple inheritance workaround again */
    StreamLayer fakeStreamLayer_ = new StreamLayer() {

        public void writeData(ByteArray data) {
            connection.write(data);
        }

        public void handleDataRead(ByteArray data) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    };

    public HighLayer getParentLayer() {
        return fakeStreamLayer_.getParentLayer();
    }

    public void setParentLayer(HighLayer parentLayer) {
        fakeStreamLayer_.setParentLayer(parentLayer);
    }

    public void writeDataToParentLayer(ByteArray data) {
        fakeStreamLayer_.writeDataToParentLayer(data);
    }
}
