/*
 * Copyright (c) 2011 Tobias Markmann
 * Licensed under the simplified BSD license.
 * See Documentation/Licenses/BSD-simplified.txt for more information.
 */
/*
 * Copyright (c) 2015-2016 Isode Limited.
 * All rights reserved.
 * See the COPYING file for more information.
 */
/*
 * Copyright (c) 2015 Tarun Gupta.
 * Licensed under the simplified BSD license.
 * See Documentation/Licenses/BSD-simplified.txt for more information.
 */

package com.isode.stroke.parser.payloadparsers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Vector;
import java.util.logging.Logger;

import org.junit.Test;

import com.isode.stroke.base.DateTime;
import com.isode.stroke.elements.JingleContentPayload;
import com.isode.stroke.elements.JingleFileTransferDescription;
import com.isode.stroke.elements.JingleFileTransferFileInfo;
import com.isode.stroke.elements.JingleFileTransferHash;
import com.isode.stroke.elements.JingleIBBTransportPayload;
import com.isode.stroke.elements.JinglePayload;
import com.isode.stroke.elements.JingleS5BTransportPayload;
import com.isode.stroke.eventloop.DummyEventLoop;
import com.isode.stroke.jid.JID;
import com.isode.stroke.network.HostAddress;
import com.isode.stroke.network.HostAddressPort;
import com.isode.stroke.stringcodecs.Base64;

public class JingleParserTest {

	private Logger logger_ = Logger.getLogger(this.getClass().getName());

	public JingleParserTest() {

	}

	@Test
	public void testParse_Xep0166_Example3() {
		DummyEventLoop eventLoop = new DummyEventLoop();
		PayloadsParserTester parser = new PayloadsParserTester(eventLoop);
		assertNotNull(parser.parse(
			"<jingle xmlns='urn:xmpp:jingle:1'\n" +
			"	 action='session-terminate'\n" +
			"	 sid='a73sjjvkla37jfea'>\n" +
			"	<reason>\n" +
			"		<success/>\n" +
			"	</reason>\n" +
			"</jingle>\n"
		));
		
		JinglePayload jingle = (JinglePayload)parser.getPayload();
		assertNotNull(jingle);
		assertEquals(JinglePayload.Action.SessionTerminate, jingle.getAction());
		assertEquals("a73sjjvkla37jfea", jingle.getSessionID());
		assertEquals(JinglePayload.Reason.Type.Success, jingle.getReason().type);
	}

	//http://xmpp.org/extensions/xep-0166.html#example-8
	@Test
	public void testParse_Xep0166_Example8() {
		DummyEventLoop eventLoop = new DummyEventLoop();
		PayloadsParserTester parser = new PayloadsParserTester(eventLoop);
		assertNotNull(parser.parse(
			"<jingle xmlns='urn:xmpp:jingle:1'\n" +
			"        action='session-terminate'\n" +
			"        sid='a73sjjvkla37jfea'>\n" +
			"	<reason>\n" +
			"		<success/>\n" +
			"		<text>Sorry, gotta go!</text>\n" +
			"	</reason>\n" +
			"</jingle>\n"));

		JinglePayload jingle = (JinglePayload)parser.getPayload();
		assertNotNull(jingle);
		assertEquals(JinglePayload.Action.SessionTerminate, jingle.getAction());
		assertEquals("a73sjjvkla37jfea", jingle.getSessionID());
		assertEquals(JinglePayload.Reason.Type.Success, jingle.getReason().type);
		assertEquals("Sorry, gotta go!", jingle.getReason().text);
	}

	// IBB Transport Method Examples
	
	// http://xmpp.org/extensions/xep-0261.html#example-1
	@Test
	public void testParse_Xep0261_Example1() {
		DummyEventLoop eventLoop = new DummyEventLoop();
		PayloadsParserTester parser = new PayloadsParserTester(eventLoop);
		assertNotNull(parser.parse(
			"<jingle xmlns='urn:xmpp:jingle:1'\n" +
			"          action='session-initiate'\n" +
			"          initiator='romeo@montague.lit/orchard'\n" +
			"          sid='a73sjjvkla37jfea'>\n" +
			"    <content creator='initiator' name='ex'>\n" +
			"      <description xmlns='urn:xmpp:example'/>\n" +
			"      <transport xmlns='urn:xmpp:jingle:transports:ibb:1'\n" +
			"                 block-size='4096'\n" +
			"                 sid='ch3d9s71'/>\n" +
			"    </content>\n" +
			"</jingle>\n"));

		JinglePayload jingle = (JinglePayload)parser.getPayload();
		assertNotNull(jingle);
		assertEquals(JinglePayload.Action.SessionInitiate, jingle.getAction());
		assertEquals(new JID("romeo@montague.lit/orchard"), jingle.getInitiator());
		assertEquals("a73sjjvkla37jfea", jingle.getSessionID());

		Vector<JingleContentPayload> payloads = jingle.getContents();
		assertEquals(1, payloads.size());
		JingleContentPayload payload = payloads.get(0);
		assertEquals(JingleContentPayload.Creator.InitiatorCreator, payload.getCreator());
		assertEquals("ex", payload.getName());
		assertEquals(1, payload.getTransports().size());

		JingleIBBTransportPayload transportPayload = payload.getTransport(new JingleIBBTransportPayload());
		assertNotNull(transportPayload);
		assertEquals(Integer.valueOf(4096), transportPayload.getBlockSize());
		assertEquals("ch3d9s71", transportPayload.getSessionID());
	}

	// http://xmpp.org/extensions/xep-0261.html#example-1
	@Test
	public void testParse_Xep0261_Example3() {
		DummyEventLoop eventLoop = new DummyEventLoop();
		PayloadsParserTester parser = new PayloadsParserTester(eventLoop);
		assertNotNull(parser.parse(
			"<jingle xmlns='urn:xmpp:jingle:1'\n" +
			"          action='session-accept'\n" +
			"          initiator='romeo@montague.lit/orchard'\n" +
			"          responder='juliet@capulet.lit/balcony'\n" +
			"          sid='a73sjjvkla37jfea'>\n" +
			"    <content creator='initiator' name='ex'>\n" +
			"      <description xmlns='urn:xmpp:example'/>\n" +
			"      <transport xmlns='urn:xmpp:jingle:transports:ibb:1'\n" +
			"                 block-size='2048'\n" +
			"                 sid='ch3d9s71'/>\n" +
			"    </content>\n" +
			"  </jingle>\n"
		));

		JinglePayload jingle = (JinglePayload)parser.getPayload();
		assertNotNull(jingle);
		assertEquals(JinglePayload.Action.SessionAccept, jingle.getAction());
		assertEquals(new JID("romeo@montague.lit/orchard"), jingle.getInitiator());
		assertEquals(new JID("juliet@capulet.lit/balcony"), jingle.getResponder());
		assertEquals("a73sjjvkla37jfea", jingle.getSessionID());
	
		Vector<JingleContentPayload> payloads = jingle.getContents();
		assertEquals(1, payloads.size());
		JingleContentPayload payload = payloads.get(0);
		assertEquals(JingleContentPayload.Creator.InitiatorCreator, payload.getCreator());
		assertEquals("ex", payload.getName());
		assertEquals(1, payload.getTransports().size());

		JingleIBBTransportPayload transportPayload = payload.getTransport(new JingleIBBTransportPayload());
		assertNotNull(transportPayload);
		assertEquals(Integer.valueOf(2048), transportPayload.getBlockSize());
		assertEquals("ch3d9s71", transportPayload.getSessionID());
	}

	// http://xmpp.org/extensions/xep-0261.html#example-9
	@Test
	public void testParse_Xep0261_Example9() {
		DummyEventLoop eventLoop = new DummyEventLoop();
		PayloadsParserTester parser = new PayloadsParserTester(eventLoop);
		assertNotNull(parser.parse(
			"<jingle xmlns='urn:xmpp:jingle:1'\n" +
			"        action='transport-info'\n" +
			"        initiator='romeo@montague.lit/orchard'\n" +
			"        sid='a73sjjvkla37jfea'>\n" +
			"  <content creator='initiator' name='ex'>\n" +
			"    <transport xmlns='urn:xmpp:jingle:transports:ibb:1'\n" +
			"               block-size='2048'\n" +
			"               sid='bt8a71h6'/>\n" +
			"  </content>\n" +
			"</jingle>\n"
		));

		JinglePayload jingle = (JinglePayload)parser.getPayload();
		assertNotNull(jingle);
		assertEquals(JinglePayload.Action.TransportInfo, jingle.getAction());
		assertEquals(new JID("romeo@montague.lit/orchard"), jingle.getInitiator());
		assertEquals("a73sjjvkla37jfea", jingle.getSessionID());
			
		Vector<JingleContentPayload> payloads = jingle.getContents();
		assertEquals(1, payloads.size());
		JingleContentPayload payload = payloads.get(0);
		assertEquals(JingleContentPayload.Creator.InitiatorCreator, payload.getCreator());
		assertEquals("ex", payload.getName());
			
		JingleIBBTransportPayload transportPayload = payload.getTransport(new JingleIBBTransportPayload());
		assertNotNull(transportPayload);
		assertEquals(Integer.valueOf(2048), transportPayload.getBlockSize());
		assertEquals("bt8a71h6", transportPayload.getSessionID());	
	}

	// http://xmpp.org/extensions/xep-0261.html#example-13
	@Test
	public void testParse_Xep0261_Example13() {
		DummyEventLoop eventLoop = new DummyEventLoop();
		PayloadsParserTester parser = new PayloadsParserTester(eventLoop);
		assertNotNull(parser.parse(
			"<jingle xmlns='urn:xmpp:jingle:1'\n" +
			"          action='session-terminate'\n" +
			"          initiator='romeo@montague.lit/orchard'\n" +
			"          sid='a73sjjvkla37jfea'>\n" +
			"    <reason><success/></reason>\n" +
			"  </jingle>\n"
		));

		JinglePayload jingle = (JinglePayload)parser.getPayload();
		assertNotNull(jingle);
		assertEquals(JinglePayload.Action.SessionTerminate, jingle.getAction());
		assertEquals(new JID("romeo@montague.lit/orchard"), jingle.getInitiator());
		assertEquals("a73sjjvkla37jfea", jingle.getSessionID());
		assertEquals(JinglePayload.Reason.Type.Success, jingle.getReason().type);
	}

	// Jingle File Transfer Examples
	
	// http://xmpp.org/extensions/xep-0234.html#example-1
	@Test
	public void testParse_Xep0234_Example1() {
		DummyEventLoop eventLoop = new DummyEventLoop();
		PayloadsParserTester parser = new PayloadsParserTester(eventLoop);
		assertNotNull(parser.parse(
			"<jingle xmlns='urn:xmpp:jingle:1'\n" +
				" action='session-initiate'\n" + 
				" initiator='romeo@montague.lit/orchard'\n" +
				" sid='851ba2'>\n" +
				"<content creator='initiator' name='a-file-offer'>\n" +
					"<description xmlns='urn:xmpp:jingle:apps:file-transfer:4'>\n" +
						"<file>\n" +
							"<date>1969-07-21T02:56:15Z</date>\n" +
							"<desc>This is a test. If this were a real file...</desc>\n" +
							"<media-type>text/plain</media-type>\n" +
							"<name>test.txt</name>\n" +
							"<range/>\n" +
							"<size>1022</size>\n" +
							"<hash xmlns='urn:xmpp:hashes:1' algo='sha-1'>VS2nSZMIUsaa5dIUHTdmsQ==</hash>\n" +
						"</file>\n" +
					"</description>\n" +
					"<transport xmlns='urn:xmpp:jingle:transports:s5b:1'\n" +
						" mode='tcp'\n" +
						" sid='vj3hs98y'>\n" +
						"<candidate cid='hft54dqy'\n" +
							" host='192.168.4.1'\n" +
							" jid='romeo@montague.lit/orchard'\n" +
							" port='5086'\n" +
							" priority='8257636'\n" +
							" type='direct'/>\n" +
						"<candidate cid='hutr46fe'\n" +
							" host='24.24.24.1'\n" +
							" jid='romeo@montague.lit/orchard'\n" +
							" port='5087'\n" +
							" priority='8258636'\n" +
							" type='direct'/>\n" +
					"</transport>\n" +
				"</content>\n" +
			"</jingle>\n"));

			JinglePayload jingle = (JinglePayload)parser.getPayload();
			assertNotNull(jingle);
			assertEquals(JinglePayload.Action.SessionInitiate, jingle.getAction());
			assertEquals(new JID("romeo@montague.lit/orchard"), jingle.getInitiator());
			assertEquals("851ba2", jingle.getSessionID());
			
			Vector<JingleContentPayload> contents = jingle.getContents();
			assertEquals(1, contents.size());
			
			JingleFileTransferDescription description = contents.get(0).getDescription(new JingleFileTransferDescription());
			
			assertNotNull(description);
			JingleFileTransferFileInfo fileInfo = description.getFileInfo();
			assertEquals("test.txt", fileInfo.getName());
			assertEquals("sha-1", fileInfo.getHashes().entrySet().iterator().next().getKey());
			assertEquals("VS2nSZMIUsaa5dIUHTdmsQ==", Base64.encode(fileInfo.getHashes().entrySet().iterator().next().getValue()));
			assertEquals(1022L, fileInfo.getSize());
			assertEquals("This is a test. If this were a real file...", fileInfo.getDescription());
			assertEquals(true, fileInfo.getSupportsRangeRequests());
			assertEquals(DateTime.stringToDate("1969-07-21T02:56:15Z"), fileInfo.getDate());
		}

	// http://xmpp.org/extensions/xep-0234.html#example-3
	@Test
	public void testParse_Xep0234_Example3() {
		DummyEventLoop eventLoop = new DummyEventLoop();
		PayloadsParserTester parser = new PayloadsParserTester(eventLoop);
		assertNotNull(parser.parse(
			"<jingle xmlns='urn:xmpp:jingle:1'\n" +
				" action='session-accept'\n" +
				" initiator='romeo@montague.lit/orchard'\n" +
				" sid='851ba2'>\n" +
				"<content creator='initiator' name='a-file-offer'>\n" +
					"<description xmlns='urn:xmpp:jingle:apps:file-transfer:4'>\n" +
						"<file>\n" +
							"<date>1969-07-21T02:56:15Z</date>\n" +
							"<desc>This is a test. If this were a real file...</desc>\n" +
							"<media-type>text/plain</media-type>\n" +
							"<name>test.txt</name>\n" +
							"<range/>\n" +
							"<size>1022</size>\n" +
							"<hash xmlns='urn:xmpp:hashes:1' algo='sha-1'>VS2nSZMIUsaa5dIUHTdmsQ==</hash>\n" +
						"</file>\n" +
					"</description>\n" +
			"    <transport xmlns='urn:xmpp:jingle:transports:s5b:1'\n" +
			"               mode='tcp'\n" +
			"               sid='vj3hs98y'>\n" +
			"      <candidate cid='ht567dq'\n" +
			"                 host='192.169.1.10'\n" +
			"                 jid='juliet@capulet.lit/balcony'\n" +
			"                 port='6539'\n" +
			"                 priority='8257636'\n" +
			"                 type='direct'/>\n" +
			"      <candidate cid='hr65dqyd'\n" +
			"                 host='134.102.201.180'\n" +
			"                 jid='juliet@capulet.lit/balcony'\n" +
			"                 port='16453'\n" +
			"                 priority='7929856'\n" +
			"                 type='assisted'/>\n" +
			"      <candidate cid='grt654q2'\n" +
			"                 host='2001:638:708:30c9:219:d1ff:fea4:a17d'\n" +
			"                 jid='juliet@capulet.lit/balcony'\n" +
			"                 port='6539'\n" +
			"                 priority='8257606'\n" +
			"                 type='direct'/>\n" +
			"    </transport>\n" +
			"  </content>\n" +
			"</jingle>\n"));

		JinglePayload jingle = (JinglePayload)parser.getPayload();
		assertNotNull(jingle);
		assertEquals(JinglePayload.Action.SessionAccept, jingle.getAction());
		assertEquals(new JID("romeo@montague.lit/orchard"), jingle.getInitiator());
		assertEquals("851ba2", jingle.getSessionID());
			
		Vector<JingleContentPayload> contents = jingle.getContents();
		assertEquals(1, contents.size());
		
		JingleFileTransferDescription description = contents.get(0).getDescription(new JingleFileTransferDescription());
		
		assertNotNull(description);
			
		JingleFileTransferFileInfo fileInfo = description.getFileInfo();
		assertEquals("test.txt", fileInfo.getName());
		assertEquals("sha-1", fileInfo.getHashes().entrySet().iterator().next().getKey());
		assertEquals("VS2nSZMIUsaa5dIUHTdmsQ==", Base64.encode(fileInfo.getHashes().entrySet().iterator().next().getValue()));
		assertEquals(1022L, fileInfo.getSize());
		assertEquals("This is a test. If this were a real file...", fileInfo.getDescription());
		assertEquals(true, fileInfo.getSupportsRangeRequests());
		assertEquals(DateTime.stringToDate("1969-07-21T02:56:15Z"), fileInfo.getDate());
	}

	// http://xmpp.org/extensions/xep-0234.html#example-5
	@Test
	public void testParse_Xep0234_Example5() {
		DummyEventLoop eventLoop = new DummyEventLoop();
		PayloadsParserTester parser = new PayloadsParserTester(eventLoop);
		assertNotNull(parser.parse(
			"<jingle xmlns='urn:xmpp:jingle:1'\n" +
			"        action='transport-info'\n" +
			"        initiator='romeo@montague.lit/orchard'\n" +
			"        sid='a73sjjvkla37jfea'>\n" +
			"  <content creator='initiator' name='ex'>\n" +
			"    <transport xmlns='urn:xmpp:jingle:transports:s5b:1'\n" +
			"               sid='vj3hs98y'>\n" +
			"      <candidate-used cid='hr65dqyd'/>\n" +
			"    </transport>\n" +
			"  </content>\n" +
			"</jingle>\n"));

		JinglePayload jingle = (JinglePayload)parser.getPayload();
		assertNotNull(jingle);
		assertEquals(JinglePayload.Action.TransportInfo, jingle.getAction());
		assertEquals(new JID("romeo@montague.lit/orchard"), jingle.getInitiator());
		assertEquals("a73sjjvkla37jfea", jingle.getSessionID());
			
		Vector<JingleContentPayload> contents = jingle.getContents();
		assertEquals(1, contents.size());
		
		JingleS5BTransportPayload transport = contents.get(0).getTransport(new JingleS5BTransportPayload());
		assertNotNull(transport);

		assertEquals("vj3hs98y", transport.getSessionID());
		assertEquals("hr65dqyd", transport.getCandidateUsed());
	}
		
	// http://xmpp.org/extensions/xep-0234.html#example-8
	@Test
	public void testParse_Xep0234_Example8() {
		DummyEventLoop eventLoop = new DummyEventLoop();
		PayloadsParserTester parser = new PayloadsParserTester(eventLoop);
		assertNotNull(parser.parse(
			"<jingle xmlns='urn:xmpp:jingle:1'\n" +
			"        action='session-info'\n" +
			"        initiator='romeo@montague.lit/orchard'\n" +
			"        sid='a73sjjvkla37jfea'>\n" +
			"	<checksum xmlns='urn:xmpp:jingle:apps:file-transfer:4'>\n" +
			"	  <file>\n" +
			"	      <hash xmlns='urn:xmpp:hashes:0' algo='sha-1'>VS2nSZMIUsaa5dIUHTdmsQ==</hash>\n" +
			"	  </file>\n" +
			"	</checksum>\n" +
			"</jingle>\n"
		));

		JinglePayload jingle = (JinglePayload)parser.getPayload();
		assertNotNull(jingle);
		assertEquals(JinglePayload.Action.SessionInfo, jingle.getAction());
		assertEquals(new JID("romeo@montague.lit/orchard"), jingle.getInitiator());
		assertEquals("a73sjjvkla37jfea", jingle.getSessionID());
			
		JingleFileTransferHash hash = jingle.getPayload(new JingleFileTransferHash());
		assertNotNull(hash);
		assertEquals("VS2nSZMIUsaa5dIUHTdmsQ==", Base64.encode(hash.getFileInfo().getHash("sha-1")));
	}
		
	// http://xmpp.org/extensions/xep-0234.html#example-10
	@Test
	public void testParse_Xep0234_Example10() {
		DummyEventLoop eventLoop = new DummyEventLoop();
		PayloadsParserTester parser = new PayloadsParserTester(eventLoop);
		assertNotNull(parser.parse(
			"<jingle xmlns='urn:xmpp:jingle:1'\n" +
			"        action='session-initiate'\n" +
			"        initiator='romeo@montague.lit/orchard'\n" +
			"        sid='uj3b2'>\n" +
			"  <content creator='initiator' name='a-file-request'>\n" +
			"    <description xmlns='urn:xmpp:jingle:apps:file-transfer:4'>\n" +
			"      <file>\n" +
			"        <hash xmlns='urn:xmpp:hashes:1' algo='sha-1'>VS2nSZMIUsaa5dIUHTdmsQ==</hash>\n" +
			"        <range offset='270336'/>\n" +
			"      </file>\n" +
			"    </description>\n" +
			"    <transport xmlns='urn:xmpp:jingle:transports:s5b:1'\n" +
			"               mode='tcp'\n" +
			"               sid='xig361fj'>\n" +
			"      <candidate cid='ht567dq'\n" +
			"                 host='192.169.1.10'\n" +
			"                 jid='juliet@capulet.lit/balcony'\n" +
			"                 port='6539'\n" +
			"                 priority='8257636'\n" +
			"                 type='direct'/>\n" +
			"      <candidate cid='hr65dqyd'\n" +
			"                 host='134.102.201.180'\n" +
			"                 jid='juliet@capulet.lit/balcony'\n" +
			"                 port='16453'\n" +
			"                 priority='7929856'\n" +
			"                 type='assisted'/>\n" +
			"      <candidate cid='grt654q2'\n" +
			"                 host='2001:638:708:30c9:219:d1ff:fea4:a17d'\n" +
			"                 jid='juliet@capulet.lit/balcony'\n" +
			"                 port='6539'\n" +
			"                 priority='8257606'\n" +
			"                 type='direct'/>\n" +
			"    </transport>\n" +
			"  </content>\n" +
			"</jingle>\n"
		));
		
		JinglePayload jingle = (JinglePayload)parser.getPayload();
		assertNotNull(jingle);
		assertEquals(JinglePayload.Action.SessionInitiate, jingle.getAction());
		assertEquals(new JID("romeo@montague.lit/orchard"), jingle.getInitiator());
		assertEquals("uj3b2", jingle.getSessionID());
			
		JingleContentPayload content = jingle.getPayload(new JingleContentPayload());
		assertNotNull(content);
			
		JingleFileTransferFileInfo file = content.getDescription(new JingleFileTransferDescription()).getFileInfo();
		assertEquals("sha-1", file.getHashes().entrySet().iterator().next().getKey());
		assertEquals("VS2nSZMIUsaa5dIUHTdmsQ==", Base64.encode(file.getHashes().entrySet().iterator().next().getValue()));
		assertEquals(270336L, file.getRangeOffset());
		assertEquals(true, file.getSupportsRangeRequests());
	}

	// http://xmpp.org/extensions/xep-0260.html#example-1
	@Test
	public void testParse_Xep0260_Example1() {
		DummyEventLoop eventLoop = new DummyEventLoop();
		PayloadsParserTester parser = new PayloadsParserTester(eventLoop);
		assertNotNull(parser.parse(
			"<jingle xmlns='urn:xmpp:jingle:1'\n" +
			"        action='session-initiate'\n" +
			"        initiator='romeo@montague.lit/orchard'\n" +
			"        sid='a73sjjvkla37jfea'>\n" +
			"  <content creator='initiator' name='ex'>\n" +
			"    <description xmlns='urn:xmpp:example'/>\n" +
			"    <transport xmlns='urn:xmpp:jingle:transports:s5b:1'\n" +
			"               mode='tcp'\n" +
			"               sid='vj3hs98y'>\n" +
			"      <candidate cid='hft54dqy'\n" +
			"                 host='192.168.4.1'\n" +
			"                 jid='romeo@montague.lit/orchard'\n" +
			"                 port='5086'\n" +
			"                 priority='8257636'\n" +
			"                 type='direct'/>\n" +
			"      <candidate cid='hutr46fe'\n" +
			"                 host='24.24.24.1'\n" +
			"                 jid='romeo@montague.lit/orchard'\n" +
			"                 port='5087'\n" +
			"                 priority='8258636'\n" +
			"                 type='direct'/>\n" +
			"    </transport>\n" +
			"  </content>\n" +
			"</jingle>\n"
		));

		JinglePayload jingle = (JinglePayload)parser.getPayload();
		assertNotNull(jingle);
		assertEquals(JinglePayload.Action.SessionInitiate, jingle.getAction());
		assertEquals(new JID("romeo@montague.lit/orchard"), jingle.getInitiator());
		assertEquals("a73sjjvkla37jfea", jingle.getSessionID());
		
		JingleContentPayload content = jingle.getPayload(new JingleContentPayload());
		assertNotNull(content);
		
		JingleS5BTransportPayload s5bPayload = content.getTransport(new JingleS5BTransportPayload());
		assertNotNull(s5bPayload);
		
		assertEquals("vj3hs98y", s5bPayload.getSessionID());
		assertEquals(JingleS5BTransportPayload.Mode.TCPMode, s5bPayload.getMode());
		assertEquals(false, s5bPayload.hasCandidateError());
		assertEquals(false, s5bPayload.hasProxyError());
		assertEquals("", s5bPayload.getActivated());
		assertEquals("", s5bPayload.getCandidateUsed());
		assertEquals(2, s5bPayload.getCandidates().size());
			
		try {
			JingleS5BTransportPayload.Candidate candidate;
			candidate = s5bPayload.getCandidates().get(0);
			assertEquals("hft54dqy", candidate.cid);
			assertEquals(new JID("romeo@montague.lit/orchard"), candidate.jid);
			assertEquals(new HostAddressPort(new HostAddress(InetAddress.getByName("192.168.4.1")), 5086), candidate.hostPort);
			assertEquals(8257636, candidate.priority);
			assertEquals(JingleS5BTransportPayload.Candidate.Type.DirectType, candidate.type);
				
			candidate = s5bPayload.getCandidates().get(1);
			assertEquals("hutr46fe", candidate.cid);
			assertEquals(new JID("romeo@montague.lit/orchard"), candidate.jid);
			assertEquals(new HostAddressPort(new HostAddress(InetAddress.getByName("24.24.24.1")), 5087), candidate.hostPort);
			assertEquals(8258636, candidate.priority);
			assertEquals(JingleS5BTransportPayload.Candidate.Type.DirectType, candidate.type);
		} catch (UnknownHostException e) {

		}
	}
		
	// http://xmpp.org/extensions/xep-0260.html#example-3
	@Test
	public void testParse_Xep0260_Example3() {
		DummyEventLoop eventLoop = new DummyEventLoop();
		PayloadsParserTester parser = new PayloadsParserTester(eventLoop);
		assertNotNull(parser.parse(
			"<jingle xmlns='urn:xmpp:jingle:1'\n" +
			"        action='session-accept'\n" +
			"        initiator='romeo@montague.lit/orchard'\n" +
			"        sid='a73sjjvkla37jfea'>\n" +
			"  <content creator='initiator' name='ex'>\n" +
			"    <description xmlns='urn:xmpp:example'/>\n" +
			"    <transport xmlns='urn:xmpp:jingle:transports:s5b:1'\n" +
			"               dstaddr='1a12fb7bc625e55f3ed5b29a53dbe0e4aa7d80ba'\n" +
			"               mode='tcp'\n" +
			"               sid='vj3hs98y'>\n" +
			"      <candidate cid='ht567dq'\n" +
			"                 host='192.169.1.10'\n" +
			"                 jid='juliet@capulet.lit/balcony'\n" +
			"                 port='6539'\n" +
			"                 priority='8257636'\n" +
			"                 type='direct'/>\n" +
			"      <candidate cid='hr65dqyd'\n" +
			"                 host='134.102.201.180'\n" +
			"                 jid='juliet@capulet.lit/balcony'\n" +
			"                 port='16453'\n" +
			"                 priority='7929856'\n" +
			"                 type='assisted'/>\n" +
			"      <candidate cid='grt654q2'\n" +
			"                 host='2001:638:708:30c9:219:d1ff:fea4:a17d'\n" +
			"                 jid='juliet@capulet.lit/balcony'\n" +
			"                 port='6539'\n" +
			"                 priority='8257606'\n" +
			"                 type='direct'/>\n" +
			"    </transport>\n" +
			"  </content>\n" +
			"</jingle>\n"
		));
			
		JinglePayload jingle = (JinglePayload)parser.getPayload();
		assertNotNull(jingle);
		assertEquals(JinglePayload.Action.SessionAccept, jingle.getAction());
		assertEquals(new JID("romeo@montague.lit/orchard"), jingle.getInitiator());
		assertEquals("a73sjjvkla37jfea", jingle.getSessionID());
			
		JingleContentPayload content = jingle.getPayload(new JingleContentPayload());
		assertNotNull(content);
			
		JingleS5BTransportPayload s5bPayload = content.getTransport(new JingleS5BTransportPayload());
		assertNotNull(s5bPayload);
			
		assertEquals("vj3hs98y", s5bPayload.getSessionID());
		assertEquals(JingleS5BTransportPayload.Mode.TCPMode, s5bPayload.getMode());
		assertEquals("1a12fb7bc625e55f3ed5b29a53dbe0e4aa7d80ba", s5bPayload.getDstAddr());
		assertEquals(false, s5bPayload.hasCandidateError());
		assertEquals(false, s5bPayload.hasProxyError());
		assertEquals("", s5bPayload.getActivated());
		assertEquals("", s5bPayload.getCandidateUsed());
		assertEquals(3, s5bPayload.getCandidates().size());

		try {
			JingleS5BTransportPayload.Candidate candidate;
			candidate = s5bPayload.getCandidates().get(0);
			assertEquals("ht567dq", candidate.cid);
			assertEquals(new JID("juliet@capulet.lit/balcony"), candidate.jid);
			assertEquals(new HostAddressPort(new HostAddress(InetAddress.getByName("192.169.1.10")), 6539), candidate.hostPort);
			assertEquals(8257636, candidate.priority);
			assertEquals(JingleS5BTransportPayload.Candidate.Type.DirectType, candidate.type);

			candidate = s5bPayload.getCandidates().get(1);
			assertEquals("hr65dqyd", candidate.cid);
			assertEquals(new JID("juliet@capulet.lit/balcony"), candidate.jid);
			assertEquals(new HostAddressPort(new HostAddress(InetAddress.getByName("134.102.201.180")), 16453), candidate.hostPort);
			assertEquals(7929856, candidate.priority);
			assertEquals(JingleS5BTransportPayload.Candidate.Type.AssistedType, candidate.type);

			candidate = s5bPayload.getCandidates().get(2);
			assertEquals("grt654q2", candidate.cid);
			assertEquals(new JID("juliet@capulet.lit/balcony"), candidate.jid);
			assertEquals(new HostAddressPort(new HostAddress(InetAddress.getByName("2001:638:708:30c9:219:d1ff:fea4:a17d")), 6539), candidate.hostPort);
			assertEquals(8257606, candidate.priority);
			assertEquals(JingleS5BTransportPayload.Candidate.Type.DirectType, candidate.type);
		} catch (UnknownHostException e) {

		}
	}
}