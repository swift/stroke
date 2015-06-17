/*
 * Copyright (c) 2015 Tarun Gupta.
 * Licensed under the simplified BSD license.
 * See Documentation/Licenses/BSD-simplified.txt for more information.
 */

package com.isode.stroke.serializer.payloadserializers;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import com.isode.stroke.serializer.payloadserializers.JingleFileTransferDescriptionSerializer;
import com.isode.stroke.elements.JingleFileTransferDescription;
import com.isode.stroke.elements.JingleFileTransferFileInfo;
import com.isode.stroke.elements.HashElement;
import com.isode.stroke.base.DateTime;
import com.isode.stroke.base.ByteArray;
import java.util.Date;
import java.util.TimeZone;

public class JingleFileTransferDescriptionSerializerTest {

	/**
	* Default Constructor.
	*/
	public JingleFileTransferDescriptionSerializerTest() {
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
	}

	@Test
	public void testSerialize_FileInfo_withAllVariables() {
		JingleFileTransferDescriptionSerializer testling = new JingleFileTransferDescriptionSerializer();
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
		String expectedResult = "<description xmlns=\"urn:xmpp:jingle:apps:file-transfer:4\"><file><date>2015-06-11T20:55:50Z</date>" +
								"<desc>It is good.</desc><media-type>MediaAAC</media-type><name>Isaac</name><range offset=\"566\"/>" +
								"<size>513</size><hash algo=\"MD5\" xmlns=\"urn:xmpp:hashes:1\"/></file></description>";
		assertEquals(expectedResult, testling.serialize(description));
	}

	@Test
	public void testSerialize_FileInfo_withSomeVariables() {
		JingleFileTransferDescriptionSerializer testling = new JingleFileTransferDescriptionSerializer();
		JingleFileTransferDescription description = new JingleFileTransferDescription();

		JingleFileTransferFileInfo fileInfo = new JingleFileTransferFileInfo();
		fileInfo.setName("Isaac");
		fileInfo.setDescription("It is good.");
		fileInfo.setMediaType("MediaAAC");
		fileInfo.addHash(new HashElement("MD5", new ByteArray()));
		description.setFileInfo(fileInfo);
		String expectedResult = "<description xmlns=\"urn:xmpp:jingle:apps:file-transfer:4\"><file><desc>It is good.</desc><media-type>MediaAAC</media-type>" +
								"<name>Isaac</name><hash algo=\"MD5\" xmlns=\"urn:xmpp:hashes:1\"/></file></description>";
		assertEquals(expectedResult, testling.serialize(description));
	}	
}