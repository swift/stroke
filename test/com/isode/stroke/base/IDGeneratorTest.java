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

package com.isode.stroke.base;

import static org.junit.Assert.assertTrue;
import org.junit.BeforeClass;
import org.junit.Test;
import com.isode.stroke.base.IDGenerator;
import java.util.*;

public class IDGeneratorTest {

	private Set<String> generatedIDs_ = new HashSet<String>();

	/**
	* Constructor
	*/
	public IDGeneratorTest() {

	}

	/**
	* Clears the set generatedIDs_.
	*/
	void setUp() {
		generatedIDs_.clear();
	}

	/**
	* Tests randomly generated UUID functionality 
	*/
	@Test
	public void testGenerate() {
		IDGenerator testling = new IDGenerator();
		for(int i = 0; i < 26*4; ++i) {
			String id = testling.generateID();
			assertTrue(generatedIDs_.add(id));
		}
	}
}