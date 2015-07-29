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

import java.util.Vector;
import com.isode.stroke.signals.Slot;
import com.isode.stroke.signals.Slot1;
import com.isode.stroke.signals.Slot2;
import com.isode.stroke.signals.Slot3;

public class Listenable<T> {

	private Vector<T> listeners = new Vector<T>();

	public void addListener(T listener) {
		listeners.add(listener);
	}

	public void removeListener(T listener) {
		while(listeners.contains(listener)) {
			listeners.remove(listener);
		}
	}

	//Swiften code calls event(i), which is not yet done.
	public void notifyListeners(Slot event) {
		for (T i : listeners) {
			event.call();
		}
	}

	//Swiften code calls event(i), which is not yet done.
	public <T1> void notifyListeners(Slot1<T1> event, T1 p1) {
		for (T i : listeners) {
			event.call(p1);
		}
	}

	public <T1, T2> void notifyListeners(Slot2<T1, T2> event, T1 p1, T2 p2) {
		for (T i : listeners) {
			event.call(p1, p2);
		}
	}

	public <T1, T2, T3> void notifyListeners(Slot3<T1, T2, T3> event, T1 p1, T2 p2, T3 p3) {
		for (T i : listeners) {
			event.call(p1, p2, p3);
		}
	}
}