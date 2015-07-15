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

package com.isode.stroke.component;

import com.isode.stroke.component.Component;
import com.isode.stroke.component.CoreComponent;
import com.isode.stroke.base.SafeByteArray;
import com.isode.stroke.signals.Slot1;

public class ComponentXMLTracer {

	public ComponentXMLTracer(CoreComponent client) {
		client.onDataRead.connect(new Slot1<SafeByteArray>() {
			@Override
			public void call(SafeByteArray b1) {
				printData('<', b1);
			}
		});

		client.onDataWritten.connect(new Slot1<SafeByteArray>() {
			@Override
			public void call(SafeByteArray b1) {
				printData('>', b1);
			}
		});
	}

	private static void printData(char direction, final SafeByteArray data) {
		printLine(direction);
		System.err.println(data);
	}

	private static void printLine(char c) {
		for (int i = 0; i < 80; ++i) {
			System.err.print(c);
		}
		System.err.println();
	}
}