/*
 * Copyright (c) 2010 Remko Tronçon
 * Licensed under the GNU General Public License v3.
 * See Documentation/Licenses/GPLv3.txt for more information.
 */
/*
 * Copyright (c) 2011, Isode Limited, London, England.
 * All rights reserved.
 */
package com.isode.stroke.streamstack;

import com.isode.stroke.base.ByteArray;
import com.isode.stroke.network.Timer;
import com.isode.stroke.network.TimerFactory;
import com.isode.stroke.signals.Slot;

public class WhitespacePingLayer extends StreamLayer {

    private static final int TIMEOUT_MILLISECONDS = 60000;

    public WhitespacePingLayer(TimerFactory timerFactory) {
        isActive = false;
        timer = timerFactory.createTimer(TIMEOUT_MILLISECONDS);
        timer.onTick.connect(new Slot() {
           public void call() {
               handleTimerTick();
           }
        });
    }

    public void writeData(ByteArray data) {
        writeDataToChildLayer(data);
    }

    public void handleDataRead(ByteArray data) {
        writeDataToParentLayer(data);
    }

    private void handleTimerTick() {
        timer.stop();
        writeDataToChildLayer(new ByteArray(" "));
        timer.start();
    }

    public void setActive() {
        isActive = true;
        timer.start();
    }

    public void setInactive() {
        timer.stop();
        isActive = false;
    }

    public boolean getIsActive() {
        return isActive;
    }

    private boolean isActive;
    private Timer timer;
}
