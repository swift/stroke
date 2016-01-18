/*
 * Copyright (c) 2013 Isode Limited.
 * All rights reserved.
 * See the COPYING file for more information.
 */
/*
 * Copyright (c) 2015 Tarun Gupta.
 * Licensed under the simplified BSD license.
 * See Documentation/Licenses/BSD-simplified.txt for more information.
 */

package com.isode.stroke.base;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public class Listenable<T> {

	private Set<T> listeners = new CopyOnWriteArraySet<T>();

	public void addListener(T listener) {
		listeners.add(listener);
	}

	public void removeListener(T listener) {
		while(listeners.contains(listener)) {
			listeners.remove(listener);
		}
	}
	
	// Swiften code takes advantage of boost binding
	// which we can't do in java.  Easiest solution seems
	// to be to pass listener to the caller to handle which
	// method should be called
	public void notifyListeners(ListenableCallback<? super T> callback) {
	    for (T listener : listeners) {
	        callback.call(listener);
	    }
	}
	
	/**
	 * Callback for subclasses to specify how the listers should be
	 * called
	 * @param <T> Type of listener that can registered with the {@link Listenable}
	 */
	public static interface ListenableCallback<S> {
	    
	    /**
	     * Called by the parent {@link Listenable} class for all
	     * registered listeners.
	     * @param listener A registered listener. May be {@code null}
	     * if a {@code null} was registered as a listener.
	     */
	    public void call(S listener);
	    
	}
}