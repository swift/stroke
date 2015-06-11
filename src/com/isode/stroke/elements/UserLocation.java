/*
 * Copyright (c) 2013 Isode Limited.
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
import com.isode.stroke.elements.Payload;
import java.util.Date;
import java.util.TimeZone;

public class UserLocation extends Payload {

	private String area;
	private Float altitude;
	private String locality;
	private Float latitude;
	private Float accuracy;
	private String description;
	private String countryCode;
	private Date timestamp;
	private String floor;
	private String building;
	private String room;
	private String country;
	private String region;
	private String uri;
	private Float longitude;
	private Float error;
	private String postalCode;
	private Float bearing;
	private String text;
	private String datum;
	private String street;
	private Float speed;

	public UserLocation() {

	}

	/**
	* @return Area.
	*/
	public String getArea() {
		return area;
	}

	/**
	* @param Area.
	*/
	public void setArea(String value) {
		this.area = value ;
	}

	/**
	* @return altitude.
	*/
	public Float getAltitude() {
		return altitude;
	}

	/**
	* @param altitude.
	*/
	public void setAltitude(Float value) {
		this.altitude = value ;
	}

	/**
	* @return locality.
	*/
	public String getLocality() {
		return locality;
	}

	/**
	* @param locality.
	*/
	public void setLocality(String value) {
		this.locality = value ;
	}

	/**
	* @return latitude.
	*/
	public Float getLatitude() {
		return latitude;
	}

	/**
	* @param latitude.
	*/
	public void setLatitude(Float value) {
		this.latitude = value ;
	}

	/**
	* @return accuracy.
	*/
	public Float getAccuracy() {
		return accuracy;
	}

	/**
	* @param accuracy.
	*/
	public void setAccuracy(Float value) {
		this.accuracy = value ;
	}

	/**
	* @return description.
	*/
	public String getDescription() {
		return description;
	}

	/**
	* @param description.
	*/
	public void setDescription(String value) {
		this.description = value ;
	}

	/**
	* @return countryCode.
	*/
	public String getCountryCode() {
		return countryCode;
	}

	/**
	* @param countryCode.
	*/
	public void setCountryCode(String value) {
		this.countryCode = value ;
	}

	/**
	* @return timestamp.
	*/
	public Date getTimestamp() {
		return timestamp;
	}

	/**
	* @param timestamp.
	*/
	public void setTimestamp(Date value) {
		this.timestamp = value ;
	}

	/**
	* @return floor.
	*/
	public String getFloor() {
		return floor;
	}

	/**
	* @param floor.
	*/
	public void setFloor(String value) {
		this.floor = value ;
	}

	/**
	* @return building.
	*/
	public String getBuilding() {
		return building;
	}

	/**
	* @param building.
	*/
	public void setBuilding(String value) {
		this.building = value ;
	}

	/**
	* @return room.
	*/
	public String getRoom() {
		return room;
	}

	/**
	* @param room.
	*/
	public void setRoom(String value) {
		this.room = value ;
	}

	/**
	* @return country.
	*/
	public String getCountry() {
		return country;
	}

	/**
	* @param country.
	*/
	public void setCountry(String value) {
		this.country = value ;
	}

	/**
	* @return region.
	*/
	public String getRegion() {
		return region;
	}

	/**
	* @param region.
	*/
	public void setRegion(String value) {
		this.region = value ;
	}

	/**
	* @return uri.
	*/
	public String getURI() {
		return uri;
	}

	/**
	* @param uri.
	*/
	public void setURI(String value) {
		this.uri = value ;
	}

	/**
	* @return longitude.
	*/
	public Float getLongitude() {
		return longitude;
	}

	/**
	* @param longitude.
	*/
	public void setLongitude(Float value) {
		this.longitude = value ;
	}

	/**
	* @return error.
	*/
	public Float getError() {
		return error;
	}

	/**
	* @param error.
	*/
	public void setError(Float value) {
		this.error = value ;
	}

	/**
	* @return postalCode.
	*/
	public String getPostalCode() {
		return postalCode;
	}

	/**
	* @param postalCode.
	*/
	public void setPostalCode(String value) {
		this.postalCode = value ;
	}

	/**
	* @return bearing.
	*/
	public Float getBearing() {
		return bearing;
	}

	/**
	* @param bearing.
	*/
	public void setBearing(Float value) {
		this.bearing = value ;
	}

	/**
	* @return text.
	*/
	public String getText() {
		return text;
	}

	/**
	* @param text.
	*/
	public void setText(String value) {
		this.text = value ;
	}

	/**
	* @return datum.
	*/
	public String getDatum() {
		return datum;
	}

	/**
	* @param datum.
	*/
	public void setDatum(String value) {
		this.datum = value ;
	}

	/**
	* @return street.
	*/
	public String getStreet() {
	return street;
	}

	/**
	* @param street.
	*/
	public void setStreet(String value) {
		this.street = value ;
	}

	/**
	* @return speed.
	*/
	public Float getSpeed() {
		return speed;
	}

	/**
	* @param speed.
	*/
	public void setSpeed(Float value) {
		this.speed = value ;
	}
}