/*
 * Copyright (c) 2010-2015 Isode Limited.
 * All rights reserved.
 * See the COPYING file for more information.
 */
/*
 * Copyright (c) 2015 Tarun Gupta.
 * Licensed under the simplified BSD license.
 * See Documentation/Licenses/BSD-simplified.txt for more information.
 */

package com.isode.stroke.base;

import com.isode.stroke.base.IDGenerator;

/**
 * @brief The SimpleIDGenerator class implements a IDGenerator generating consecutive ID strings from
 * the lower case latin alphabet.
 */
public class SimpleIDGenerator extends IDGenerator {

	private static String currentID;

	/**
	* Constructor
	*/
	public SimpleIDGenerator() {
		currentID = "";
	}

	/**
	* Simply generates a UUID.
	* @return a String which will never be null or empty.
	*/
	public static String generateID() {
		boolean carry = true;
		int i = 0;
		char[] char_currentID = currentID.toCharArray();
		while (carry && i < char_currentID.length) {
			char c = char_currentID[i];
			if (c >= 'z') {
				char_currentID[i] = 'a';
			}
			else {
				char_currentID[i] = (char)(c+1);
				carry = false;
			}
			++i;
		}
		currentID = String.valueOf(char_currentID);
		if (carry) {
			currentID += 'a';
		}
		return currentID;
	}
}