/*
 * Copyright (c) 2015 Tarun Gupta.
 * Licensed under the simplified BSD license.
 * See Documentation/Licenses/BSD-simplified.txt for more information.
 */

package com.isode.stroke.serializer.payloadserializers;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import com.isode.stroke.serializer.payloadserializers.JingleS5BTransportPayloadSerializer;
import com.isode.stroke.elements.JingleS5BTransportPayload;
import com.isode.stroke.jid.JID;
import com.isode.stroke.network.HostAddressPort;
import com.isode.stroke.network.HostAddress;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class JingleS5BTransportPayloadSerializerTest {

	/**
	* Default Constructor.
	*/
	public JingleS5BTransportPayloadSerializerTest() {

	}

	@Test
	public void testSerialize() {
		JingleS5BTransportPayloadSerializer testling = new JingleS5BTransportPayloadSerializer();
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

		String expectedResult = "<transport dstaddr=\"UK\" mode=\"tcp\" sid=\"\" xmlns=\"urn:xmpp:jingle:transports:s5b:1\">" +
								"<candidate cid=\"cid\" host=\"173.194.36.112\" jid=\"blas@nal.vx\" port=\"-1\" priority=\"4\" type=\"assisted\"/>" +
								"<proxy-error/><activated cid=\"Activity\"/><candidate-used cid=\"Candidate\"/></transport>";
		assertEquals(expectedResult, testling.serialize(jingleS5BTransportPayload));
	}
}