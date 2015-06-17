/*
 * Copyright (c) 2015 Tarun Gupta.
 * Licensed under the simplified BSD license.
 * See Documentation/Licenses/BSD-simplified.txt for more information.
 */

package com.isode.stroke.serializer.payloadserializers;

import static org.junit.Assert.assertEquals;
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
import com.isode.stroke.network.HostAddress;
import com.isode.stroke.network.HostAddressPort;
import java.util.Date;
import java.util.TimeZone;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class JingleContentPayloadSerializerTest {

	/**
	* Default Constructor.
	*/
	public JingleContentPayloadSerializerTest() {
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
	}

	@Test
	public void testSerialize() {
		JingleContentPayloadSerializer testling = new JingleContentPayloadSerializer();
		JingleContentPayload content = new JingleContentPayload();
		content.setCreator(JingleContentPayload.Creator.InitiatorCreator);
		content.setName("Erin");

		JingleFileTransferDescription description = new JingleFileTransferDescription();
		JingleFileTransferFileInfo fileInfo = new JingleFileTransferFileInfo();
		fileInfo.setName("Isaac");
		fileInfo.setDescription("It is good.");
		fileInfo.setMediaType("MediaAAC");
		fileInfo.setSize(513L);
		fileInfo.setDate(new Date(1434056150620L));
		fileInfo.setSupportsRangeRequests(true);
		fileInfo.setRangeOffset(566L);
		fileInfo.addHash(new HashElement("MD5", new ByteArray()));
		description.setFileInfo(fileInfo);
		content.addDescription(description);

		JingleIBBTransportPayload jingleIBBTransportPayload = new JingleIBBTransportPayload();
		jingleIBBTransportPayload.setBlockSize(4);
		jingleIBBTransportPayload.setSessionID("546-45");
		content.addTransport(jingleIBBTransportPayload);

		JingleS5BTransportPayload jingleS5BTransportPayload = new JingleS5BTransportPayload();
		jingleS5BTransportPayload.setMode(JingleS5BTransportPayload.Mode.TCPMode);
		JingleS5BTransportPayload.Candidate candidate = new JingleS5BTransportPayload.Candidate();
		candidate.cid = "cid";
		candidate.jid = new JID("blas@nal.vx");
		try {
			candidate.hostPort = new HostAddressPort(new HostAddress(InetAddress.getByName("173.194.36.112")));
		} catch (UnknownHostException e) {

		}
		candidate.priority = 4;
		candidate.type = JingleS5BTransportPayload.Candidate.Type.AssistedType;
		jingleS5BTransportPayload.addCandidate(candidate);
		jingleS5BTransportPayload.setCandidateUsed("Candidate");
		jingleS5BTransportPayload.setActivated("Activity");
		jingleS5BTransportPayload.setDstAddr("UK");
		jingleS5BTransportPayload.setCandidateError(false);
		jingleS5BTransportPayload.setProxyError(true);
		content.addTransport(jingleS5BTransportPayload);

		String expectedResult = "<content creator=\"initiator\" name=\"Erin\"><description xmlns=\"urn:xmpp:jingle:apps:file-transfer:4\">" +
								"<file><date>2015-06-11T20:55:50Z</date><desc>It is good.</desc><media-type>MediaAAC</media-type>" +
								"<name>Isaac</name><range offset=\"566\"/><size>513</size><hash algo=\"MD5\" xmlns=\"urn:xmpp:hashes:1\"/></file>" +
								"</description><transport block-size=\"4\" sid=\"546-45\" xmlns=\"urn:xmpp:jingle:transports:ibb:1\"/>" +
								"<transport dstaddr=\"UK\" mode=\"tcp\" sid=\"\" xmlns=\"urn:xmpp:jingle:transports:s5b:1\">" +
								"<candidate cid=\"cid\" host=\"173.194.36.112\" jid=\"blas@nal.vx\" port=\"-1\" priority=\"4\" type=\"assisted\"/><proxy-error/>" +
								"<activated cid=\"Activity\"/><candidate-used cid=\"Candidate\"/></transport></content>";
		assertEquals(expectedResult, testling.serialize(content));
	}

	@Test
	public void testSerialize_MultiplePayloads() {
		JingleContentPayloadSerializer testling = new JingleContentPayloadSerializer();
		JingleContentPayload content = new JingleContentPayload();
		content.setCreator(JingleContentPayload.Creator.InitiatorCreator);
		content.setName("Erin");

		JingleFileTransferDescription description = new JingleFileTransferDescription();
		JingleFileTransferFileInfo fileInfo = new JingleFileTransferFileInfo();
		fileInfo.setName("Isaac");
		fileInfo.setDescription("It is good.");
		fileInfo.setMediaType("MediaAAC");
		fileInfo.setSize(513L);
		fileInfo.setDate(new Date(1434056150620L));
		fileInfo.setSupportsRangeRequests(true);
		fileInfo.setRangeOffset(566L);
		fileInfo.addHash(new HashElement("MD5", new ByteArray()));
		description.setFileInfo(fileInfo);
		content.addDescription(description);

		JingleFileTransferDescription description2 = new JingleFileTransferDescription();
		JingleFileTransferFileInfo fileInfo2 = new JingleFileTransferFileInfo();
		fileInfo2.setName("Newton");
		fileInfo2.setDescription("It is bad.");
		fileInfo2.setMediaType("MediaJPG");
		fileInfo2.setSize(556L);
		fileInfo2.setSupportsRangeRequests(false);
		fileInfo2.addHash(new HashElement("SHA-1", new ByteArray()));
		description2.setFileInfo(fileInfo2);
		content.addDescription(description2);

		JingleIBBTransportPayload jingleIBBTransportPayload = new JingleIBBTransportPayload();
		jingleIBBTransportPayload.setBlockSize(4);
		jingleIBBTransportPayload.setSessionID("546-45");
		content.addTransport(jingleIBBTransportPayload);


		JingleIBBTransportPayload jingleIBBTransportPayload2 = new JingleIBBTransportPayload();
		jingleIBBTransportPayload2.setBlockSize(43);
		jingleIBBTransportPayload2.setSessionID("546-452");
		content.addTransport(jingleIBBTransportPayload2);

		JingleS5BTransportPayload jingleS5BTransportPayload = new JingleS5BTransportPayload();
		jingleS5BTransportPayload.setMode(JingleS5BTransportPayload.Mode.TCPMode);
		JingleS5BTransportPayload.Candidate candidate = new JingleS5BTransportPayload.Candidate();
		candidate.cid = "cid";
		candidate.jid = new JID("blas@nal.vx");
		try {
			candidate.hostPort = new HostAddressPort(new HostAddress(InetAddress.getByName("173.194.36.112")));
		} catch (UnknownHostException e) {

		}
		candidate.priority = 4;
		candidate.type = JingleS5BTransportPayload.Candidate.Type.AssistedType;
		jingleS5BTransportPayload.addCandidate(candidate);
		jingleS5BTransportPayload.setCandidateUsed("Candidate");
		jingleS5BTransportPayload.setActivated("Activity");
		jingleS5BTransportPayload.setDstAddr("UK");
		jingleS5BTransportPayload.setCandidateError(false);
		jingleS5BTransportPayload.setProxyError(true);
		content.addTransport(jingleS5BTransportPayload);

		JingleS5BTransportPayload jingleS5BTransportPayload2 = new JingleS5BTransportPayload();
		jingleS5BTransportPayload2.setMode(JingleS5BTransportPayload.Mode.TCPMode);
		JingleS5BTransportPayload.Candidate candidate2 = new JingleS5BTransportPayload.Candidate();
		candidate2.cid = "cid";
		candidate2.jid = new JID("blas@nal.vx");
		try {
			candidate2.hostPort = new HostAddressPort(new HostAddress(InetAddress.getByName("173.194.36.112")));
		} catch (UnknownHostException e) {

		}
		candidate2.priority = 4;
		candidate2.type = JingleS5BTransportPayload.Candidate.Type.AssistedType;
		jingleS5BTransportPayload2.addCandidate(candidate2);		
		jingleS5BTransportPayload2.setCandidateUsed("Candy");
		jingleS5BTransportPayload2.setActivated("Active");
		jingleS5BTransportPayload2.setDstAddr("USA");
		jingleS5BTransportPayload2.setCandidateError(true);
		jingleS5BTransportPayload2.setProxyError(false);
		content.addTransport(jingleS5BTransportPayload2);

		String expectedResult = "<content creator=\"initiator\" name=\"Erin\"><description xmlns=\"urn:xmpp:jingle:apps:file-transfer:4\"><file>" +
								"<date>2015-06-11T20:55:50Z</date><desc>It is good.</desc><media-type>MediaAAC</media-type><name>Isaac</name>" +
								"<range offset=\"566\"/><size>513</size><hash algo=\"MD5\" xmlns=\"urn:xmpp:hashes:1\"/></file></description>" +
								"<description xmlns=\"urn:xmpp:jingle:apps:file-transfer:4\"><file><desc>It is bad.</desc><media-type>MediaJPG</media-type>" +
								"<name>Newton</name><size>556</size><hash algo=\"SHA-1\" xmlns=\"urn:xmpp:hashes:1\"/></file></description>" +
								"<transport block-size=\"4\" sid=\"546-45\" xmlns=\"urn:xmpp:jingle:transports:ibb:1\"/>" +
								"<transport block-size=\"43\" sid=\"546-452\" xmlns=\"urn:xmpp:jingle:transports:ibb:1\"/>" +
								"<transport dstaddr=\"UK\" mode=\"tcp\" sid=\"\" xmlns=\"urn:xmpp:jingle:transports:s5b:1\">" +
								"<candidate cid=\"cid\" host=\"173.194.36.112\" jid=\"blas@nal.vx\" port=\"-1\" priority=\"4\" type=\"assisted\"/><proxy-error/>" +
								"<activated cid=\"Activity\"/><candidate-used cid=\"Candidate\"/></transport>" +
								"<transport dstaddr=\"USA\" mode=\"tcp\" sid=\"\" xmlns=\"urn:xmpp:jingle:transports:s5b:1\">" +
								"<candidate cid=\"cid\" host=\"173.194.36.112\" jid=\"blas@nal.vx\" port=\"-1\" priority=\"4\" type=\"assisted\"/><candidate-error/>" +
								"<activated cid=\"Active\"/><candidate-used cid=\"Candy\"/></transport></content>";
		assertEquals(expectedResult, testling.serialize(content));
	}
}