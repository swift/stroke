/*
 * Copyright (c) 2010-2015, Isode Limited, London, England.
 * All rights reserved.
 */
package com.isode.stroke.elements;

import java.util.ArrayList;
import java.util.Collection;

public class SecurityLabel extends Payload {

	private Collection<String> equivalentLabels = new ArrayList<String>();
	private String foregroundColor = "";
	private String displayMarking = "";
	private String backgroundColor = "";
	private String label = "";

	public final Collection<String> getEquivalentLabels() {
		return equivalentLabels;
	}

	public void setEquivalentLabels(final Collection<String> value) {
		this.equivalentLabels = value;
	}

	public void addEquivalentLabel(final String value) {
		this.equivalentLabels.add(value);
	}

	public final String getForegroundColor() {
		return foregroundColor;
	}

	public void setForegroundColor(final String value) {
		this.foregroundColor = value;
	}

	public final String getDisplayMarking() {
		return displayMarking;
	}

	public void setDisplayMarking(final String value) {
		this.displayMarking = value;
	}

	public final String getBackgroundColor() {
		return backgroundColor;
	}

	public void setBackgroundColor(final String value) {
		this.backgroundColor = value;
	}

	public final String getLabel() {
		return label;
	}

	public void setLabel(final String value) {
		this.label = value;
	}

}
