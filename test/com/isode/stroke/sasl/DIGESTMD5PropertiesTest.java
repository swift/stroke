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

package com.isode.stroke.sasl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.junit.Before;
import com.isode.stroke.sasl.DIGESTMD5Properties;
import com.isode.stroke.base.ByteArray;

public class DIGESTMD5PropertiesTest {

	@Test
	public void testParse() {
		DIGESTMD5Properties properties = DIGESTMD5Properties.parse(new ByteArray(
				"realm=\"myrealm1\",realm=\"myrealm2\",nonce=\"mynonce\","
			+	"algorithm=md5-sess,charset=utf-8"));

		assertNotNull(properties.getValue("realm"));
		assertEquals(("myrealm1"), properties.getValue("realm"));
		assertNotNull(properties.getValue("nonce"));
		assertEquals(("mynonce"), properties.getValue("nonce"));
		assertNotNull(properties.getValue("algorithm"));
		assertEquals(("md5-sess"), properties.getValue("algorithm"));
		assertNotNull(properties.getValue("charset"));
		assertEquals(("utf-8"), properties.getValue("charset"));
	}

	@Test
	public void testSerialize() {
		DIGESTMD5Properties properties = new DIGESTMD5Properties();
		properties.setValue("authzid", "myauthzid");
		properties.setValue("charset", "utf-8");
		properties.setValue("cnonce", "mycnonce");
		properties.setValue("digest-uri", "mydigesturi");
		properties.setValue("nc", "1");
		properties.setValue("nonce", "mynonce");
		properties.setValue("qop", "auth");
		properties.setValue("realm", "myrealm");
		properties.setValue("response", "myresponse");
		properties.setValue("username", "myuser");

		ByteArray result = properties.serialize();
		ByteArray expected = new ByteArray("authzid=\"myauthzid\",charset=utf-8,cnonce=\"mycnonce\",digest-uri=\"mydigesturi\",nc=1,nonce=\"mynonce\",qop=auth,realm=\"myrealm\",response=myresponse,username=\"myuser\"");
		assertEquals(expected.toString(), result.toString());
	}
}