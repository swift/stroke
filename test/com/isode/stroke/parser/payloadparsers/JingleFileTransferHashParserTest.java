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
import com.isode.stroke.elements.JingleFileTransferHash;
import com.isode.stroke.elements.JingleFileTransferFileInfo;
import com.isode.stroke.parser.payloadparsers.JingleFileTransferHashParser;
import com.isode.stroke.parser.payloadparsers.JingleFileTransferFileInfoParser;
import com.isode.stroke.parser.payloadparsers.PayloadsParserTester;
import com.isode.stroke.eventloop.DummyEventLoop;
import java.util.Date;
import com.isode.stroke.base.DateTime;
import com.isode.stroke.base.ByteArray;
import java.util.TimeZone;

public class JingleFileTransferHashParserTest {

	public JingleFileTransferHashParserTest() {
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
	}

	@Test
	public void testParse_with_all_variables() {
		DummyEventLoop eventLoop = new DummyEventLoop();
		PayloadsParserTester parser = new PayloadsParserTester(eventLoop);
		assertNotNull(parser.parse("<checksum xmlns=\"urn:xmpp:jingle:apps:file-transfer:4\"><file><date>2015-06-11T20:55:50Z</date><desc>It is good.</desc>" +
								"<media-type>MediaAAC</media-type>" +
								"<name>Isaac</name><range offset=\"566\"/><size>513</size>" +
								"<hash algo=\"MD5\" xmlns=\"urn:xmpp:hashes:1\"/></file></checksum>"));

		JingleFileTransferHash hash = (JingleFileTransferHash)parser.getPayload();
		assertNotNull(hash);

		JingleFileTransferFileInfo fileInfo = hash.getFileInfo();
		assertNotNull(fileInfo);

		assertEquals("Isaac", fileInfo.getName());
		assertEquals("It is good.", fileInfo.getDescription());
		assertEquals("MediaAAC", fileInfo.getMediaType());
		assertEquals(513L, fileInfo.getSize());
		assertEquals(DateTime.dateToString(new Date(1434056150620L)), DateTime.dateToString(fileInfo.getDate()));
		assertEquals(true, fileInfo.getSupportsRangeRequests());
		assertEquals(566L, fileInfo.getRangeOffset());
		assertEquals(new ByteArray(), fileInfo.getHash("MD5"));
	}

	@Test
	public void testParse_with_Some_variables() {
		DummyEventLoop eventLoop = new DummyEventLoop();
		PayloadsParserTester parser = new PayloadsParserTester(eventLoop);
		assertNotNull(parser.parse("<checksum xmlns=\"urn:xmpp:jingle:apps:file-transfer:4\"><file><desc>It is good.</desc><media-type>MediaAAC</media-type>" +
								"<name>Isaac</name><hash algo=\"MD5\" xmlns=\"urn:xmpp:hashes:1\"/></file></checksum>"));


		JingleFileTransferHash hash = (JingleFileTransferHash)parser.getPayload();
		assertNotNull(hash);

		JingleFileTransferFileInfo fileInfo = hash.getFileInfo();
		assertNotNull(fileInfo);

		assertEquals("Isaac", fileInfo.getName());
		assertEquals("It is good.", fileInfo.getDescription());
		assertEquals("MediaAAC", fileInfo.getMediaType());
		assertEquals(0L, fileInfo.getSize());
		assertNull(fileInfo.getDate());
		assertEquals(false, fileInfo.getSupportsRangeRequests());
		assertEquals(0L, fileInfo.getRangeOffset());
		assertEquals(new ByteArray(), fileInfo.getHash("MD5"));
	}

}