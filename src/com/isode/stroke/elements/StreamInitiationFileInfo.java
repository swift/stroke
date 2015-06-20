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

package com.isode.stroke.elements;

import com.isode.stroke.elements.Payload;
import com.isode.stroke.base.NotNull;
import com.isode.stroke.base.DateTime;
import java.util.Date;

public class StreamInitiationFileInfo extends Payload {

	private String name = "";
	private String description;
	private long size;
	private String hash = "";
	private Date date;
	private String algo = "";
	private boolean supportsRangeRequests;
	private long rangeOffset;

	/**
	* Default Constructor.
	*/
	public StreamInitiationFileInfo() {
		this("", "", 0, "", null, "md5");
	}

	/**
	* Parameterized Constructor.
	* @param name, NotNull.
	*/
	public StreamInitiationFileInfo(String name) {
		this(name, "", 0, "", null, "md5");
	}

	/**
	* Parameterized Constructor.
	* @param name, NotNull.
	* @param description, NotNull.
	*/
	public StreamInitiationFileInfo(String name, String description) {
		this(name, description, 0, "", null, "md5");
	}

	/**
	* Parameterized Constructor.
	* @param name, NotNull.
	* @param description, NotNull.
	* @param size.
	*/
	public StreamInitiationFileInfo(String name, String description, long size) {
		this(name, description, size, "", null, "md5");
	}

	/**
	* Parameterized Constructor.
	* @param name, NotNull.
	* @param description, NotNull.
	* @param size.
	* @param hash, NotNull.
	*/
	public StreamInitiationFileInfo(String name, String description, long size, String hash) {
		this(name, description, size, hash, null, "md5");
	}

	/**
	* Parameterized Constructor.
	* @param name, NotNull.
	* @param description, NotNull.
	* @param size.
	* @param hash, NotNull.
	* @param date. Null means invalid date.
	*/
	public StreamInitiationFileInfo(String name, String description, long size, String hash, Date date) {
		this(name, description, size, hash, date, "md5");
	}

	/**
	* Parameterized Constructor.
	* @param name, NotNull.
	* @param description, NotNull.
	* @param size.
	* @param hash, NotNull.
	* @param date. Null means invalid date.
	* @param algo, NotNull.
	*/
	public StreamInitiationFileInfo(String name, String description, long size, String hash, Date date, String algo) {
		NotNull.exceptIfNull(name, "name");
		NotNull.exceptIfNull(description, "description");
		NotNull.exceptIfNull(hash, "hash");
		NotNull.exceptIfNull(algo, "algo");
		this.name = name;
		this.description = description;
		this.size = size;
		this.hash = hash;
		this.date = date;
		this.algo = algo;
		this.supportsRangeRequests = false;
		this.rangeOffset = 0L;
	}

	/**
	* @param name, NotNull.
	*/
	public void setName(String name) {
		NotNull.exceptIfNull(name, "name");
		this.name = name;
	}

	/**
	* @return name, NotNull.
	*/
	public String getName() {
		return name;
	}

	/**
	* @param description, NotNull.
	*/
	public void setDescription(String description) {
		NotNull.exceptIfNull(description, "description");
		this.description = description;
	}

	/**
	* @return description, NotNull.
	*/
	public String getDescription() {
		return description;
	}

	/**
	* @param size.
	*/
	public void setSize(long size) {
		this.size = size;
	}

	/**
	* @return size.
	*/
	public long getSize() {
		return size;
	}

	/**
	* @param hash, NotNull.
	*/
	public void setHash(String hash) {
		NotNull.exceptIfNull(hash, "hash");
		this.hash = hash;
	}

	/**
	* @return hash, NotNull.
	*/
	public String getHash() {
		return this.hash;
	}

	/**
	* @param date. Null means invalid date.
	*/
	public void setDate(Date date) {
		this.date = date;
	}

	/**
	* @return date, which may be null for an invalid date.
	*/
	public Date getDate() {
		return date;
	}

	/**
	* @param algo, NotNull.
	*/
	public void setAlgo(String algo) {
		NotNull.exceptIfNull(algo, "algo");
		this.algo = algo;
	}

	/**
	* @return algo, NotNull.
	*/
	public String getAlgo() {
		return this.algo;
	}

	/**
	* @param supportsRangeRequests.
	*/
	public void setSupportsRangeRequests(boolean supportsIt) {
		this.supportsRangeRequests = supportsIt;
	}

	/**
	* @return supportsRangeRequests.
	*/
	public boolean getSupportsRangeRequests() {
		return supportsRangeRequests;
	}

	/**
	* @param offset.
	*/
	public void setRangeOffset(long offset) {
		this.supportsRangeRequests = true;
		this.rangeOffset = offset;
	}

	/**
	* @return offset.
	*/
	public long getRangeOffset() {
		return rangeOffset;
	}
}
