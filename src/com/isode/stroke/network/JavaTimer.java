/*
 * Copyright (c) 2010 Remko Tronçon
 * Licensed under the GNU General Public License v3.
 * See Documentation/Licenses/GPLv3.txt for more information.
 */
/*
 * Copyright (c) 2010, Isode Limited, London, England.
 * All rights reserved.
 */
package com.isode.stroke.network;

import com.isode.stroke.eventloop.Event;
import com.isode.stroke.eventloop.EventLoop;

class JavaTimer extends Timer {

    private class TimerRunnable implements Runnable {

        boolean running_ = true;
        private final EventLoop eventLoop_;
        private final int milliseconds_;

        public TimerRunnable(EventLoop eventLoop, int milliseconds) {
            eventLoop_ = eventLoop;
            milliseconds_ = milliseconds;
        }

        public void run() {
            while (shouldEmit()) {
                try {
                    Thread.sleep(milliseconds_);
                } catch (InterruptedException ex) {
                    /* If we were interrupted, either emit or don't, based on whether stop was called.*/
                }
                if (shouldEmit()) {
                    eventLoop_.postEvent(new Event.Callback() {
                        public void run() {
                            onTick.emit();
                        }
                    });
                }
            }
        }


        synchronized boolean shouldEmit() {
            return running_;
        }

        public synchronized void stop() {
            running_ = false;
        }
    }

    public JavaTimer(EventLoop eventLoop, int milliseconds) {
        timer_ = new TimerRunnable(eventLoop, milliseconds);
    }

    @Override
    public void start() {
        Thread thread = (new Thread(timer_));
        thread.setDaemon(true);
        thread.start();
    }

    @Override
    public void stop() {
        timer_.stop();
        //FIXME: This needs to clear any remaining events out of the EventLoop queue.
    }
    private final TimerRunnable timer_;
}
