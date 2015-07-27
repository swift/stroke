/*
 * Copyright (c) 2010, Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2010, Remko Tronçon.
 * All rights reserved.
 */

package com.isode.stroke.network;

import com.isode.stroke.signals.Signal;

public abstract class Timer {

	/**
	 * Starts the timer.
	 *
	 * After the given period, onTick() will be called.
	 */	
    public abstract void start();

	/**
	 * Cancels the timer.
	 *
	 * If the timer was started, onTick() will no longer be called.
	 */    
    public abstract void stop();

	/**
	 * Emitted when the timer expires.
	 */    
    public final Signal onTick = new Signal();
}
