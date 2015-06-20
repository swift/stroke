/*
 * Copyright (c) 2012-2014 Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2010 Remko Tronçon
 * All rights reserved.
 */

package com.isode.stroke.elements;

import java.util.ArrayList;
import java.util.List;


/**
 * XEP-0004 Data form. For the relevant Fields, the parsers and serialisers
 * protect the API user against the strange multi-value instead of newline thing
 * by transforming them.
 */
public class Form extends Payload {
    /**
     * Attribute "type"
     */
    public static final String FORM_ATTRIBUTE_TYPE = "type";

    /**
     * Element "title"
     */
    public static final String FORM_ELEMENT_TITLE = "title";

    /**
     * Element "instructions"
     */
    public static final String FORM_ELEMENT_INSTRUCTIONS = "instructions";
    
    /**
     * Element "reported"
     */
    public static final String FORM_ELEMENT_REPORTED = "reported";
    
    /**
     * Element "item"
     */
    public static final String FORM_ELEMENT_ITEM = "item";

    /**
     * Element "field"
     */
    public static final String FORM_ELEMENT_FIELD = "field";

    /**
     * Type of form
     */
    public enum Type {
        /**
         * The form-processing entity is asking the form-submitting entity to
         * complete a form.
         */
        FORM_TYPE("form"),
        /**
         * The form-submitting entity is submitting data to the form-processing
         * entity.
         */
        SUBMIT_TYPE("submit"),
        /**
         * The form-submitting entity has cancelled submission of data to the
         * form-processing entity.
         */
        CANCEL_TYPE("cancel"),
        /**
         * The form-processing entity is returning data (e.g., search results)
         * to the form-submitting entity, or the data is a generic data set.
         */
        RESULT_TYPE("result");

        private String stringForm_;

        private Type(String stringForm) {
            stringForm_ = stringForm;
        }

        /**
         * Get type from its string form.
         *
         * @param stringForm String form of type, can be null
         *
         * @return Corresponding type if match found, otherwise
         *         {@link Type#FORM_TYPE}. Will never be null.
         */
        public static Type getType(String stringForm) {
            if (stringForm != null) {
                for (Type type : Type.values()) {
                    if (type.stringForm_.equals(stringForm)) {
                        return type;
                    }
                }
            }

            return FORM_TYPE;
        }

        /**
         * @return String form of type, will never be null
         */
        public String getStringForm() {
            return stringForm_;
        }
    }

    private List<FormReportedRef> reportedRefs_ = new ArrayList<FormReportedRef>();
    private List<FormText> textElements_ = new ArrayList<FormText>();
    private List<FormPage> pages_ = new ArrayList<FormPage>();
    private FormReportedRef reportedRef_;
    private List<FormField> fields_ = new ArrayList<FormField>();
    private List<FormField> reportedFields_ = new ArrayList<FormField>();
    private List<FormItem> items_ = new ArrayList<FormItem>();
    private String instructions_ = "";
    private String title_ = "";
    private Type type_;
    
    /**
     * Create a form of the given type.
     *
     * @param type Form type, must not be null
     */
    public Form(Type type) {
        setType(type);
    }

    /**
     * Create a form of {@link Type#FORM_TYPE}.
     */
    public Form() {
        this(Type.FORM_TYPE);
    }

    /**
    * @param reportedRef, Not Null.
    */
    public void addReportedRef(FormReportedRef reportedRef) {
        assert(reportedRef != null);
        reportedRefs_.add(reportedRef);
    }

    /**
    * @return reportedRef, Not Null.
    */
    public List<FormReportedRef> getReportedRefs() {
        return reportedRefs_;
    }

    /**
    * @param text, Not Null.
    */
    public void addTextElement(FormText text) {
        assert(text != null);
        textElements_.add(text);
    }

    /**
    * @return text, Not Null.
    */
    public List<FormText> getTextElements() {
        return textElements_;
    }

    /**
    * @return page, Not Null.
    */
    public void addPage(FormPage page) {
        assert(page != null);
        pages_.add(page);
    }

    /**
    * @return pages, Not Null.
    */
    public List<FormPage> getPages() {
        return pages_;
    }

    /**
     * Add to the list of fields for the form.
     *
     * @param field Field to add, must not be null. The instance of the form
     *            field is stored in the object, a copy is not made.
     */
    public void addField(FormField field) {
        if (field == null) {
            throw new NullPointerException("'field' must not be null");
        }
        fields_.add(field);
    }

    /**
     * @return List of fields for the form, will never be null. The instance of
     *         the list stored in the object is not returned, a copy is made.
     *         But the instances of the form fields stored in the list is the
     *         same as the list in the object, a copy is not made.
     */
    public List<FormField> getFields() {
        return new ArrayList<FormField>(fields_);
    }

    public void clearFields() {
        fields_.clear();
    }

    /**
     * Add a reported element to this Form.
     * @param reportedField should not be null
     */
    public void addReportedField(FormField reportedField) {
    	if (reportedField == null) {
    		throw new NullPointerException("'reportedField' should not be null");
    	}
    	reportedFields_.add(reportedField);
    }
    
    /**
     * Return the list of reported fields for this Form.
     * @return reportedFields_, never null
     */
    public List<FormField> getReportedFields() {
    	return reportedFields_;
    }
    
    /**
     * Add a list of item elements to the Form.
     * @param item List<FormField>, should not be null
     */
    public void addItem(FormItem item) {
    	if (item == null) {
    		throw new NullPointerException("'item' should not be null");
    	}
    	items_.add(item);
    }

    public void clearItems() { 
        items_.clear(); 
    }

    /**
     * Get the list of FormItem elements for the form.
     * @return itemsCopy ArrayList<List<FormItem>>, list of items for the Form,
     * 	a copy is made
     */
    public List<FormItem> getItems() {
    	return new ArrayList<FormItem>(items_);
    }
    
    /**
     * Remove all reported fields from this Form.
     */
    public void clearReportedFields() {
    	reportedFields_.clear();
    }

    /**
     * Set title of the form.
     *
     * @param title title of the form, must not be null
     */
    public void setTitle(String title) {
        if (title == null) {
            throw new NullPointerException("'title' must not be null");
        }

        title_ = title;
    }

    /**
     * @return title of the form, will never be null
     */
    public String getTitle() {
        return title_;
    }

    /**
     * Set natural-language instructions for the form.
     *
     * @param instructions instructions for the form, must not be null
     */
    public void setInstructions(String instructions) {
        if (instructions == null) {
            throw new NullPointerException("'instructions' must not be null");
        }

        instructions_ = instructions;
    }

    /**
     * @return natural-language instructions for the form, will never be null
     */
    public String getInstructions() {
        return instructions_;
    }

    /**
     * Set type of the form.
     *
     * @param type Form type, must not be null
     */
    public void setType(Type type) {
        if (type == null) {
            throw new NullPointerException("'type' must not be null");
        }

        type_ = type;
    }

    /**
     * @return type of the form, will never be null
     */
    public Type getType() {
        return type_;
    }

    /**
     * @return Value of the "FORM_TYPE" hidden form field if it is present in
     *         the form, an empty string otherwise, will never be null
     */
    public String getFormType() {
        FormField field = getField("FORM_TYPE");
        if (field != null && field.getType() == FormField.Type.HIDDEN_TYPE) {
        	return field.getValues().isEmpty() ? "" : field.getValues().get(0);
        }
        return "";
    }

    /**
     * Get form field for the given name.
     *
     * @param name Name of form field to retrieve, must not be null
     *
     * @return Form field with the given name if it is present in the form, null
     *         otherwise. The instance of the form field stored in the object is
     *         returned, a copy is not made.
     */
    public FormField getField(String name) {
        if (name == null) {
            throw new NullPointerException("'name' must not be null");
        }

        for (FormField field : fields_) {
            if (field.getName().equals(name)) {
                return field;
            }
        }

        return null;
    }

    public void clearEmptyTextFields() {
        List<FormField> populatedFields = new ArrayList<FormField>();
        for (FormField field : fields_) {
            if (field.getType() == FormField.Type.TEXT_SINGLE_TYPE) {
                if (!field.getTextSingleValue().isEmpty()) {
                    populatedFields.add(field);
                }
            }
            else if (field.getType() == FormField.Type.TEXT_MULTI_TYPE) {
                if (!field.getTextMultiValue().isEmpty()) {
                    populatedFields.add(field);
                }
            }
            else {
                populatedFields.add(field);
            }
        }
        fields_ = populatedFields;
    }

    @Override
    public String toString() {
        return Form.class.getSimpleName() + "\ntitle: " + title_
                + "\ninstructions: " + instructions_ + "\ntype: " + type_;
    }
}
