/*
 * Copyright (c) 2014 Isode Limited, London, England.
 * All rights reserved.
 */
package com.isode.stroke.elements;

import java.util.ArrayList;
import java.util.List;

/**
 * Class representing a XEP-0004 data form <item/> element.
 * @author tr
 *
 */
public class FormItem {

	private List<FormField> itemFields_ = new ArrayList<FormField>();
	
	public FormItem() {}
	
	/**
	 * Add a single FormField to this FormItem.
	 * @param itemField FormField, should not be null
	 */
	public void addItemField(FormField itemField) {
		if (itemField == null) {
			throw new NullPointerException("'itemField' must not be null");
		}
		itemFields_.add(itemField);
	}
	
	/**
	 * Add a list of FormFields to this FormItem.
	 * @param itemFields List<ForMField>, should not be null
	 */
	public void addItemFields(List<FormField> itemFields) {
		if (itemFields == null) {
			throw new NullPointerException("'itemFields' must not be null");
		}
		itemFields_.addAll(itemFields);
	}
	
	/**
	 * Returns a list of FormFields for this FormItem.
	 * @return List<FormField> list of item, never null
	 */
	public List<FormField> getItemFields() {
		return new ArrayList<FormField>(itemFields_);
	}
}
