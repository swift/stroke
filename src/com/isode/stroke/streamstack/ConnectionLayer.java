/*
 * Copyright (c) 2010-2015, Isode Limited, London, England.
 * All rights reserved.
 */
package com.isode.stroke.streamstack;

import com.isode.stroke.base.SafeByteArray;
import com.isode.stroke.network.Connection;
import com.isode.stroke.signals.Slot1;

public class ConnectionLayer implements LowLayer {

    public ConnectionLayer(Connection connection) {
        this.connection = connection;
        connection.onDataRead.connect(new Slot1<SafeByteArray>() {

            public void call(SafeByteArray p1) {
                writeDataToParentLayer(p1);
            }
        });
    }

    public void writeData(SafeByteArray data) {
        connection.write(data);
    }

    private Connection connection;

    /* Work around multiple inheritance workaround again */
    StreamLayer fakeStreamLayer_ = new StreamLayer() {

        public void writeData(SafeByteArray data) {
            connection.write(data);
        }

        public void handleDataRead(SafeByteArray data) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    };

    public HighLayer getParentLayer() {
        return fakeStreamLayer_.getParentLayer();
    }

    public void setParentLayer(HighLayer parentLayer) {
        fakeStreamLayer_.setParentLayer(parentLayer);
    }

    public void writeDataToParentLayer(SafeByteArray data) {
        fakeStreamLayer_.writeDataToParentLayer(data);
    }
}
