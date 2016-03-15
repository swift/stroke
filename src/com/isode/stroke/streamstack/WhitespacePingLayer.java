/*
 * Copyright (c) 2010 Remko Tronçon
 * Licensed under the GNU General Public License v3.
 * See Documentation/Licenses/GPLv3.txt for more information.
 */
/*
 * Copyright (c) 2011-2016, Isode Limited, London, England.
 * All rights reserved.
 */
package com.isode.stroke.streamstack;

import java.util.logging.Logger;

import com.isode.stroke.base.SafeByteArray;
import com.isode.stroke.network.Timer;
import com.isode.stroke.network.TimerFactory;
import com.isode.stroke.signals.SignalConnection;
import com.isode.stroke.signals.Slot;

public class WhitespacePingLayer extends StreamLayer {

    private static final int TIMEOUT_MILLISECONDS = 60000;
    
    private final Logger logger = Logger.getLogger(this.getClass().getName());

    public WhitespacePingLayer(TimerFactory timerFactory) {
        isActive = false;
        timer = timerFactory.createTimer(TIMEOUT_MILLISECONDS);
        onTickConnection = timer.onTick.connect(new Slot() {
           public void call() {
               handleTimerTick();
           }
        });
    }
    
    @Override
    protected void finalize() throws Throwable {
        try {
            destroy();
        }
        finally {
            super.finalize();
        }
    }

    /**
     * This replaces the C++ destructor. After calling this object should not be used again.
     * If any methods are called after they may throw {@link NullPointerException}
     */
    public void destroy() {
        if (isActive && timer != null) {
            logger.finer("WhitespacePingLayer still active at destruction");
            timer.stop();
        }
        onTickConnection.disconnect();
        timer = null;
        isActive = false;
    }

    public void writeData(SafeByteArray data) {
        writeDataToChildLayer(data);
    }

    public void handleDataRead(SafeByteArray data) {
        writeDataToParentLayer(data);
    }

    private void handleTimerTick() {
        if (timer == null) {
            return;
        }
        timer.stop();
        writeDataToChildLayer(new SafeByteArray(" "));
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

    private final SignalConnection onTickConnection;
}
