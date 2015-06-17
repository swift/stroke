/*
 * Copyright (c) 2015 Tarun Gupta.
 * Licensed under the simplified BSD license.
 * See Documentation/Licenses/BSD-simplified.txt for more information.
 */

package com.isode.stroke.parser.payloadparsers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import org.junit.Test;
import com.isode.stroke.serializer.payloadserializers.JingleContentPayloadSerializer;
import com.isode.stroke.elements.JingleContentPayload;
import com.isode.stroke.elements.JingleFileTransferDescription;
import com.isode.stroke.elements.JingleFileTransferFileInfo;
import com.isode.stroke.elements.JingleIBBTransportPayload;
import com.isode.stroke.elements.JingleS5BTransportPayload;
import com.isode.stroke.elements.HashElement;
import com.isode.stroke.base.DateTime;
import com.isode.stroke.base.ByteArray;
import com.isode.stroke.jid.JID;
import java.util.Date;
import java.util.TimeZone;
import com.isode.stroke.parser.payloadparsers.PayloadsParserTester;
import com.isode.stroke.eventloop.DummyEventLoop;

public class JingleContentPayloadParserTest {

	public JingleContentPayloadParserTest() {
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
	}

	@Test
	public void testParse() {
		DummyEventLoop eventLoop = new DummyEventLoop();
		PayloadsParserTester parser = new PayloadsParserTester(eventLoop);
		assertNotNull(parser.parse("<content xmlns=\"urn:xmpp:jingle:1\" creator=\"initiator\" name=\"Erin\"><description xmlns=\"urn:xmpp:jingle:apps:file-transfer:4\">" +
								"<file><date>2015-06-11T20:55:50Z</date><desc>It is good.</desc><media-type>MediaAAC</media-type>" +
								"<name>Isaac</name><range offset=\"566\"/><size>513</size><hash algo=\"MD5\" xmlns=\"urn:xmpp:hashes:1\"/></file>" +
								"</description><transport block-size=\"4\" sid=\"546-45\" xmlns=\"urn:xmpp:jingle:transports:ibb:1\"/>" +
								"<transport dstaddr=\"UK\" mode=\"tcp\" sid=\"\" xmlns=\"urn:xmpp:jingle:transports:s5b:1\">" +
								"<candidate cid=\"cid\" host=\"173.194.36.112\" jid=\"blas@nal.vx\" port=\"-1\" priority=\"4\" type=\"assisted\"/><proxy-error/>" +
								"<activated cid=\"Activity\"/><candidate-used cid=\"Candidate\"/></transport></content>"));

		JingleContentPayload content = (JingleContentPayload)parser.getPayload();

		assertEquals (JingleContentPayload.Creator.InitiatorCreator, content.getCreator());
		assertEquals ("Erin", content.getName());


		assertEquals(1, content.getDescriptions().size());
		JingleFileTransferDescription description = content.getDescription(new JingleFileTransferDescription());
		assertNotNull(description);
		JingleFileTransferFileInfo fileInfo = description.getFileInfo();
		assertNotNull(fileInfo);
		assertEquals("Isaac", fileInfo.getName());
		assertEquals("It is good.", fileInfo.getDescription());
		assertEquals("MediaAAC", fileInfo.getMediaType());
		assertEquals(513L, fileInfo.getSize());
		assertEquals(DateTime.dateToString(new Date(1434056150620L)), DateTime.dateToString(fileInfo.getDate()));
		assertEquals(true, fileInfo.getSupportsRangeRequests());
		assertEquals(566L, fileInfo.getRangeOffset());
		assertEquals(new ByteArray(), fileInfo.getHash("MD5"));

		assertEquals(2, content.getTransports().size());
		JingleIBBTransportPayload jingleIBBTransportPayload = content.getTransport(new JingleIBBTransportPayload());
		assertNotNull(jingleIBBTransportPayload);
		assertEquals(Integer.valueOf(4), jingleIBBTransportPayload.getBlockSize());
		assertEquals("546-45", jingleIBBTransportPayload.getSessionID());
	
		JingleS5BTransportPayload jingleS5BTransportPayload = content.getTransport(new JingleS5BTransportPayload());
		assertNotNull(jingleS5BTransportPayload);
		assertEquals (JingleS5BTransportPayload.Mode.TCPMode, jingleS5BTransportPayload.getMode());
		assertEquals ("Candidate", jingleS5BTransportPayload.getCandidateUsed());
		assertEquals ("Activity", jingleS5BTransportPayload.getActivated());
		assertEquals ("UK", jingleS5BTransportPayload.getDstAddr());
		assertEquals (false, jingleS5BTransportPayload.hasCandidateError());
		assertEquals (true, jingleS5BTransportPayload.hasProxyError());

		assertEquals(1, jingleS5BTransportPayload.getCandidates().size());
		JingleS5BTransportPayload.Candidate candidate = jingleS5BTransportPayload.getCandidates().get(0);
		assertEquals("cid", candidate.cid);
		assertEquals(new JID("blas@nal.vx"), candidate.jid);
		assertEquals("/173.194.36.112", candidate.hostPort.getAddress().getInetAddress().toString());
		assertEquals(4, candidate.priority);
		assertEquals(JingleS5BTransportPayload.Candidate.Type.AssistedType, candidate.type);
		
	}
}