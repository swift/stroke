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

package com.isode.stroke.parser.payloadparsers;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotNull;
import org.junit.BeforeClass;
import org.junit.Test;
import com.isode.stroke.elements.VCard;
import com.isode.stroke.elements.Payload;
import com.isode.stroke.parser.payloadparsers.VCardParser;
import com.isode.stroke.parser.payloadparsers.PayloadsParserTester;
import com.isode.stroke.eventloop.DummyEventLoop;
import com.isode.stroke.jid.JID;
import com.isode.stroke.base.DateTime;
import com.isode.stroke.base.ByteArray;
import com.isode.stroke.stringcodecs.Hexify;

public class VCardParserTest {

	@Test
	public void testParse() {
		DummyEventLoop eventloop = new DummyEventLoop();
		PayloadsParserTester parser = new PayloadsParserTester(eventloop);

		assertNotNull(parser.parse(
			"<vCard xmlns=\"vcard-temp\">" +
				"<VERSION>2.0</VERSION>" +
				"<FN>Alice In Wonderland</FN>" +
				"<N>" +
				"<FAMILY>Wonderland</FAMILY>" +
				"<GIVEN>Alice</GIVEN>" +
				"<MIDDLE>In</MIDDLE>" +
				"<PREFIX>Mrs</PREFIX>" +
				"<SUFFIX>PhD</SUFFIX>" +
				"</N>" +
				"<EMAIL>" +
				"<USERID>alice@wonderland.lit</USERID>" +
				"<HOME/>" +
				"<INTERNET/>" +
				"<PREF/>" +
				"</EMAIL>" +
				"<EMAIL>" +
				"<USERID>alice@teaparty.lit</USERID>" +
				"<WORK/>" +
				"<X400/>" +
				"</EMAIL>" +
				"<TEL>" +
				"<NUMBER>555-6273</NUMBER>" +
				"<HOME/>" +
				"<VOICE/>" +
				"</TEL>" +
				"<ADR>" +
				"<LOCALITY>Any Town</LOCALITY>" +
				"<STREET>Fake Street 123</STREET>" +
				"<PCODE>12345</PCODE>" +
				"<CTRY>USA</CTRY>" +
				"<HOME/>" +
				"</ADR>" +
				"<LABEL>" +
				"<LINE>Fake Street 123</LINE>" +
				"<LINE>12345 Any Town</LINE>" +
				"<LINE>USA</LINE>" +
				"<HOME/>" +
				"</LABEL>" +
				"<NICKNAME>DreamGirl</NICKNAME>" +
				"<BDAY>1865-05-04T00:00:00Z</BDAY>" +
				"<JID>alice@teaparty.lit</JID>" +
				"<JID>alice@wonderland.lit</JID>" +
				"<DESC>I once fell down a rabbit hole.</DESC>" +
				"<ORG>" +
				"<ORGNAME>Alice In Wonderland Inc.</ORGNAME>" +
				"</ORG>" +
				"<TITLE>Some Title</TITLE>" +
				"<ROLE>Main Character</ROLE>" +
				"<URL>http://wonderland.lit/~alice</URL>" +
				"<URL>http://teaparty.lit/~alice2</URL>" +
				"<MAILER>mutt</MAILER>" +
			"</vCard>"));

		VCard payload = (VCard)(parser.getPayload());
		assertEquals(("2.0"), payload.getVersion());
		assertEquals(("Alice In Wonderland"), payload.getFullName());
		assertEquals(("Alice"), payload.getGivenName());
		assertEquals(("In"), payload.getMiddleName());
		assertEquals(("Wonderland"), payload.getFamilyName());
		assertEquals(("Mrs"), payload.getPrefix());
		assertEquals(("PhD"), payload.getSuffix());
		assertEquals(("DreamGirl"), payload.getNickname());
		assertEquals(DateTime.stringToDate("1865-05-04T00:00:00Z"), payload.getBirthday());
		assertEquals(2, (payload.getEMailAddresses().size()));
		assertEquals(("alice@wonderland.lit"), payload.getEMailAddresses().get(0).address);
		assertTrue(payload.getEMailAddresses().get(0).isHome);
		assertTrue(payload.getEMailAddresses().get(0).isInternet);
		assertTrue(payload.getEMailAddresses().get(0).isPreferred);
		assertFalse(payload.getEMailAddresses().get(0).isWork);
		assertFalse(payload.getEMailAddresses().get(0).isX400);
		assertEquals(("alice@teaparty.lit"), payload.getEMailAddresses().get(1).address);
		assertFalse(payload.getEMailAddresses().get(1).isHome);
		assertFalse(payload.getEMailAddresses().get(1).isInternet);
		assertFalse(payload.getEMailAddresses().get(1).isPreferred);
		assertTrue(payload.getEMailAddresses().get(1).isWork);
		assertTrue(payload.getEMailAddresses().get(1).isX400);

		assertEquals(1, (payload.getTelephones().size()));
		assertEquals(("555-6273"), payload.getTelephones().get(0).number);
		assertTrue(payload.getTelephones().get(0).isHome);
		assertTrue(payload.getTelephones().get(0).isVoice);
		assertFalse(payload.getTelephones().get(0).isPreferred);

		assertEquals(1, (payload.getAddresses().size()));
		assertEquals(("Any Town"), payload.getAddresses().get(0).locality);
		assertEquals(("Fake Street 123"), payload.getAddresses().get(0).street);
		assertEquals(("12345"), payload.getAddresses().get(0).postalCode);
		assertEquals(("USA"), payload.getAddresses().get(0).country);
		assertTrue(payload.getAddresses().get(0).isHome);

		assertEquals(1, (payload.getAddressLabels().size()));
		assertEquals(("Fake Street 123"), payload.getAddressLabels().get(0).lines.get(0));
		assertEquals(("12345 Any Town"), payload.getAddressLabels().get(0).lines.get(1));
		assertEquals(("USA"), payload.getAddressLabels().get(0).lines.get(2));
		assertTrue(payload.getAddressLabels().get(0).isHome);

		assertEquals(2, (payload.getJIDs().size()));
		assertEquals(new JID("alice@teaparty.lit"), payload.getJIDs().get(0));
		assertEquals(new JID("alice@wonderland.lit"), payload.getJIDs().get(1));

		assertEquals(("I once fell down a rabbit hole."), payload.getDescription());

		assertEquals(1, (payload.getOrganizations().size()));
		assertEquals(("Alice In Wonderland Inc."), payload.getOrganizations().get(0).name);
		assertEquals(0, (payload.getOrganizations().get(0).units.size()));

		assertEquals(1, (payload.getTitles().size()));
		assertEquals(("Some Title"), payload.getTitles().get(0));
		assertEquals(1, (payload.getRoles().size()));
		assertEquals(("Main Character"), payload.getRoles().get(0));
		assertEquals(2, (payload.getURLs().size()));
		assertEquals(("http://wonderland.lit/~alice"), payload.getURLs().get(0));
		assertEquals(("http://teaparty.lit/~alice2"), payload.getURLs().get(1));

		assertEquals(("<MAILER xmlns=\"vcard-temp\">mutt</MAILER>"), payload.getUnknownContent());
	}

	@Test
	public void testParse_Photo() {
		DummyEventLoop eventloop = new DummyEventLoop();
		PayloadsParserTester parser = new PayloadsParserTester(eventloop);

		assertNotNull(parser.parse(
			"<vCard xmlns='vcard-temp'>" +
				"<PHOTO>" +
				"<TYPE>image/jpeg</TYPE>" +
				"<BINVAL>" +
					"QUJDREVGR0hJSktMTU5PUFFSU1RVVldYWVphYmNkZWZnaGlqa2xtbm9wcXJzdHV2d3h5ej" +
					"EyMzQ1Njc4OTA=" +
				"</BINVAL>" +
				"</PHOTO>" +
			"</vCard>"));

		VCard payload = (VCard)(parser.getPayload());
		assertEquals(("image/jpeg"), payload.getPhotoType());
		assertEquals(new ByteArray("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890"), payload.getPhoto());
	}

		void testParse_NewlinedPhoto() {
		DummyEventLoop eventloop = new DummyEventLoop();
		PayloadsParserTester parser = new PayloadsParserTester(eventloop);

		assertNotNull(parser.parse(
			"<vCard xmlns='vcard-temp'>" +
				"<PHOTO>" +
				"<TYPE>image/jpeg</TYPE>" +
				"<BINVAL>" +
					"dTsETKSAskgu2/BqVO+ogcu3DJy4QATGJqpsa6znWwNGiLnVElVVB6PtS+mTiHUXsrOlKvRjtvzV\n" +
					"VDknNaRF58Elmu5EC6VoCllBEEB/lFf0emYn2gkp0X1khNi75dl+rOj95Ar6XuwLh+ZoSStqwOWj\n" +
					"pIpxmZmVw7E69qr0FY0oI3zcaxXwzHw7Lx9Qf4sH7ufQvIN88ga+hwp8MiXevh3Ac8pN00kgINlq\n" +
					"9AY/bYJL418Y/6wWsJbgmrJ/N78wSMpC7VVszLBZVv8uFnupubyi8Ophd/1wIWWzPPwAbBhepWVb\n" +
					"1oPiFEBT5MNKCMTPEi0npXtedVz0HQbbPNIVwmo=" +
				"</BINVAL>" +
				"</PHOTO>" +
			"</vCard>"));

		VCard payload = (VCard)(parser.getPayload());
		assertEquals(("image/jpeg"), payload.getPhotoType());
		assertEquals("753B044CA480B2482EDBF06A54EFA881CBB70C9CB84004C626AA6C6BACE75B034688B9D512555507A3ED4BE993887517B2B3A52AF463B6FCD554392735A445E7C1259AEE440BA5680A594110407F9457F47A6627DA0929D17D6484D8BBE5D97EACE8FDE40AFA5EEC0B87E668492B6AC0E5A3A48A71999995C3B13AF6AAF4158D28237CDC6B15F0CC7C3B2F1F507F8B07EEE7D0BC837CF206BE870A7C3225DEBE1DC073CA4DD3492020D96AF4063F6D824BE35F18FFAC16B096E09AB27F37BF3048CA42ED556CCCB05956FF2E167BA9B9BCA2F0EA6177FD702165B33CFC006C185EA5655BD683E2144053E4C34A08C4CF122D27A57B5E755CF41D06DB3CD215C26A", Hexify.hexify(payload.getPhoto()));
		}


	@Test
	public void testParse_Nickname() {
		DummyEventLoop eventloop = new DummyEventLoop();
		PayloadsParserTester parser = new PayloadsParserTester(eventloop);

		assertNotNull(parser.parse(
			"<vCard xmlns='vcard-temp'>" +
				"<NICKNAME>mynick</NICKNAME>" +
			"</vCard>"));

		VCard payload = (VCard)(parser.getPayload());
		assertEquals(("mynick"), payload.getNickname());
	}
}