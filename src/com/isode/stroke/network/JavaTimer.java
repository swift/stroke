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

import java.util.Date;

import com.isode.stroke.eventloop.Event;
import com.isode.stroke.eventloop.EventLoop;

class JavaTimer extends Timer {

    private class TimerRunnable implements Runnable {

        boolean running_ = true;
        private final EventLoop eventLoop_;
        private final long milliseconds_;

        public TimerRunnable(EventLoop eventLoop, long milliseconds) {
            eventLoop_ = eventLoop;
            milliseconds_ = milliseconds;
        }

        public void run() {
            long endTime = new Date().getTime() + milliseconds_;
            while (shouldEmit() && new Date().getTime() < endTime) {
                try {
                    long timeToWait = endTime - new Date().getTime();
                    if (timeToWait > 0) {
                        Thread.sleep(milliseconds_);
                    }
                } catch (InterruptedException ex) {
                    // Needs to be caught, but won't break out of the loop
                    // unless end time reached or stop() has been called.
                }
            } 
            if (shouldEmit()) {
                eventLoop_.postEvent(new Event.Callback() {
                    public void run() {
                        onTick.emit();
                    }
                });
            }            
        }


        synchronized boolean shouldEmit() {
            return running_;
        }

        public synchronized void stop() {
            running_ = false;
        }
    }

    /**
     * Create a new JavaTimer
     * @param eventLoop the caller's EventLoop. Should not be null.
     * @param milliseconds length of delay.
     */
    public JavaTimer(EventLoop eventLoop, long milliseconds) {
        timer_ = new TimerRunnable(eventLoop, milliseconds);
    }

    /**
     * Start the timer running. The timer will expire and generate a signal
     * after the specified delay, unless {@link #stop()} has been called.
     */
    @Override
    public void start() {
        Thread thread = (new Thread(timer_));
        thread.setDaemon(true);
        thread.start();
    }

    /**
     * Cancel the timer. No signal will be generated.
     */
    @Override
    public void stop() {
        timer_.stop();
        //FIXME: This needs to clear any remaining events out of the EventLoop queue.
    }
    
    @Override
    public String toString() {
        return "JavaTimer for " + timer_.milliseconds_ + 
        " milliseconds " + 
        (timer_.running_ ? "running" : "not running"); 
    }
    private final TimerRunnable timer_;
}
