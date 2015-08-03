/*
 * Copyright (c) 2011 Isode Limited.
 * All rights reserved.
 * See the COPYING file for more information.
 */
/*
 * Copyright (c) 2015 Tarun Gupta.
 * Licensed under the simplified BSD license.
 * See Documentation/Licenses/BSD-simplified.txt for more information.
 */

package com.isode.stroke.jingle;

import com.isode.stroke.elements.JingleContentPayload;
import com.isode.stroke.elements.Payload;
import java.util.Vector;

public class Jingle {
    
	public static <T extends Payload> JingleContentPayload getContentWithDescription(final Vector<JingleContentPayload> contents, T payload) {
		for (JingleContentPayload jingleContentPayload : contents) {
			if (jingleContentPayload.getDescription(payload) != null) {
				return jingleContentPayload;
			}
		}
		return null;
	}
}
