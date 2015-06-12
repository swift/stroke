/*
 * Copyright (c) 2010 Isode Limited.
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
import com.isode.stroke.elements.Form;

public class InBandRegistrationPayload extends Payload {

	private Form form;
	private boolean registered;
	private boolean remove;
	private String instructions;
	private String username;
	private String nick;
	private String password;
	private String name;
	private String first;
	private String last;
	private String email;
	private String address;
	private String city;
	private String state;
	private String zip;
	private String phone;
	private String url;
	private String date;
	private String misc;
	private String text;
	private String key;

	/**
	* Default Constructor.
	*/
	public InBandRegistrationPayload() {
		this.registered = false;
		this.remove = false;
	}

	/**
	* @return form.
	*/
	public Form getForm() { 
		return form; 
	}

	/**
	* @return registered.
	*/
	public boolean isRegistered() {
		return registered;
	}

	/**
	* @return remove.
	*/
	public boolean isRemove() {
		return remove;
	}

	/**
	* @return instructions.
	*/
	public String getInstructions() {
		return instructions;
	}

	/**
	* @return username.
	*/
	public String getUsername() {
		return username;
	}

	/**
	* @return nick.
	*/
	public String getNick() {
		return nick;
	}

	/**
	* @return password.
	*/
	public String getPassword() {
		return password;
	}

	/**
	* @return name.
	*/
	public String getName() {
		return name;
	}

	/**
	* @return first.
	*/
	public String getFirst() {
		return first;
	}

	/**
	* @return last.
	*/
	public String getLast() {
		return last;
	}

	/**
	* @return email.
	*/
	public String getEMail() {
		return email;
	}

	/**
	* @return address.
	*/
	public String getAddress() {
		return address;
	}

	/**
	* @return city.
	*/
	public String getCity() {
		return city;
	}

	/**
	* @return state.
	*/
	public String getState() {
		return state;
	}

	/**
	* @return zip.
	*/
	public String getZip() {
		return zip;
	}

	/**
	* @return phone.
	*/
	public String getPhone() {
		return phone;
	}

	/**
	* @return url.
	*/
	public String getURL() {
		return url;
	}

	/**
	* @return date.
	*/
	public String getDate() {
		return date;
	}

	/**
	* @return misc.
	*/
	public String getMisc() {
		return misc;
	}

	/**
	* @return text.
	*/
	public String getText() {
		return text;
	}

	/**
	* @return key.
	*/
	public String getKey() {
		return key;
	}

	/**
	* @param Form.
	*/
	public void setForm(Form f) {
		form = f; 
	}

	/**
	* @param remove.
	*/
	public void setRemove(boolean b) {
		remove = b;
	}

	/**
	* @param registered.
	*/
	public void setRegistered(boolean b) {
		registered = b;
	}

	/**
	* @param instructions.
	*/
	public void setInstructions(String v) {
		this.instructions = v;
	}

	/**
	* @param username.
	*/
	public void setUsername(String v) {
		this.username = v;
	}

	/**
	* @param nick.
	*/
	public void setNick(String v) {
		this.nick = v;
	}

	/**
	* @param password.
	*/
	public void setPassword(String v) {
		this.password = v;
	}

	/**
	* @param name.
	*/
	public void setName(String v) {
		this.name = v;
	}

	/**
	* @param first.
	*/
	public void setFirst(String v) {
		this.first = v;
	}

	/**
	* @param last.
	*/
	public void setLast(String v) {
		this.last = v;
	}

	/**
	* @param email.
	*/
	public void setEMail(String v) {
		this.email = v;
	}

	/**
	* @param address.
	*/
	public void setAddress(String v) {
		this.address = v;
	}

	/**
	* @param city.
	*/
	public void setCity(String v) {
		this.city = v;
	}

	/**
	* @param state.
	*/
	public void setState(String v) {
		this.state = v;
	}

	/**
	* @param zip.
	*/
	public void setZip(String v) {
		this.zip = v;
	}

	/**
	* @param phone.
	*/
	public void setPhone(String v) {
		this.phone = v;
	}

	/**
	* @param url.
	*/
	public void setURL(String v) {
		this.url = v;
	}

	/**
	* @param date.
	*/
	public void setDate(String v) {
		this.date = v;
	}

	/**
	* @param misc.
	*/
	public void setMisc(String v) {
		this.misc = v;
	}

	/**
	* @param text.
	*/
	public void setText(String v) {
		this.text = v;
	}

	/**
	* @param key.
	*/
	public void setKey(String v) {
		this.key = v;
	}
}
