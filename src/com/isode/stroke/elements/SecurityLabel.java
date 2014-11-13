/*
 * Copyright (c) 2010-2015, Isode Limited, London, England.
 * All rights reserved.
 */
package com.isode.stroke.elements;

import java.util.ArrayList;
import java.util.Collection;

public class SecurityLabel extends Payload {

	private Collection<String> equivalentLabels = new ArrayList<String>();
	private String foregroundColor;
	private String displayMarking;
	private String backgroundColor;
	private String label;

	final Collection<String> getEquivalentLabels() {
		return equivalentLabels;
	}

	void setEquivalentLabels(final Collection<String> value) {
		this.equivalentLabels = value ;
	}

	void addEquivalentLabel(final String value) {
		this.equivalentLabels.add(value);
	}

	final String getForegroundColor() {
		return foregroundColor;
	}

	void setForegroundColor(final String value) {
		this.foregroundColor = value ;
	}

	final String getDisplayMarking() {
		return displayMarking;
	}

	void setDisplayMarking(final String value) {
		this.displayMarking = value ;
	}

	final String getBackgroundColor() {
		return backgroundColor;
	}

	void setBackgroundColor(final String value) {
		this.backgroundColor = value ;
	}

	final String getLabel() {
		return label;
	}

	void setLabel(final String value) {
		this.label = value ;
	}

}
