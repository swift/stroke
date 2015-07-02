/*
 * Copyright (c) 2010-2013 Isode Limited.
 * All rights reserved.
 * See the COPYING file for more information.
 */
/*
 * Copyright (c) 2015 Tarun Gupta.
 * Licensed under the simplified BSD license.
 * See Documentation/Licenses/BSD-simplified.txt for more information.
 */

package com.isode.stroke.disco;

import com.isode.stroke.crypto.CryptoProvider;
import com.isode.stroke.crypto.JavaCryptoProvider;
import com.isode.stroke.elements.DiscoInfo;
import com.isode.stroke.elements.Form;
import com.isode.stroke.elements.FormField;
import com.isode.stroke.elements.CapsInfo;
import com.isode.stroke.disco.CapsInfoGenerator;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.junit.Before;

public class CapsInfoGeneratorTest {

	private CryptoProvider crypto;

	@Before
	public void setUp() {
		crypto = new JavaCryptoProvider();
	}

	@Test
	public void testGenerate_XEP0115SimpleExample() {
		DiscoInfo discoInfo = new DiscoInfo();
		discoInfo.addIdentity(new DiscoInfo.Identity("Exodus 0.9.1", "client", "pc"));
		discoInfo.addFeature("http://jabber.org/protocol/disco#items");
		discoInfo.addFeature("http://jabber.org/protocol/caps");
		discoInfo.addFeature("http://jabber.org/protocol/disco#info");
		discoInfo.addFeature("http://jabber.org/protocol/muc");

		CapsInfoGenerator testling = new CapsInfoGenerator("http://code.google.com/p/exodus", crypto);
		CapsInfo result = testling.generateCapsInfo(discoInfo);

		assertEquals("http://code.google.com/p/exodus", result.getNode());
		assertEquals("sha-1", result.getHash());
		assertEquals("QgayPKawpkPSDYmwT/WM94uAlu0=", result.getVersion());
	}

	@Test
	public void testGenerate_XEP0115ComplexExample() {
		DiscoInfo discoInfo = new DiscoInfo();
		discoInfo.addIdentity(new DiscoInfo.Identity("Psi 0.11", "client", "pc", "en"));
		discoInfo.addIdentity(new DiscoInfo.Identity("Ψ 0.11", "client", "pc", "el"));
		discoInfo.addFeature("http://jabber.org/protocol/disco#items");
		discoInfo.addFeature("http://jabber.org/protocol/caps");
		discoInfo.addFeature("http://jabber.org/protocol/disco#info");
		discoInfo.addFeature("http://jabber.org/protocol/muc");

		Form extension = new Form(Form.Type.RESULT_TYPE);
		FormField field = new FormField(FormField.Type.HIDDEN_TYPE, "urn:xmpp:dataforms:softwareinfo");
		field.setName("FORM_TYPE");
		extension.addField(field);
		field = new FormField(FormField.Type.LIST_MULTI_TYPE);
		field.addValue("ipv6");
		field.addValue("ipv4");
		field.setName("ip_version");
		extension.addField(field);
		field = new FormField(FormField.Type.TEXT_SINGLE_TYPE, "Psi");
		field.setName("software");
		extension.addField(field);
		field = new FormField(FormField.Type.TEXT_SINGLE_TYPE, "0.11");
		field.setName("software_version");
		extension.addField(field);
		field = new FormField(FormField.Type.TEXT_SINGLE_TYPE, "Mac");
		field.setName("os");
		extension.addField(field);
		field = new FormField(FormField.Type.TEXT_SINGLE_TYPE, "10.5.1");
		field.setName("os_version");
		extension.addField(field);
		discoInfo.addExtension(extension);

		CapsInfoGenerator testling = new CapsInfoGenerator("http://psi-im.org", crypto);
		CapsInfo result = testling.generateCapsInfo(discoInfo);

		assertEquals("q07IKJEyjvHSyhy//CH0CxmKi8w=", result.getVersion());
	}
}