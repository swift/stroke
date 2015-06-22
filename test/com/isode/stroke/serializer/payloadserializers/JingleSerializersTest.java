/*
 * Copyright (c) 2011 Tobias Markmann
 * Licensed under the simplified BSD license.
 * See Documentation/Licenses/BSD-simplified.txt for more information.
 */
/*
 * Copyright (c) 2015 Isode Limited.
 * All rights reserved.
 * See the COPYING file for more information.
 */
/*
 * Copyright (c) 2015 Tarun Gupta.
 * Licensed under the simplified BSD license.
 * See Documentation/Licenses/BSD-simplified.txt for more information.
 */

package com.isode.stroke.serializer.payloadserializers;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import com.isode.stroke.serializer.payloadserializers.JingleFileTransferDescriptionSerializer;
import com.isode.stroke.serializer.payloadserializers.StreamInitiationFileInfoSerializer;
import com.isode.stroke.serializer.payloadserializers.JinglePayloadSerializer;
import com.isode.stroke.serializer.payloadserializers.FullPayloadSerializerCollection;
import com.isode.stroke.elements.JingleFileTransferDescription;
import com.isode.stroke.elements.JingleFileTransferFileInfo;
import com.isode.stroke.elements.JingleIBBTransportPayload;
import com.isode.stroke.elements.JingleS5BTransportPayload;
import com.isode.stroke.elements.JingleFileTransferHash;
import com.isode.stroke.elements.JinglePayload;
import com.isode.stroke.elements.HashElement;
import com.isode.stroke.elements.JingleContentPayload;
import com.isode.stroke.elements.StreamInitiationFileInfo;
import com.isode.stroke.base.DateTime;
import com.isode.stroke.stringcodecs.Base64;
import com.isode.stroke.jid.JID;
import com.isode.stroke.network.HostAddress;
import com.isode.stroke.network.HostAddressPort;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class JingleSerializersTest {

	private FullPayloadSerializerCollection collection = new FullPayloadSerializerCollection();

	/**
	* Default Constructor.
	*/
	public JingleSerializersTest() {

	}

	public JinglePayloadSerializer createTestling() {
		return new JinglePayloadSerializer(collection);
	}

	@Test
	public void testSerialize_StreamInitiationFileInfo() {
		String expected = "<file date=\"1969-07-21T02:56:15Z\" hash=\"552da749930852c69ae5d2141d3766b1\" name=\"test.txt\" size=\"1022\" xmlns=\"http://jabber.org/protocol/si/profile/file-transfer\"><desc>This is a test. If this were a real file...</desc><range/></file>";
			
		StreamInitiationFileInfo fileInfo = new StreamInitiationFileInfo();
		fileInfo.setDate(DateTime.stringToDate("1969-07-21T02:56:15Z"));
		fileInfo.setHash("552da749930852c69ae5d2141d3766b1");
		fileInfo.setSize(1022);
		fileInfo.setName("test.txt");
		fileInfo.setDescription("This is a test. If this were a real file...");
		fileInfo.setSupportsRangeRequests(true);
		
		StreamInitiationFileInfoSerializer serializer = new StreamInitiationFileInfoSerializer();
		assertEquals(expected, serializer.serializePayload(fileInfo));
	}


	@Test
	public void testSerialize_StreamInitiationFileInfoRange() {
		String expected = "<file hash=\"552da749930852c69ae5d2141d3766b1\" xmlns=\"http://jabber.org/protocol/si/profile/file-transfer\"><range offset=\"270336\"/></file>";
		
		StreamInitiationFileInfo fileInfo = new StreamInitiationFileInfo();
		fileInfo.setHash("552da749930852c69ae5d2141d3766b1");
		fileInfo.setSupportsRangeRequests(true);
		fileInfo.setRangeOffset(270336);
		
		StreamInitiationFileInfoSerializer serializer = new StreamInitiationFileInfoSerializer();
		assertEquals(expected, serializer.serializePayload(fileInfo));
	}
		
		
	// IBB Transport Method Examples
		
	// http://xmpp.org/extensions/xep-0261.html#example-1
	@Test
	public void testSerialize_Xep0261_Example1() {
		String expected = "<jingle action=\"session-initiate\" initiator=\"romeo@montague.lit/orchard\" sid=\"a73sjjvkla37jfea\" xmlns=\"urn:xmpp:jingle:1\"><content creator=\"initiator\" name=\"ex\"><transport block-size=\"4096\" sid=\"ch3d9s71\" xmlns=\"urn:xmpp:jingle:transports:ibb:1\"/></content></jingle>";
		
		JinglePayload payload = new JinglePayload();
		payload.setAction(JinglePayload.Action.SessionInitiate);
		payload.setSessionID("a73sjjvkla37jfea");
		payload.setInitiator(new JID("romeo@montague.lit/orchard"));
		
		JingleIBBTransportPayload transport = new JingleIBBTransportPayload();
		transport.setBlockSize(4096);
		transport.setSessionID("ch3d9s71");
		
		JingleContentPayload content = new JingleContentPayload();
		content.setCreator(JingleContentPayload.Creator.InitiatorCreator);
		content.setName("ex");
		content.addTransport(transport);
		
		payload.addPayload(content);
		
		assertEquals(expected, createTestling().serialize(payload));
	}
		
	// http://xmpp.org/extensions/xep-0261.html#example-9
	@Test
	public void testSerialize_Xep0261_Example9() {
		String expected = "<jingle action=\"transport-info\" initiator=\"romeo@montague.lit/orchard\" sid=\"a73sjjvkla37jfea\" xmlns=\"urn:xmpp:jingle:1\"><content creator=\"initiator\" name=\"ex\"><transport block-size=\"2048\" sid=\"bt8a71h6\" xmlns=\"urn:xmpp:jingle:transports:ibb:1\"/></content></jingle>";
		
		JinglePayload payload = new JinglePayload();
		payload.setAction(JinglePayload.Action.TransportInfo);
		payload.setInitiator(new JID("romeo@montague.lit/orchard"));
		payload.setSessionID("a73sjjvkla37jfea");
		
		JingleContentPayload content = new JingleContentPayload();
		content.setCreator(JingleContentPayload.Creator.InitiatorCreator);
		content.setName("ex");
		
		JingleIBBTransportPayload transport = new JingleIBBTransportPayload();
		transport.setBlockSize(2048);
		transport.setSessionID("bt8a71h6");
		
		content.addTransport(transport);
		payload.addPayload(content);
		
		assertEquals(expected, createTestling().serialize(payload));
	}
		
	// http://xmpp.org/extensions/xep-0261.html#example-13
	@Test
	public void testSerialize_Xep0261_Example13() {
		String expected = "<jingle action=\"session-terminate\" initiator=\"romeo@montague.lit/orchard\" sid=\"a73sjjvkla37jfea\" xmlns=\"urn:xmpp:jingle:1\"><reason><success/></reason></jingle>";
		
		JinglePayload payload = new JinglePayload();
		payload.setAction(JinglePayload.Action.SessionTerminate);
		payload.setInitiator(new JID("romeo@montague.lit/orchard"));
		payload.setSessionID("a73sjjvkla37jfea");
		payload.setReason(new JinglePayload.Reason(JinglePayload.Reason.Type.Success));
		
		assertEquals(expected, createTestling().serialize(payload));
	}
		
	// http://xmpp.org/extensions/xep-0234.html#example-1
	@Test
	public void testSerialize_Xep0234_Example1() {
		String expected = "<description xmlns=\"urn:xmpp:jingle:apps:file-transfer:4\"><file><date>1969-07-21T02:56:15Z</date><desc>This is a test. If this were a real file...</desc><name>test.txt</name><range/><size>1022</size><hash algo=\"sha-1\" xmlns=\"urn:xmpp:hashes:1\">VS2nSZMIUsaa5dIUHTdmsQ==</hash></file></description>";

		JingleFileTransferDescription desc = new JingleFileTransferDescription();
		JingleFileTransferFileInfo fileInfo = new JingleFileTransferFileInfo();
		
		fileInfo.setDate(DateTime.stringToDate("1969-07-21T02:56:15Z"));
		fileInfo.addHash(new HashElement("sha-1", Base64.decode("VS2nSZMIUsaa5dIUHTdmsQ==")));
		fileInfo.setSize(1022);
		fileInfo.setName("test.txt");
		fileInfo.setDescription("This is a test. If this were a real file...");
		fileInfo.setSupportsRangeRequests(true);
		
		desc.setFileInfo(fileInfo);
		
		assertEquals(expected, new JingleFileTransferDescriptionSerializer().serialize(desc));
	}
		
	// http://xmpp.org/extensions/xep-0234.html#example-3
	@Test	
	public void testSerialize_Xep0234_Example3() {
		String expected = "<jingle action=\"session-accept\" initiator=\"romeo@montague.lit/orchard\" sid=\"851ba2\" xmlns=\"urn:xmpp:jingle:1\"><content creator=\"initiator\" name=\"a-file-offer\"><description xmlns=\"urn:xmpp:jingle:apps:file-transfer:4\"><file><date>1969-07-21T02:56:15Z</date><desc>This is a test. If this were a real file...</desc><name>test.txt</name><range/><size>1022</size><hash algo=\"sha-1\" xmlns=\"urn:xmpp:hashes:1\">VS2nSZMIUsaa5dIUHTdmsQ==</hash></file></description></content></jingle>";
		
		JinglePayload payload = new JinglePayload();
		payload.setAction(JinglePayload.Action.SessionAccept);
		payload.setInitiator(new JID("romeo@montague.lit/orchard"));
		payload.setSessionID("851ba2");
		
		JingleContentPayload content = new JingleContentPayload();
		content.setCreator(JingleContentPayload.Creator.InitiatorCreator);
		content.setName("a-file-offer");
		
		JingleFileTransferDescription description = new JingleFileTransferDescription();
		JingleFileTransferFileInfo fileInfo = new JingleFileTransferFileInfo();
		fileInfo.setName("test.txt");
		fileInfo.setSize(1022);
		fileInfo.addHash(new HashElement("sha-1", Base64.decode("VS2nSZMIUsaa5dIUHTdmsQ==")));
		fileInfo.setDate(DateTime.stringToDate("1969-07-21T02:56:15Z"));
		fileInfo.setDescription("This is a test. If this were a real file...");
		fileInfo.setSupportsRangeRequests(true);
		
		description.setFileInfo(fileInfo);
		content.addDescription(description);
		payload.addPayload(content);
		
		assertEquals(expected, createTestling().serialize(payload));
	}
		
	// http://xmpp.org/extensions/xep-0234.html#example-5
	@Test
	public void testSerialize_Xep0234_Example5() {
		String expected = "<jingle action=\"transport-info\" initiator=\"romeo@montague.lit/orchard\" sid=\"a73sjjvkla37jfea\" xmlns=\"urn:xmpp:jingle:1\"><content creator=\"initiator\" name=\"ex\"/></jingle>";

		JinglePayload payload = new JinglePayload();
		payload.setAction(JinglePayload.Action.TransportInfo);
		payload.setInitiator(new JID("romeo@montague.lit/orchard"));
		payload.setSessionID("a73sjjvkla37jfea");
		
		JingleContentPayload content = new JingleContentPayload();
		content.setCreator(JingleContentPayload.Creator.InitiatorCreator);
		content.setName("ex");
		payload.addPayload(content);
		
		assertEquals(expected, createTestling().serialize(payload));
	}
		
	// http://xmpp.org/extensions/xep-0234.html#example-8
	@Test
	public void testSerialize_Xep0234_Example8() {
		String expected = "<jingle action=\"session-info\" initiator=\"romeo@montague.lit/orchard\" sid=\"a73sjjvkla37jfea\" xmlns=\"urn:xmpp:jingle:1\"><checksum xmlns=\"urn:xmpp:jingle:apps:file-transfer:4\"><file><hash algo=\"sha-1\" xmlns=\"urn:xmpp:hashes:1\">VS2nSZMIUsaa5dIUHTdmsQ==</hash></file></checksum></jingle>";
		
		JinglePayload payload = new JinglePayload();
		payload.setAction(JinglePayload.Action.SessionInfo);
		payload.setInitiator(new JID("romeo@montague.lit/orchard"));
		payload.setSessionID("a73sjjvkla37jfea");
		
		JingleFileTransferHash hash = new JingleFileTransferHash();
		hash.getFileInfo().addHash(new HashElement("sha-1", Base64.decode("VS2nSZMIUsaa5dIUHTdmsQ==")));
		
		payload.addPayload(hash);
		
		assertEquals(expected, createTestling().serialize(payload));
	}

	// http://xmpp.org/extensions/xep-0260.html#example-1
	@Test
	public void testSerialize_Xep0260_Example1() {
		String expected = "<jingle action=\"session-initiate\" initiator=\"romeo@montague.lit/orchard\" sid=\"a73sjjvkla37jfea\" xmlns=\"urn:xmpp:jingle:1\"><content creator=\"initiator\" name=\"ex\"><transport dstaddr=\"1a12fb7bc625e55f3ed5b29a53dbe0e4aa7d80ba\" mode=\"tcp\" sid=\"vj3hs98y\" xmlns=\"urn:xmpp:jingle:transports:s5b:1\"><candidate cid=\"hft54dqy\" host=\"192.168.4.1\" jid=\"romeo@montague.lit/orchard\" port=\"5086\" priority=\"8257636\" type=\"direct\"/><candidate cid=\"hutr46fe\" host=\"24.24.24.1\" jid=\"romeo@montague.lit/orchard\" port=\"5087\" priority=\"8258636\" type=\"direct\"/></transport></content></jingle>";

		JinglePayload payload = new JinglePayload();
		payload.setAction(JinglePayload.Action.SessionInitiate);
		payload.setInitiator(new JID("romeo@montague.lit/orchard"));
		payload.setSessionID("a73sjjvkla37jfea");

		JingleContentPayload content = new JingleContentPayload();
		content.setCreator(JingleContentPayload.Creator.InitiatorCreator);
		content.setName("ex");

		JingleS5BTransportPayload transport = new JingleS5BTransportPayload();
		transport.setMode(JingleS5BTransportPayload.Mode.TCPMode);
		transport.setDstAddr("1a12fb7bc625e55f3ed5b29a53dbe0e4aa7d80ba");
		transport.setSessionID("vj3hs98y");

		JingleS5BTransportPayload.Candidate candidate1 = new JingleS5BTransportPayload.Candidate();
		candidate1.cid = "hft54dqy";
		try {
			candidate1.hostPort = new HostAddressPort(new HostAddress(InetAddress.getByName("192.168.4.1")), 5086);
		} catch (UnknownHostException e) {

		}
		candidate1.jid = new JID("romeo@montague.lit/orchard");
		candidate1.priority = 8257636;
		candidate1.type = JingleS5BTransportPayload.Candidate.Type.DirectType;
		transport.addCandidate(candidate1);

		JingleS5BTransportPayload.Candidate candidate2 = new JingleS5BTransportPayload.Candidate();
		candidate2.cid = "hutr46fe";
		try {
			candidate2.hostPort = new HostAddressPort(new HostAddress(InetAddress.getByName("24.24.24.1")), 5087);
		} catch (UnknownHostException e) {

		}
		candidate2.jid = new JID("romeo@montague.lit/orchard");
		candidate2.priority = 8258636;
		candidate2.type = JingleS5BTransportPayload.Candidate.Type.DirectType;
		transport.addCandidate(candidate2);

		content.addTransport(transport);

		payload.addPayload(content);

		assertEquals(expected, createTestling().serialize(payload));
	}
}