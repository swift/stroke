/*
 * Copyright (c) 2015 Tarun Gupta.
 * Licensed under the simplified BSD license.
 * See Documentation/Licenses/BSD-simplified.txt for more information.
 */

package com.isode.stroke.serializer.payloadserializers;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import com.isode.stroke.serializer.payloadserializers.JingleFileTransferHashSerializer;
import com.isode.stroke.elements.JingleFileTransferHash;
import com.isode.stroke.elements.JingleFileTransferFileInfo;
import com.isode.stroke.elements.HashElement;
import com.isode.stroke.base.DateTime;
import com.isode.stroke.base.ByteArray;
import java.util.Date;

public class JingleFileTransferHashSerializerTest {

	/**
	* Default Constructor.
	*/
	public JingleFileTransferHashSerializerTest() {

	}

	@Test
	public void testSerialize_FileInfo_withAllVariables() {
		JingleFileTransferHashSerializer testling = new JingleFileTransferHashSerializer();
		JingleFileTransferHash hash = new JingleFileTransferHash();

		JingleFileTransferFileInfo fileInfo = new JingleFileTransferFileInfo();
		fileInfo.setName("Isaac");
		fileInfo.setDescription("It is good.");
		fileInfo.setMediaType("MediaAAC");
		fileInfo.setSize(513L);
		fileInfo.setDate(new Date(1434056150620L));
		fileInfo.setSupportsRangeRequests(true);
		fileInfo.setRangeOffset(566L);
		fileInfo.addHash(new HashElement("MD5", new ByteArray()));

		hash.setFileInfo(fileInfo);
		String expectedResult = "<checksum xmlns=\"urn:xmpp:jingle:apps:file-transfer:4\"><file><date>2015-06-11T20:55:50Z</date>" +
								"<desc>It is good.</desc><media-type>MediaAAC</media-type><name>Isaac</name><range offset=\"566\"/>" +
								"<size>513</size><hash algo=\"MD5\" xmlns=\"urn:xmpp:hashes:1\"/></file></checksum>";
		assertEquals(expectedResult, testling.serialize(hash));
	}

	@Test
	public void testSerialize_FileInfo_withSomeVariables() {
		JingleFileTransferHashSerializer testling = new JingleFileTransferHashSerializer();
		JingleFileTransferHash hash = new JingleFileTransferHash();

		JingleFileTransferFileInfo fileInfo = new JingleFileTransferFileInfo();
		fileInfo.setName("Isaac");
		fileInfo.setDescription("It is good.");
		fileInfo.setMediaType("MediaAAC");
		fileInfo.addHash(new HashElement("MD5", new ByteArray()));
		hash.setFileInfo(fileInfo);
		String expectedResult = "<checksum xmlns=\"urn:xmpp:jingle:apps:file-transfer:4\"><file><desc>It is good.</desc><media-type>MediaAAC</media-type>" +
								"<name>Isaac</name><hash algo=\"MD5\" xmlns=\"urn:xmpp:hashes:1\"/></file></checksum>";
		assertEquals(expectedResult, testling.serialize(hash));
	}	
}