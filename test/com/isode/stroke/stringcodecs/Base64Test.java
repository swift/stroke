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

package com.isode.stroke.stringcodecs;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.junit.Before;
import com.isode.stroke.base.ByteArray;
import com.isode.stroke.stringcodecs.Base64;

public class Base64Test {

	@Test
	public void testEncodeDecodeAllChars() {
		ByteArray input = new ByteArray();
		for (int i = 0; i < 255; ++i) {
			char c = (char)i;
			input.append((byte)c);
		}
		String result = Base64.encode(input);

		assertEquals(("AAECAwQFBgcICQoLDA0ODxAREhMUFRYXGBkaGxwdHh8gISIjJCUmJygpKissLS4vMDEyMzQ1Njc4OTo7PD0+P0BBQkNERUZHSElKS0xNTk9QUVJTVFVWV1hZWltcXV5fYGFiY2RlZmdoaWprbG1ub3BxcnN0dXZ3eHl6e3x9fn+AgYKDhIWGh4iJiouMjY6PkJGSk5SVlpeYmZqbnJ2en6ChoqOkpaanqKmqq6ytrq+wsbKztLW2t7i5uru8vb6/wMHCw8TFxsfIycrLzM3Oz9DR0tPU1dbX2Nna29zd3t/g4eLj5OXm5+jp6uvs7e7v8PHy8/T19vf4+fr7/P3+"), result);
		assertEquals(input, Base64.decode(result));
	}

	@Test
	public void testEncodeDecodeOneBytePadding() {
		ByteArray input = new ByteArray("ABCDE");

		String result = Base64.encode(input);

		assertEquals(("QUJDREU="), result);
		assertEquals(input, Base64.decode(result));
	}

	@Test
	public void testEncodeDecodeTwoBytesPadding() {
		ByteArray input = new ByteArray("ABCD");

		String result = Base64.encode(input);

		assertEquals(("QUJDRA=="), result);
		assertEquals(input, Base64.decode(result));
	}

	@Test
	public void testEncode_NoData() {
		String result = (Base64.encode(new ByteArray()));
		assertEquals((""), result);
	}

	@Test
	public void testDecode_NoData() {
		ByteArray result = (Base64.decode(""));
		assertEquals(new ByteArray(), result);
	}
}