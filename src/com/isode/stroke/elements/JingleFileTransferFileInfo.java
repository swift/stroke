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

import com.isode.stroke.elements.Payload;
import com.isode.stroke.elements.HashElement;
import com.isode.stroke.base.DateTime;
import com.isode.stroke.base.NotNull;
import com.isode.stroke.base.ByteArray;
import java.util.Date;
import java.util.TimeZone;
import java.util.Map;
import java.util.HashMap;

/**
 * @brief This class represents the file info used in XEP-0234.
 */
public class JingleFileTransferFileInfo extends Payload {

	private String name_ = "";
	private String description_ = "";
	private String mediaType_ = "";
	private long size_;
	private Date date_;
	private boolean supportsRangeRequests_;
	private long rangeOffset_;
	private Map<String, ByteArray> hashes_ = new HashMap<String, ByteArray>();

	/**
	* Default Constructor.
	*/
	public JingleFileTransferFileInfo() {
		this("", "", 0, new Date(0L));
	}

	/**
	* Parameterized Constructor.
	* @param name, NotNull.
	*/
	public JingleFileTransferFileInfo(String name) {
		this(name, "", 0, new Date(0L));
	}

	/**
	* Parameterized Constructor.
	* @param name, NotNull.
	* @param description, NotNull.
	*/
	public JingleFileTransferFileInfo(String name, String description) {
		this(name, description, 0, new Date(0L));
	}

	/**
	* Parameterized Constructor.
	* @param name, NotNull.
	* @param description, NotNull.
	* @param size.
	*/
	public JingleFileTransferFileInfo(String name, String description, long size) {
		this(name, description, size, new Date(0L));
	}

	/**
	* Parameterized Constructor.
	* @param name, NotNull.
	* @param description, NotNull.
	* @param size.
	* @param date, NotNull.
	*/
	public JingleFileTransferFileInfo(String name, String description, long size, Date date) {
		NotNull.exceptIfNull(name, "name");
		NotNull.exceptIfNull(description, "description");
		NotNull.exceptIfNull(date, "date");
		this.name_ = name;
		this.description_ = description;
		this.size_ = size;
		this.date_ = date;
		this.supportsRangeRequests_ = false;
		this.rangeOffset_ = 0;
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
	}

	/**
	* @param name, NotNull.
	*/
	public void setName(String name) {
		NotNull.exceptIfNull(name, "name");
		name_ = name;;
	}

	/**
	* @return name, NotNull.
	*/
	public String getName() {
		return name_;
	}

	/**
	* @param description, NotNull.
	*/
	public void setDescription(String description) {
		NotNull.exceptIfNull(description, "description");
		description_ = description;
	}

	/**
	* @return description, NotNull.
	*/
	public String getDescription() {
		return description_;
	}

	/**
	* @param mediaType, NotNull.
	*/
	public void setMediaType(String mediaType) {
		NotNull.exceptIfNull(mediaType, "mediaType");
		mediaType_ = mediaType;
	}

	/**
	* @return mediaType, NotNull.
	*/
	public String getMediaType() {
		return mediaType_;
	}

	/**
	* @param size.
	*/
	public void setSize(long size) {
		size_ = size;
	}

	/**
	* @return size.
	*/
	public long getSize() {
		return size_;
	}

	/**
	* @param date, NotNull.
	*/
	public void setDate(Date date) {
		NotNull.exceptIfNull(date, "date");
		date_ = date;
	}

	/**
	* @return date, NotNull.
	*/
	public Date getDate() {
		return date_;
	}

	/**
	* @param supportsRangeRequests_.
	*/
	public void setSupportsRangeRequests(boolean supportsIt) {
		supportsRangeRequests_ = supportsIt;
	}

	/**
	* @return supportsRangeRequests_.
	*/
	public boolean getSupportsRangeRequests() {
		return supportsRangeRequests_;
	}

	/**
	* @param offset.
	*/
	public void setRangeOffset(long offset) {
		supportsRangeRequests_ = true;
		rangeOffset_ = offset;
	}

	/**
	* @return offset.
	*/
	public long getRangeOffset() {
		return rangeOffset_;
	}

	/**
	* @param hash, NotNull.
	*/
	public void addHash(HashElement hash) {
		NotNull.exceptIfNull(hash, "hash");
		hashes_.put(hash.getAlgorithm(), hash.getHashValue());
	}

	/**
	* @return hashes map.
	*/
	public Map<String, ByteArray> getHashes() {
		return hashes_;
	}

	/**
	* @param algorithm, NotNull.
	* @return ByteArray, can be null.
	*/
	public ByteArray getHash(String algorithm) {
		NotNull.exceptIfNull(algorithm, "algorithm");
		ByteArray ret = null;
		if(hashes_.containsKey(algorithm)) {
			ret = new ByteArray(hashes_.get(algorithm));
		}
		return ret;
	}
}