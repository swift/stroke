/*
 * Copyright (c) 2015 Isode Limited.
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
import com.isode.stroke.elements.FormField;
import com.isode.stroke.elements.FormReportedRef;
import com.isode.stroke.elements.FormText;
import com.isode.stroke.elements.FormSection;
import java.util.Vector;

public class FormPage {

	private String label_ = "";
	private String xmlns_ = "";
	private Vector<FormText> textElements_ = new Vector<FormText>();
	private Vector<FormReportedRef> reportedRefs_ = new Vector<FormReportedRef>();
	private Vector<FormSection> childSections_ = new Vector<FormSection>();
	private Vector<FormField> fields_ = new Vector<FormField>();
	private Vector<String> fieldRefs_ = new Vector<String>();

	/**
	* Default Constructor.
	*/
	public FormPage() {
		this.xmlns_ = "http://jabber.org/protocol/xdata-layout";
	}

	/**
	* @param label, Not Null.
	*/
	public void setLabel(String label) {
		NotNull.exceptIfNull(label, "label");
		label_ = label;
	}

	/**
	* @return label, Not Null.
	*/
	public String getLabel() {
		return label_;
	}

	/**
	* @return xmlns, Not Null.
	*/
	public String getXMLNS() {
		return xmlns_;
	}

	/**
	* @param textElement, Not Null.
	*/
	public void addTextElement(FormText textElement) {
		NotNull.exceptIfNull(textElement, "textElement");
		textElements_.add(textElement);
	}

	/**
	* @return textElement, Not Null.
	*/
	public Vector<FormText> getTextElements() {
		return textElements_;
	}

	/**
	* @param reportedRef, Not Null.
	*/
	public void addReportedRef(FormReportedRef reportedRef) {
		NotNull.exceptIfNull(reportedRef, "reportedRef");
		reportedRefs_.add(reportedRef);
	}

	/**
	* @return reportedRef, Not Null.
	*/
	public Vector<FormReportedRef> getReportedRefs() {
		return reportedRefs_;
	}

	/**
	* @param childSection, Not Null.
	*/
	public void addChildSection(FormSection childSection) {
		NotNull.exceptIfNull(childSection, "childSection");
		childSections_.add(childSection);
	}

	/**
	* @return childSection, Not Null.
	*/
	public Vector<FormSection> getChildSections() {
		return childSections_;
	}

	/**
	* @param field, Not Null.
	*/
	public void addField(FormField field) {
		NotNull.exceptIfNull(field, "field");
		fields_.add(field);
	}

	/**
	* @return field, Not Null.
	*/
	public Vector<FormField> getFields() {
		return fields_;
	}

	/**
	* @param ref, Not Null.
	*/
	public void addFieldRef(String ref) {
		NotNull.exceptIfNull(ref, "ref");
		fieldRefs_.add(ref);
	}

	/**
	* @return ref, Not Null.
	*/
	public Vector<String> getFieldRefs() {
		return fieldRefs_;
	}
}