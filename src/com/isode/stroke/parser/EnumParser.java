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

package com.isode.stroke.parser;

import java.util.Map;
import java.util.HashMap;

public class EnumParser<T> {

	private Map<String, T> values = new HashMap<String, T>();

	public EnumParser() {

	}

	public EnumParser addValue(T value, String text) {
		values.put(text, value);
		return this;
	}

	public T parse(String value) {
		if(values.containsKey(value)) {
			return values.get(value);
		} else {
			return null;
		}
	}
}