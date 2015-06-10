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

public class UserTune extends Payload {

	private Integer rating;
	private String title;
	private String track;
	private String artist;
	private String uri;
	private String source;
	private Integer length;

	/**
	* Default Constructor.
	*/
	public UserTune() {

	}

	/**
	* @return rating.
	*/
	public Integer getRating() {
		return rating;
	}

	/**
	* @param rating.
	*/
	public void setRating(Integer value) {
		this.rating = value;
	}

	/**
	* @return title.
	*/
	public String getTitle() {
		return title;
	}

	/**
	* @param title.
	*/
	public void setTitle(String value) {
		this.title = value;
	}

	/**
	* @return track.
	*/
	public String getTrack() {
		return track;
	}

	/**
	* @param track.
	*/
	public void setTrack(String value) {
		this.track = value;
	}

	/**
	* @return artist.
	*/
	public String getArtist() {
		return artist;
	}

	/**
	* @param artist.
	*/
	public void setArtist(String value) {
		this.artist = value;
	}	

	/**
	* @return URI.
	*/
	public String getURI() {
		return uri;
	}

	/**
	* @param URI.
	*/
	public void setURI(String value) {
		this.uri = value;
	}

	/**
	* @return source.
	*/
	public String getSource() {
		return source;
	}

	/**
	* @param source.
	*/
	public void setSource(String value) {
		this.source = value;
	}

	/**
	* @return length.
	*/
	public Integer getLength() {
		return length;
	}

	/**
	* @param length.
	*/
	public void setLength(Integer value) {
		this.length = value;
	}
}
