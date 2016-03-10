/*
 * Copyright (c) 2010-2016 Isode Limited.
 * All rights reserved.
 * See the COPYING file for more information.
 */
/*
 * Copyright (c) 2015 Tarun Gupta.
 * Licensed under the simplified BSD license.
 * See Documentation/Licenses/BSD-simplified.txt for more information.
 */

package com.isode.stroke.disco;

import com.isode.stroke.jid.JID;
import com.isode.stroke.elements.DiscoInfo;
import com.isode.stroke.elements.DiscoItems;
import com.isode.stroke.elements.ErrorPayload;
import com.isode.stroke.disco.GetDiscoInfoRequest;
import com.isode.stroke.disco.GetDiscoItemsRequest;
import com.isode.stroke.queries.IQRouter;
import com.isode.stroke.signals.Signal;
import com.isode.stroke.signals.Slot2;
import com.isode.stroke.signals.Signal2;
import com.isode.stroke.signals.SignalConnection;
import com.isode.stroke.base.NotNull;

import java.util.logging.Logger;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.isode.stroke.base.NotNull;

/**
 * Recursively walk service discovery trees to find all services offered.
 * This stops on any disco item that's not reporting itself as a server.
 */
public class DiscoServiceWalker {

	private JID service_;
	private IQRouter iqRouter_;
	private long maxSteps_;
	private boolean active_;
	private Set<JID> servicesBeingSearched_ = new HashSet<JID>();
	private Set<JID> searchedServices_ = new HashSet<JID>();
	private Set<GetDiscoInfoRequest> pendingDiscoInfoRequests_ = new HashSet<GetDiscoInfoRequest>();
	private Set<GetDiscoItemsRequest> pendingDiscoItemsRequests_ = new HashSet<GetDiscoItemsRequest>();
	private Logger logger_ = Logger.getLogger(this.getClass().getName());
	private SignalConnection onServiceFoundConnection;
	private SignalConnection onWalkAbortedConnection;
	private SignalConnection onWalkCompleteConnection;
	
	/**
	 * The discoInfo.onResponse connections for each GetDiscoInfoRequest
	 */
	private Map<GetDiscoInfoRequest,SignalConnection> onResponseDiscoInfoConnections =
	        new HashMap<GetDiscoInfoRequest,SignalConnection>();
	
	/**
	 * The discoItems.onResponse connections for each GetDiscoItemsRequest
	 */
	private Map<GetDiscoItemsRequest,SignalConnection> onResponseDiscoItemsConnections =
	        new HashMap<GetDiscoItemsRequest,SignalConnection>();

	/** Emitted for each service found. */
	public final Signal2<JID, DiscoInfo> onServiceFound = new Signal2<JID, DiscoInfo>();

	/** Emitted when walking is aborted. */
	public final Signal onWalkAborted = new Signal();

	/** Emitted when walking is complete.*/
	public final Signal onWalkComplete = new Signal();

	/**
	* Parameterized Constructor.
	* @param service, Not Null.
	* @param iqRouter, Not Null.
	*/
	public DiscoServiceWalker(JID service, IQRouter iqRouter) {
		this(service, iqRouter, 200);
	}

	/**
	* Parameterized Constructor.
	* @param service, Not Null.
	* @param iqRouter, Not Null.
	* @param maxSteps.
	*/
	public DiscoServiceWalker(JID service, IQRouter iqRouter, long maxSteps) {
		NotNull.exceptIfNull(service, "service");
		NotNull.exceptIfNull(iqRouter, "iqRouter");
		this.service_ = service;
		this.iqRouter_ = iqRouter;
		this.maxSteps_ = maxSteps;
		this.active_ = false;
	}

	/**
	 * Start the walk.
	 *
	 * Call this exactly once.
	 */
	public void beginWalk() {
		logger_.fine("Starting walk to " + service_ + "\n");
		assert(!active_);
		assert(servicesBeingSearched_.isEmpty());
		active_ = true;
		walkNode(service_);
	}

	/**
	 * End the walk.
	 */
	public void endWalk() {
	    if (active_) {
	        logger_.fine("Ending walk to" + service_ + "\n");
	        for (SignalConnection discoInfoConnection : onResponseDiscoInfoConnections.values()) {
	            discoInfoConnection.disconnect();
	        }
	        onResponseDiscoInfoConnections.clear();
	        for (SignalConnection discoItemsConnection : onResponseDiscoItemsConnections.values()) {
	            discoItemsConnection.disconnect();
	        }
	        onResponseDiscoItemsConnections.clear();
	        active_ = false;
	        onWalkAborted.emit();
	    }		
	}

	public boolean isActive() {
		return active_;
	}

	private void walkNode(JID jid) {
		logger_.fine("Walking node" + jid + "\n");
		servicesBeingSearched_.add(jid);
		searchedServices_.add(jid);
		final GetDiscoInfoRequest discoInfoRequest = GetDiscoInfoRequest.create(jid, iqRouter_);
		SignalConnection connection = discoInfoRequest.onResponse.connect(new Slot2<DiscoInfo, ErrorPayload>() {

			@Override
			public void call(DiscoInfo info, ErrorPayload error) {
				handleDiscoInfoResponse(info, error, discoInfoRequest);
			}
		});
		onResponseDiscoInfoConnections.put(discoInfoRequest, connection);
		pendingDiscoInfoRequests_.add(discoInfoRequest);
		discoInfoRequest.send();
	}

	private void markNodeCompleted(JID jid) {
		logger_.fine("Node completed " + jid + "\n");
		servicesBeingSearched_.remove(jid);
		/* All results are in */
		if (servicesBeingSearched_.isEmpty()) {
			active_ = false;
			onWalkComplete.emit();
		}
		/* Check if we're on a rampage */
		else if (searchedServices_.size() >= maxSteps_) {
			active_ = false;
			onWalkComplete.emit();
		}
	}

	private void handleDiscoInfoResponse(DiscoInfo info, ErrorPayload error, GetDiscoInfoRequest request) {
		/* If we got canceled, don't do anything */
		if (!active_) {
			return;
		}

		logger_.fine("Disco info response from " + request.getReceiver() + "\n");

		
		SignalConnection connection = onResponseDiscoInfoConnections.remove(request);
		connection.disconnect();
		
		pendingDiscoInfoRequests_.remove(request);
		if (error != null) {
			handleDiscoError(request.getReceiver(), error);
			return;
		}

		boolean couldContainServices = false;
		for (DiscoInfo.Identity identity : info.getIdentities()) {
			if (identity.getCategory().equals("server")) {
				couldContainServices = true;
			}
		}
		boolean completed = false;
		if (couldContainServices) {
			final GetDiscoItemsRequest discoItemsRequest = GetDiscoItemsRequest.create(request.getReceiver(), iqRouter_);
			SignalConnection discoItemsConnection = discoItemsRequest.onResponse.connect(new Slot2<DiscoItems, ErrorPayload>() {

				@Override
				public void call(DiscoItems item, ErrorPayload error) {
					handleDiscoItemsResponse(item, error, discoItemsRequest);
				}
			});
			onResponseDiscoItemsConnections.put(discoItemsRequest, discoItemsConnection);
			pendingDiscoItemsRequests_.add(discoItemsRequest);
			discoItemsRequest.send();
		} else {
			completed = true;
		}
		onServiceFound.emit(request.getReceiver(), info);
		if (completed) {
			markNodeCompleted(request.getReceiver());
		}		
	}

	private void handleDiscoItemsResponse(DiscoItems items, ErrorPayload error, GetDiscoItemsRequest request) {
	    /* If we got canceled, don't do anything */
	    if (!active_) {
	        return;
	    }

	    logger_.fine("Received disco items from " + request.getReceiver() + "\n");

	    SignalConnection connection = onResponseDiscoItemsConnections.remove(request);
	    connection.disconnect();

	    pendingDiscoItemsRequests_.remove(request);
	    if (error != null) {
	        handleDiscoError(request.getReceiver(), error);
	        return;
	    }
	    for (DiscoItems.Item item : items.getItems()) {
	        if (item.getNode().isEmpty()) {
	            /* Don't look at noded items. It's possible that this will exclude some services,
	             * but I've never seen one in the wild, and it's an easy fix for not looping.
	             */
	            if(!searchedServices_.contains(item.getJID())) {
	                logger_.fine("Received disco item " + item.getJID() + "\n");
	                walkNode(item.getJID());
	            }
	        }
	    }
	    markNodeCompleted(request.getReceiver());
	}

	private void handleDiscoError(JID jid, ErrorPayload error) {
		logger_.fine("Disco error from " + jid + "\n");
		markNodeCompleted(jid);
	}
}