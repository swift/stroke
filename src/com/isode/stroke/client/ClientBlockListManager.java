/*
 * Copyright (c) 2011-2015 Isode Limited.
 * All rights reserved.
 * See the COPYING file for more information.
 */
/*
 * Copyright (c) 2015 Tarun Gupta.
 * Licensed under the simplified BSD license.
 * See Documentation/Licenses/BSD-simplified.txt for more information.
 */

package com.isode.stroke.client;

import com.isode.stroke.elements.BlockPayload;
import com.isode.stroke.elements.BlockListPayload;
import com.isode.stroke.elements.UnblockPayload;
import com.isode.stroke.elements.DiscoInfo;
import com.isode.stroke.elements.ErrorPayload;
import com.isode.stroke.elements.IQ;
import com.isode.stroke.signals.Slot2;
import com.isode.stroke.queries.SetResponder;
import com.isode.stroke.queries.GenericRequest;
import com.isode.stroke.client.BlockList;
import com.isode.stroke.client.BlockListImpl;
import com.isode.stroke.queries.IQRouter;
import com.isode.stroke.jid.JID;
import java.util.Vector;

public class ClientBlockListManager {

	private IQRouter iqRouter;
	private SetResponder<BlockPayload> blockResponder;
	private SetResponder<UnblockPayload> unblockResponder;
	private BlockListImpl blockList;

	class BlockResponder extends SetResponder<BlockPayload> {

		public BlockResponder(BlockListImpl blockList, IQRouter iqRouter) {
			super(new BlockPayload(), iqRouter);
			this.blockList = blockList;
		}

		public boolean handleSetRequest(final JID from, final JID to, final String id, BlockPayload payload) {
			if (getIQRouter().isAccountJID(from)) {
					if (payload != null) {
						blockList.addItems(payload.getItems());
					}
					sendResponse(from, id, null);
			}
			else {
				sendError(from, id, ErrorPayload.Condition.NotAuthorized, ErrorPayload.Type.Cancel);
			}
			return true;
		}

		private BlockListImpl blockList;
	};

	class UnblockResponder extends SetResponder<UnblockPayload> {

		public UnblockResponder(BlockListImpl blockList, IQRouter iqRouter) {
			super(new UnblockPayload(), iqRouter);
			this.blockList = blockList;
		}

		public boolean handleSetRequest(final JID from, final JID to, final String id, UnblockPayload payload) {
			if (getIQRouter().isAccountJID(from)) {
				if (payload != null) {
					if (payload.getItems().isEmpty()) {
						blockList.removeAllItems();
					}
					else {
						blockList.removeItems(payload.getItems());
					}
				}
				sendResponse(from, id, null);
			}
			else {
				sendError(from, id, ErrorPayload.Condition.NotAuthorized, ErrorPayload.Type.Cancel);
			}
			return true;
		}

		private BlockListImpl blockList;
	};

	public ClientBlockListManager(IQRouter iqRouter) {
		this.iqRouter = iqRouter;
	}

	protected void finalize() throws Throwable {
		try {
			if (blockList != null && BlockList.State.Available.equals(blockList.getState())) {
				unblockResponder.stop();
				blockResponder.stop();
			}
		}
		finally {
			super.finalize();
		}
	}

	/**
	 * Returns the blocklist.
	 */
	public BlockList getBlockList() {
		if (blockList == null) {
			blockList = new BlockListImpl();
			blockList.setState(BlockList.State.Init);
		}
		return blockList;
	}

	/**
	 * Get the blocklist from the server.
	 */
	public BlockList requestBlockList() {
		if (blockList == null) {
			blockList = new BlockListImpl();
		}
		blockList.setState(BlockList.State.Requesting);
		GenericRequest<BlockListPayload> getRequest = new GenericRequest<BlockListPayload>(IQ.Type.Get, new JID(), new BlockListPayload(), iqRouter);
		getRequest.onResponse.connect(new Slot2<BlockListPayload, ErrorPayload>() {
			@Override
			public void call(BlockListPayload p, ErrorPayload e) {
				handleBlockListReceived(p, e);
			}
		});
		getRequest.send();
		return blockList;		
	}

	public GenericRequest<BlockPayload> createBlockJIDRequest(final JID jid) {
		Vector<JID> vec = new Vector<JID>();
		vec.add(jid);
		return createBlockJIDsRequest(vec);
	}

	public GenericRequest<BlockPayload> createBlockJIDsRequest(final Vector<JID> jids) {
		BlockPayload payload = new BlockPayload(jids);
		return new GenericRequest<BlockPayload>(IQ.Type.Set, new JID(), payload, iqRouter);
	}

	public GenericRequest<UnblockPayload> createUnblockJIDRequest(final JID jid) {
		Vector<JID> vec = new Vector<JID>();
		vec.add(jid);		
		return createUnblockJIDsRequest(vec);
	}

	public GenericRequest<UnblockPayload> createUnblockJIDsRequest(final Vector<JID> jids) {
		UnblockPayload payload = new UnblockPayload(jids);
		return new GenericRequest<UnblockPayload>(IQ.Type.Set, new JID(), payload, iqRouter);
	}

	public GenericRequest<UnblockPayload> createUnblockAllRequest() {
		return createUnblockJIDsRequest(new Vector<JID>());
	}

	private void handleBlockListReceived(BlockListPayload payload, ErrorPayload error) {
		if (error != null || payload == null) {
			blockList.setState(BlockList.State.Error);
		}
		else {
			blockList.setItems(payload.getItems());
			blockList.setState(BlockList.State.Available);
			if (blockResponder == null) {
				blockResponder = new BlockResponder(blockList, iqRouter);
				blockResponder.start();
			}
			if (unblockResponder == null) {
				unblockResponder = new UnblockResponder(blockList, iqRouter);
				unblockResponder.start();
			}
		}
	}
}