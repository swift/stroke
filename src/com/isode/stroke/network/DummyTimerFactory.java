/*
 * Copyright (c) 2010 Isode Limited.
 * All rights reserved.
 * See the COPYING file for more information.
 */
/*
 * Copyright (c) 2015 Tarun Gupta.
 * Licensed under the simplified BSD license.
 * See Documentation/Licenses/BSD-simplified.txt for more information.
 */

package com.isode.stroke.network;

import java.util.List;
import java.util.ArrayList;

public class DummyTimerFactory implements TimerFactory {

	private int currentTime;
	private List<DummyTimer> timers = new ArrayList<DummyTimer>();

	public class DummyTimer extends Timer {

		public DummyTimer(long timeout, DummyTimerFactory factory) {
			this.timeout = timeout;
			this.factory = factory;
			this.isRunning = false;
			this.startTime = 0;
		}

		public void start() {
			isRunning = true;
			startTime = factory.currentTime;
		}

		public void stop() {
			isRunning = false;
		}

		public long getAlarmTime() {
			return startTime + timeout;
		}
		
		public long timeout;
		public DummyTimerFactory factory;
		public boolean isRunning;
		public long startTime;
	};

	public DummyTimerFactory() {
		this.currentTime = 0;
	}

	public Timer createTimer(long milliseconds) {
		DummyTimer timer = new DummyTimer(milliseconds, this);
		timers.add(timer);
		return timer;
	}

	public void setTime(int time) {
		assert(time > currentTime);
		for(DummyTimer timer : timers) {
			if (timer.getAlarmTime() > currentTime && timer.getAlarmTime() <= time && timer.isRunning) {
				timer.onTick.emit();
			}
		}
		currentTime = time;
	}
}