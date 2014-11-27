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

import com.isode.stroke.jid.JID;

/**
 * This class implements the field element of a form.
 *
 * <p>
 * From http://xmpp.org/extensions/xep-0050.html: Data validation is the
 * responsibility of the form-processing entity (commonly a server, service, or
 * bot) rather than the form-submitting entity (commonly a client controlled by
 * a human user). This helps to meet the requirement for keeping client
 * implementations simple. If the form-processing entity determines that the
 * data provided is not valid, it SHOULD return a "Not Acceptable" error,
 * optionally providing a textual explanation.
 */
public class FormField {
	
    public static final String FORM_FIELD_ATTRIBUTE_VAR = "var";
    public static final String FORM_FIELD_ATTRIBUTE_LABEL = "label";
    public static final String FORM_FIELD_ELEMENT_REQUIRED = "required";
    public static final String FORM_FIELD_ELEMENT_DESC = "desc";
    public static final String FORM_FIELD_ELEMENT_VALUE = "value";
    public static final String FORM_FIELD_ATTRIBUTE_TYPE = "type";
    public static final String FORM_FIELD_ELEMENT_OPTION = "option";
    public static final String FORM_FIELD_ATTRIBUTE_OPTION_LABEL = "label";
    public static final String FORM_FIELD_ELEMENT_OPTION_VALUE = "value";
    private static final String ILLEGAL_ARG_EX_STR = "This API is not valid for getting a value of type ";

    private boolean required_;
    private List<Option> options_ = new ArrayList<Option>();
    private List<String> values_ = new ArrayList<String>();
    private String description_ = "";
    private String label_ = "";
    private String name_ = "";
    private Type type_;
    
    public enum Type {
	UNKNOWN_TYPE(""),
    	BOOLEAN_TYPE("boolean"),
    	FIXED_TYPE("fixed"),
    	HIDDEN_TYPE("hidden"),
    	LIST_SINGLE_TYPE("list-single"),
    	LIST_MULTI_TYPE("list-multi"),
    	TEXT_PRIVATE_TYPE("text-private"),
    	TEXT_MULTI_TYPE("text-multi"),
    	TEXT_SINGLE_TYPE("text-single"),
    	JID_MULTI_TYPE("jid-multi"),
    	JID_SINGLE_TYPE("jid-single");
    	private String description_;
    	private Type(String description) {
    		description_ = description;
    	}
    	public String getDescription() {
    		return description_;
    	}
    	
    	public static Type getTypeFromString(String string) {
        	for (Type type : Type.values()) {
        		if (type.getDescription().equals(string)) {
        			return type;
        		}
        	}
        	return Type.UNKNOWN_TYPE;
        }
    }
    
    public FormField() {
	this(Type.UNKNOWN_TYPE);
    }

    public FormField(Type type, String value) {
	this(type);
	addValue(value);
    }

    public FormField(Type type) {
    	type_ = type;
    	required_ = false;
    	if (type == Type.BOOLEAN_TYPE) {
    		setBoolValue(false);
    	}
    }
    
    /**
     * This class defines the option element that can be used in
     * ListSingleFormField and ListMultiFormField. This class is
     * immutable.
     */
    public static class Option {
        /**
         * Human-readable name for the option, will not be null
         */
        public final String label_;

        /**
         * Option value, will not be null
         */
        public final String value_;

        /**
         * Create an option element.
         * @param label Human-readable name for the option, must not be null
         * @param value Option value, must not be null
         */
        public Option(String label, String value) {
            if (label == null) {
                throw new NullPointerException("'label' must not be null");
            }
            if (value == null) {
                throw new NullPointerException("'value' must not be null");
            }
            label_ = label;
            value_ = value;
        }
    }

    /**
     * Set the unique identifier for the field in the form.
     * @param name unique identifier for the field in the form, must not be null
     */
    public void setName(String name) {
        if (name == null) {
            throw new NullPointerException("'name' must not be null");
        }
        name_ = name;
    }

    /**
     * @return unique identifier for the field, will never be null
     */
    public String getName() {
        return name_;
    }

    /**
     * Set the human-readable name for the field.
     * @param label human-readable name for the field, must not be null
     */
    public void setLabel(String label) {
        if (label == null) {
            throw new NullPointerException("'label' must not be null");
        }

        label_ = label;
    }

    /**
     * @return human-readable name for the field, will never be null
     */
    public String getLabel() {
        return label_;
    }

    /**
     * Set the natural-language description for the field.
     * @param description natural-language description for the field, must not
     *            be null
     */
    public void setDescription(String description) {
        if (description == null) {
            throw new NullPointerException("'description' must not be null");
        }

        description_ = description;
    }

    /**
     * @return natural-language description for the field, will never be null
     */
    public String getDescription() {
        return description_;
    }

    /**
     * Set if the field is required for the form to be considered valid.
     * @param required true if the field is required for the form to be
     *            considered valid
     */
    public void setRequired(boolean required) {
        required_ = required;
    }

    /**
     * @return true if the field is required for the form to be considered valid
     */
    public boolean getRequired() {
        return required_;
    }

    /**
     * Add to the list of options_ for this FormField.
     * @param option Option to add, must not be null
     */
    public void addOption(Option option) {
        if (option == null) {
            throw new NullPointerException("'option' must not be null");
        }
        options_.add(option);
    }

    /**
     * @return List of options_ for this FormField, will never be null. The instance
     *         of the list stored in the object is not returned, a copy is made.
     */
    public List<Option> getOptions() {
        return new ArrayList<Option>(options_);
    }
    
    /**
     * Clears the list of options_ for the FormField.
     */
    public void clearOptions() {
    	options_.clear();
    }

    /**
     * Add to values_ for this field. The values_ can be the defaults suggested in
     * a form of {@link Form.Type#FORM_TYPE} or results provided in a form of
     * {@link Type#RESULT_TYPE} or values_ submitted in a form of
     * {@link Type#SUBMIT_TYPE}.
     * @param value Value to add, must not be null
     */
    public void addValue(String value) {
        if (value == null) {
            throw new NullPointerException("'value' must not be null");
        }
        values_.add(value);
    }
    
    /**
     * Creates a single value for the FormField. This resets the values_ for this
     * field to a single argument.
     * @param value String, should not be null
     */
    public void setValue(String value) {
    	if (value == null ) {
    		throw new NullPointerException("'value' should not be null");
    	}
    	values_.clear();
    	values_.add(value);
    }
    
    /**
     * Resets the list of values for this field to the specified argument. The 
     * instance of the list stored in the object is not returned, a copy is made.
     * @param values List<String> of values
     */
    public void setValues(List<String> values) {
    	if (values == null) {
    		throw new NullPointerException("'values' must not be null");
    	}
    	values_ = new ArrayList<String>(values);
    }

    /**
     * @return List of values_ for this field, will never be null. The instance
     *         of the list stored in the object is not returned, a copy is made.
     */
    public List<String> getValues() {
    	if (values_ == null) {
    		values_ = new ArrayList<String>();
    	}
        return new ArrayList<String>(values_);
    }

    /**
     * Returns the type of the FormField.
     * @return type_ Type, never null
     */
    public Type getType() {
    	return type_;
    }
    
    /**
     * Sets the type of the FormField.
     * @param type Type, never null
     */
    public void setType(Type type) {
    	type_ = type;
    }
    
    /**
     * Returns the value of a FormField with the type boolean.
     * @return value boolean, will return false if FormField has no values
     */
    public boolean getBoolValue() {
	if (type_ != Type.BOOLEAN_TYPE && type_ != Type.UNKNOWN_TYPE) {
    		throw new IllegalArgumentException(ILLEGAL_ARG_EX_STR + type_);
    	}
        return values_.isEmpty() ? false : values_.get(0).equals("true") || values_.get(0).equals("1");
    }
    

    /**
     * Sets the value of a FormField with type boolean to a boolean value.
     * @param bool boolean
     */
    public void setBoolValue(boolean bool) {
    	values_.clear();
    	values_.add(bool ? "1" : "0");
    }
    
    /**
     * Returns a JID single value.
     * @return JID value, or empty JID is FormField has no values
     */
    public JID getJIDSingleValue() {
	if (type_ != Type.JID_SINGLE_TYPE && type_ != Type.UNKNOWN_TYPE) {
    		throw new IllegalArgumentException(ILLEGAL_ARG_EX_STR + type_);
    	}
    	return values_.isEmpty() ? new JID() : JID.fromString(values_.get(0));
    }

    /**
     * Gets the value at a specified index of a JID-multi FormField.
     * @param index index of the JID value
     * @return JID value, or empty JID is FormField has no values
     */
    public JID getJIDMultiValue(int index) {
	if (type_ != Type.JID_MULTI_TYPE && type_ != Type.UNKNOWN_TYPE) {
    		throw new IllegalArgumentException(ILLEGAL_ARG_EX_STR + type_);
    	}
    	return values_.isEmpty() ? new JID() : JID.fromString(values_.get(index));
    }

    /**
     * Gets the value of a FormField with the type text-private.
     * @return value String, empty String if FormField has no values
     */
    public String getTextPrivateValue() {
	if (type_ != Type.TEXT_PRIVATE_TYPE && type_ != Type.UNKNOWN_TYPE) {
    		throw new IllegalArgumentException(ILLEGAL_ARG_EX_STR + type_);
    	}
    	return values_.isEmpty() ? "" : values_.get(0);
    }

    /**
     * Gets the value of a FormField with the type fixed.
     * @return value String, or empty String if invalid FormField type
     */
    public String getFixedValue() {
	if (type_ != Type.FIXED_TYPE && type_ != Type.UNKNOWN_TYPE) {
    		throw new IllegalArgumentException(ILLEGAL_ARG_EX_STR + type_);
    	}
    	return values_.isEmpty() ? "" : values_.get(0);
    }
    
    /**
     * Gets the value of a FormField with the type text-single.
     * If unknown type, extract a string value.
     * @return value String, or empty String if invalid FormField type
     */
    public String getTextSingleValue() {
    	if (type_ != Type.TEXT_SINGLE_TYPE && type_ != Type.UNKNOWN_TYPE) {
    		throw new IllegalArgumentException(ILLEGAL_ARG_EX_STR + type_);
    	}
    	return values_.isEmpty() ? "" : values_.get(0);
    }
    
    /**
     * Gets the value of a FormField with the type text-multi.
     * @return value String
     */
    public String getTextMultiValue() {
	if (type_ != Type.TEXT_MULTI_TYPE && type_ != Type.UNKNOWN_TYPE) {
    		throw new IllegalArgumentException(ILLEGAL_ARG_EX_STR + type_);
    	}
    	StringBuilder val = new StringBuilder();
    	for (int i=0; i<values_.size(); i++) {
    		String s = values_.get(i);
    		if (i != 0) {
    			val.append("\n");
    		}
    		val.append(s);
    	}
    	return val.toString();
    }

    /**
     * Sets the value of a FormField with the type text-multi.
     * @param val String value to set, must not be null
     */
    public void setTextMultiValue(String val) {
	if (type_ != Type.TEXT_MULTI_TYPE && type_ != Type.UNKNOWN_TYPE) {
    		throw new IllegalArgumentException(ILLEGAL_ARG_EX_STR + type_);
    	}
    	values_.clear();
	if (val.indexOf("\r\n") != -1) {
    		for (String s : val.split("\r\n")) {
    			values_.add(s);
    		}
    	} 
	else if (val.indexOf("\n") != -1){
    		for (String s : val.split("\n")) {
    			values_.add(s);
    		}
    	} 
    	else {
    		values_.add(val);
    	}
    }

    @Override
    public String toString() {
        return FormField.class.getSimpleName() + "\nname: " + name_
                + "\nlabel: " + label_ + "\ndescription: " + description_
                + "\nrequired: " + required_;
    }
}
