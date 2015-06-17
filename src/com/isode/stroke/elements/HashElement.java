/*
 * Copyright (c) 2014 Isode Limited.
 * All rights reserved.
 * See the COPYING file for more information.
 */
/*
 * Copyright (c) 2015 Tarun Gupta.
 * Licensed under the simplified BSD license.
 * See Documentation/Licenses/BSD-simplified.txt for more information.
 */

package com.isode.stroke.elements;

import com.isode.stroke.base.NotNull;
import com.isode.stroke.base.ByteArray;

public class HashElement {

	private String algorithm_ = "";
	private ByteArray hash_;

	public HashElement(String algorithm, ByteArray hash) {
		algorithm_ = algorithm;
		hash_ = hash;
	}

	/**
	* @param algorithm, Not Null.
	* @param hash, Not Null.
	*/
	public void setHashValue(String algorithm, ByteArray hash) {
		NotNull.exceptIfNull(algorithm, "algorithm");
		NotNull.exceptIfNull(hash, "hash");
		algorithm_ = algorithm;
		hash_ = hash;
	}

	/**
	* @return algorithm, Not Null.
	*/
	public String getAlgorithm() {
		return algorithm_;
	}

	/**
	* @return hash, Not Null.
	*/
	public ByteArray getHashValue() {
		return hash_;
	}

	public boolean equals(Object other) {

		if ((!(other instanceof HashElement)) || other == null) {
			return false;
		}

		HashElement guest = (HashElement) other;
		return 	(algorithm_.equals(guest.algorithm_)) && (hash_.equals(guest.hash_));
	}
}