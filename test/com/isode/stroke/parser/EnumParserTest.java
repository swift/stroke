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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import com.isode.stroke.parser.EnumParser;

public class EnumParserTest {

	public EnumParserTest() {

	}

	public enum MyEnum {
		MyValue1,
		MyValue2,
		MyValue3
	};
		
	@Test
	public void testParse() {
		EnumParser parser = new EnumParser<MyEnum>();
		parser.addValue(MyEnum.MyValue1, "my-value-1");
		parser.addValue(MyEnum.MyValue2, "my-value-2");
		parser.addValue(MyEnum.MyValue3, "my-value-3");
		assertEquals(MyEnum.MyValue2, parser.parse("my-value-2"));
	}

	@Test
	public void testParse_NoValue() {
		EnumParser parser = new EnumParser<MyEnum>();
		parser.addValue(MyEnum.MyValue1, "my-value-1");
		parser.addValue(MyEnum.MyValue2, "my-value-2");
		parser.addValue(MyEnum.MyValue3, "my-value-3");
		assertNull(parser.parse("my-value-4"));
	}
}