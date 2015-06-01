/*
 * Copyright (c) 2015, Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2011 Tobias Markmann
 * Licensed under the BSD license.
 * See http://www.opensource.org/licenses/bsd-license.php for more information.
 */

package com.isode.stroke.elements;

public class DeliveryReceipt extends Payload {

	private String receivedID_;

	public DeliveryReceipt() {receivedID_ = "";}

	public DeliveryReceipt(String msgId) {
		receivedID_= msgId;
	}

	public void setReceivedID(String msgId) {
		receivedID_ = msgId;
	}

	public String getReceivedID() {
		return receivedID_;
	}
}
