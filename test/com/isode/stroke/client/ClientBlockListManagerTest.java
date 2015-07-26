/*
 * Copyright (c) 2013 Tobias Markmann
 * Licensed under the simplified BSD license.
 * See Documentation/Licenses/BSD-simplified.txt for more information.
 */
/*
 * Copyright (c) 2015 Tarun Gupta.
 * Licensed under the simplified BSD license.
 * See Documentation/Licenses/BSD-simplified.txt for more information.
 */

package com.isode.stroke.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.junit.Before;
import com.isode.stroke.elements.BlockPayload;
import com.isode.stroke.elements.BlockListPayload;
import com.isode.stroke.elements.UnblockPayload;
import com.isode.stroke.client.StanzaChannel;
import com.isode.stroke.client.DummyStanzaChannel;
import com.isode.stroke.client.ClientBlockListManager;
import com.isode.stroke.queries.IQRouter;
import com.isode.stroke.elements.IQ;
import com.isode.stroke.queries.GenericRequest;
import com.isode.stroke.jid.JID;
import java.util.Vector;

public class ClientBlockListManagerTest {

	private JID ownJID_ = new JID();
	private IQRouter iqRouter_;
	private DummyStanzaChannel stanzaChannel_;
	private ClientBlockListManager clientBlockListManager_;

	private void helperInitialBlockListFetch(final Vector<JID> blockedJids) {
		BlockList blockList = clientBlockListManager_.requestBlockList();
		assertNotNull(blockList);

		// check for IQ request
		IQ request = stanzaChannel_.getStanzaAtIndex(new IQ(), 0);
		assertNotNull(request);
		BlockListPayload requestPayload = request.getPayload(new BlockListPayload());
		assertNotNull(requestPayload);

		assertEquals(BlockList.State.Requesting, blockList.getState());
		assertEquals(BlockList.State.Requesting, clientBlockListManager_.getBlockList().getState());

		// build IQ response
		BlockListPayload responsePayload = new BlockListPayload();
		for(final JID jid : blockedJids) {
			responsePayload.addItem(jid);
		}

		IQ response = IQ.createResult(ownJID_, new JID(), request.getID(), responsePayload);
		stanzaChannel_.sendIQ(response);
		stanzaChannel_.onIQReceived.emit(response);

		assertEquals(BlockList.State.Available, clientBlockListManager_.getBlockList().getState());
		assertEquals(responsePayload.getItems(), clientBlockListManager_.getBlockList().getItems());
	}

	@Before
	public void setUp() {
		ownJID_ = new JID("kev@wonderland.lit");
		stanzaChannel_ = new DummyStanzaChannel();
		iqRouter_ = new IQRouter(stanzaChannel_);
		iqRouter_.setJID(ownJID_);
		clientBlockListManager_ = new ClientBlockListManager(iqRouter_);
	}

	@Test
	public void testFetchBlockList() {
		Vector<JID> blockJids = new Vector<JID>();
		blockJids.add(new JID("romeo@montague.net"));
		blockJids.add(new JID("iago@shakespeare.lit"));
		helperInitialBlockListFetch(blockJids);

		assertEquals(2, clientBlockListManager_.getBlockList().getItems().size());
	}

	@Test
	public void testBlockCommand() {
		// start with an already fetched block list
		Vector<JID> vec = new Vector<JID>();
		vec.add(new JID("iago@shakespeare.lit"));
		helperInitialBlockListFetch(vec);

		assertEquals(1, clientBlockListManager_.getBlockList().getItems().size());
		assertEquals(BlockList.State.Available, clientBlockListManager_.getBlockList().getState());

		GenericRequest<BlockPayload> blockRequest = clientBlockListManager_.createBlockJIDRequest(new JID("romeo@montague.net"));
		blockRequest.send();
		IQ request = stanzaChannel_.getStanzaAtIndex(new IQ(), 2);
		assertNotNull(request);
		BlockPayload blockPayload = request.getPayload(new BlockPayload());
		assertNotNull(blockPayload);
		assertEquals(new JID("romeo@montague.net"), blockPayload.getItems().get(0));

		IQ blockRequestResponse = IQ.createResult(request.getFrom(), new JID(), request.getID());
		stanzaChannel_.sendIQ(blockRequestResponse);
		stanzaChannel_.onIQReceived.emit(blockRequestResponse);

		assertEquals((1), clientBlockListManager_.getBlockList().getItems().size());

		// send block push
		BlockPayload pushPayload = new BlockPayload();
		pushPayload.addItem(new JID("romeo@montague.net"));
		IQ blockPush = IQ.createRequest(IQ.Type.Set, ownJID_, "push1", pushPayload);
		stanzaChannel_.sendIQ(blockPush);
		stanzaChannel_.onIQReceived.emit(blockPush);

		Vector<JID> blockedJIDs = clientBlockListManager_.getBlockList().getItems();
		assertTrue(blockedJIDs.contains(new JID("romeo@montague.net")));
	}

	@Test
	public void testUnblockCommand() {
		// start with an already fetched block list
		Vector<JID> initialBlockList = new Vector<JID>();
		initialBlockList.add(new JID("iago@shakespeare.lit"));
		initialBlockList.add(new JID("romeo@montague.net"));
		helperInitialBlockListFetch(initialBlockList);

		assertEquals((2), clientBlockListManager_.getBlockList().getItems().size());
		assertEquals(BlockList.State.Available, clientBlockListManager_.getBlockList().getState());

		GenericRequest<UnblockPayload> unblockRequest = clientBlockListManager_.createUnblockJIDRequest(new JID("romeo@montague.net"));
		unblockRequest.send();
		IQ request = stanzaChannel_.getStanzaAtIndex(new IQ(), 2);
		assertNotNull(request);
		UnblockPayload unblockPayload = request.getPayload(new UnblockPayload());
		assertNotNull(unblockPayload);
		assertEquals(new JID("romeo@montague.net"), unblockPayload.getItems().get(0));

		IQ unblockRequestResponse = IQ.createResult(request.getFrom(), new JID(), request.getID());
		stanzaChannel_.sendIQ(unblockRequestResponse);
		stanzaChannel_.onIQReceived.emit(unblockRequestResponse);

		assertEquals((2), clientBlockListManager_.getBlockList().getItems().size());

		// send block push
		UnblockPayload pushPayload = new UnblockPayload();
		pushPayload.addItem(new JID("romeo@montague.net"));
		IQ unblockPush = IQ.createRequest(IQ.Type.Set, ownJID_, "push1", pushPayload);
		stanzaChannel_.sendIQ(unblockPush);
		stanzaChannel_.onIQReceived.emit(unblockPush);

		Vector<JID> blockedJIDs = clientBlockListManager_.getBlockList().getItems();
		assertFalse(blockedJIDs.contains(new JID("romeo@montague.net")));
	}

	@Test
	public void testUnblockAllCommand() {
		// start with an already fetched block list
		Vector<JID> initialBlockList = new Vector<JID>();
		initialBlockList.add(new JID("iago@shakespeare.lit"));
		initialBlockList.add(new JID("romeo@montague.net"));
		initialBlockList.add(new JID("benvolio@montague.net"));
		helperInitialBlockListFetch(initialBlockList);

		assertEquals((3), clientBlockListManager_.getBlockList().getItems().size());
		assertEquals(BlockList.State.Available, clientBlockListManager_.getBlockList().getState());

		GenericRequest<UnblockPayload> unblockRequest = clientBlockListManager_.createUnblockAllRequest();
		unblockRequest.send();
		IQ request = stanzaChannel_.getStanzaAtIndex(new IQ(), 2);
		assertNotNull(request);
		UnblockPayload unblockPayload = request.getPayload(new UnblockPayload());
		assertNotNull(unblockPayload);
		assertEquals(true, unblockPayload.getItems().isEmpty());

		IQ unblockRequestResponse = IQ.createResult(request.getFrom(), new JID(), request.getID());
		stanzaChannel_.sendIQ(unblockRequestResponse);
		stanzaChannel_.onIQReceived.emit(unblockRequestResponse);

		assertEquals((3), clientBlockListManager_.getBlockList().getItems().size());

		// send block push
		UnblockPayload pushPayload = new UnblockPayload();
		IQ unblockPush = IQ.createRequest(IQ.Type.Set, ownJID_, "push1", pushPayload);
		stanzaChannel_.sendIQ(unblockPush);
		stanzaChannel_.onIQReceived.emit(unblockPush);

		assertEquals(true, clientBlockListManager_.getBlockList().getItems().isEmpty());
	}
}