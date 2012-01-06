/*
 * Copyright (c) 2012 Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2010 Remko Tronçon
 * All rights reserved.
 */

package com.isode.stroke.elements;

import java.util.ArrayList;
import java.util.List;

import com.isode.stroke.elements.Form.Type;
import com.isode.stroke.jid.JID;

/**
 * This class implements the field element of a form.
 * 
 * <p>
 * Data validation is the responsibility of the form-processing entity (commonly
 * a server, service, or bot) rather than the form-submitting entity (commonly a
 * client controlled by a human user). This helps to meet the requirement for
 * keeping client implementations simple. If the form-processing entity
 * determines that the data provided is not valid, it SHOULD return a
 * "Not Acceptable" error, optionally providing a textual explanation.
 */
public class FormField {
    /**
     * Attribute "var"
     */
    public static final String FORM_FIELD_ATTRIBUTE_VAR = "var";

    /**
     * Attribute "label"
     */
    public static final String FORM_FIELD_ATTRIBUTE_LABEL = "label";

    /**
     * Element "required"
     */
    public static final String FORM_FIELD_ELEMENT_REQUIRED = "required";

    /**
     * Element "desc"
     */
    public static final String FORM_FIELD_ELEMENT_DESC = "desc";

    /**
     * Element "value"
     */
    public static final String FORM_FIELD_ELEMENT_VALUE = "value";

    /**
     * Attribute "type"
     */
    public static final String FORM_FIELD_ATTRIBUTE_TYPE = "type";

    /**
     * Element "option"
     */
    public static final String FORM_FIELD_ELEMENT_OPTION = "option";

    /**
     * Attribute option "label"
     */
    public static final String FORM_FIELD_ATTRIBUTE_OPTION_LABEL = "label";

    /**
     * Element option "value"
     */
    public static final String FORM_FIELD_ELEMENT_OPTION_VALUE = "value";

    /**
     * Type "boolean"
     */
    public static final String FORM_FIELD_TYPE_BOOLEAN = "boolean";

    /**
     * Type "fixed"
     */
    public static final String FORM_FIELD_TYPE_FIXED = "fixed";

    /**
     * Type "hidden"
     */
    public static final String FORM_FIELD_TYPE_HIDDEN = "hidden";

    /**
     * Type "list-single"
     */
    public static final String FORM_FIELD_TYPE_LIST_SINGLE = "list-single";

    /**
     * Type "text-private"
     */
    public static final String FORM_FIELD_TYPE_TEXT_PRIVATE = "text-private";

    /**
     * Type "text-single"
     */
    public static final String FORM_FIELD_TYPE_TEXT_SINGLE = "text-single";

    /**
     * Type "jid-multi"
     */
    public static final String FORM_FIELD_TYPE_JID_MULTI = "jid-multi";

    /**
     * Type "jid-single"
     */
    public static final String FORM_FIELD_TYPE_JID_SINGLE = "jid-single";

    /**
     * Type "list-multi"
     */
    public static final String FORM_FIELD_TYPE_LIST_MULTI = "list-multi";

    /**
     * Type "text-multi"
     */
    public static final String FORM_FIELD_TYPE_TEXT_MULTI = "text-multi";

    /**
     * This class defines the option element that can be used in
     * {@link ListSingleFormField} and {@link ListMultiFormField}. TODO: This
     * class should be immutable.
     */
    public static class Option {
        /**
         * Human-readable name for the option, must not be null
         */
        public String label;

        /**
         * Option value, must not be null
         */
        public String value;

        /**
         * Create an option element.
         * 
         * @param label Human-readable name for the option, can be null in which
         *            case an empty string will be stored
         * @param value Option value, must not be null
         */
        public Option(String label, String value) {
            if (value == null) {
                throw new NullPointerException("'value' must not be null");
            }

            this.label = (label != null) ? label : "";
            this.value = value;
        }
    }

    private String name = "";
    private String label = "";
    private String description = "";
    private boolean required;
    private List<Option> options = new ArrayList<Option>();
    private List<String> rawValues = new ArrayList<String>();

    protected FormField() {
        required = false;
    }

    /**
     * Set the unique identifier for the field in the form.
     * 
     * @param name unique identifier for the field in the form, can be null in
     *            which case an empty string will be stored
     */
    public void setName(String name) {
        this.name = (name != null) ? name : "";
    }

    /**
     * @return unique identifier for the field, will never be null
     */
    public String getName() {
        return name;
    }

    /**
     * Set the human-readable name for the field.
     * 
     * @param label human-readable name for the field, can be null in which case
     *            an empty string will be stored
     */
    public void setLabel(String label) {
        this.label = (label != null) ? label : "";
    }

    /**
     * @return human-readable name for the field, will never be null
     */
    public String getLabel() {
        return label;
    }

    /**
     * Set the natural-language description for the field.
     * 
     * @param description natural-language description for the field, can be
     *            null in which case an empty string will be stored
     */
    public void setDescription(String description) {
        this.description = (description != null) ? description : "";
    }

    /**
     * @return natural-language description for the field, will never be null
     */
    public String getDescription() {
        return description;
    }

    /**
     * Set if the field is required for the form to be considered valid.
     * 
     * @param required true if the field is required for the form to be
     *            considered valid
     */
    public void setRequired(boolean required) {
        this.required = required;
    }

    /**
     * @return true if the field is required for the form to be considered valid
     */
    public boolean getRequired() {
        return required;
    }

    /**
     * Add to the list of options for this form field.
     * 
     * @param option Option to add, can be null in which case it will be ignored
     */
    public void addOption(Option option) {
        if (option != null) {
            options.add(option);
        }
    }

    /**
     * @return List of options for this form, will never be null
     */
    public List<Option> getOptions() {
        return new ArrayList<Option>(options);
    }

    /**
     * Add to values for this field. The values can be the defaults suggested in
     * a form of {@link Type#FORM_TYPE} or results provided in a form of
     * {@link Type#RESULT_TYPE} or values submitted in a form of
     * {@link Type#SUBMIT_TYPE}.
     * 
     * @param value Value to add, can be null
     */
    public void addRawValue(String value) {
        rawValues.add(value);
    }

    /**
     * @return List of values for this field, will never be null
     */
    public List<String> getRawValues() {
        return new ArrayList<String>(rawValues);
    }

    /**
     * Template for creating a form field.
     * 
     * @param <T> Type of form field.
     */
    public static class GenericFormField<T> extends FormField {
        private T value;

        /**
         * @return Values for this field. The values can be the defaults
         *         suggested in a form of {@link Type#FORM_TYPE} or results
         *         provided in a form of {@link Type#RESULT_TYPE} or values
         *         submitted in a form of {@link Type#SUBMIT_TYPE}. Will never be
         *         null.
         */
        public T getValue() {
            return value;
        }

        /**
         * Set values for this field. The values can be the defaults suggested
         * in a form of {@link Type#FORM_TYPE} or results provided in a form of
         * {@link Type#RESULT_TYPE} or values submitted in a form of
         * {@link Type#SUBMIT_TYPE}.
         * 
         * @param value Value to set, must not be null
         */
        public void setValue(T value) {
            if (value == null) {
                throw new NullPointerException("'value' must not be null");
            }

            this.value = value;
        }

        protected GenericFormField(T value) {
            setValue(value);
        }
    }

    /**
     * This field enables an entity to gather or provide an either-or choice
     * between two options. The default value is "false".
     */
    public static class BooleanFormField extends GenericFormField<Boolean> {
        private BooleanFormField(Boolean value) {
            super((value == null) ? Boolean.FALSE : value);
        }

        /**
         * Create an object with given value.
         * 
         * @param value Value for this field, can be null which will be assumed
         *            as FALSE.
         * 
         * @return new object, will never be null
         */
        public static BooleanFormField create(Boolean value) {
            return new BooleanFormField(value);
        }

        /**
         * Create an object with value FALSE.
         * 
         * @return new object, will never be null
         */
        public static BooleanFormField create() {
            return create(null);
        }
    }

    /**
     * This field is intended for data description (e.g., human-readable text
     * such as "section" headers) rather than data gathering or provision.
     */
    public static class FixedFormField extends GenericFormField<String> {
        private FixedFormField(String value) {
            super((value != null) ? value : "");
        }

        /**
         * Create an object with given value.
         * 
         * @param value Value for this field, can be null which will be stored
         *            as empty string.
         * 
         * @return new object, will never be null
         */
        public static FixedFormField create(String value) {
            return new FixedFormField(value);
        }

        /**
         * Create an object with value as an empty string.
         * 
         * @return new object, will never be null
         */
        public static FixedFormField create() {
            return create(null);
        }
    }

    /**
     * This field is not shown to the form-submitting entity, but instead is
     * returned with the form.
     */
    public static class HiddenFormField extends GenericFormField<String> {
        private HiddenFormField(String value) {
            super((value != null) ? value : "");
        }

        /**
         * Create an object with given value.
         * 
         * @param value Value for this field, can be null which will be stored
         *            as empty string.
         * 
         * @return new object, will never be null
         */
        public static HiddenFormField create(String value) {
            return new HiddenFormField(value);
        }

        /**
         * Create an object with value as an empty string.
         * 
         * @return new object, will never be null
         */
        public static HiddenFormField create() {
            return create(null);
        }
    }

    /**
     * This field enables an entity to gather or provide one option from among
     * many.
     */
    public static class ListSingleFormField extends GenericFormField<String> {
        private ListSingleFormField(String value) {
            super((value != null) ? value : "");
        }

        /**
         * Create an object with given value.
         * 
         * @param value Value for this field, can be null which will be stored
         *            as empty string.
         * 
         * @return new object, will never be null
         */
        public static ListSingleFormField create(String value) {
            return new ListSingleFormField(value);
        }

        /**
         * Create an object with value as an empty string.
         * 
         * @return new object, will never be null
         */
        public static ListSingleFormField create() {
            return create(null);
        }
    }

    /**
     * This field enables an entity to gather or provide multiple lines of text
     * (i.e. containing newlines).
     */
    public static class TextMultiFormField extends GenericFormField<String> {
        private TextMultiFormField(String value) {
            super((value != null) ? value : "");
        }

        /**
         * Create an object with given value.
         * 
         * @param value Value for this field, can be null which will be stored
         *            as empty string.
         * 
         * @return new object, will never be null
         */
        public static TextMultiFormField create(String value) {
            return new TextMultiFormField(value);
        }

        /**
         * Create an object with value as an empty string.
         * 
         * @return new object, will never be null
         */
        public static TextMultiFormField create() {
            return create(null);
        }
    }

    /**
     * This field enables an entity to gather or provide a single line or word
     * of text, which shall be obscured in an interface (e.g., with multiple
     * instances of the asterisk character).
     */
    public static class TextPrivateFormField extends GenericFormField<String> {
        private TextPrivateFormField(String value) {
            super((value != null) ? value : "");
        }

        /**
         * Create an object with given value.
         * 
         * @param value Value for this field, can be null which will be stored
         *            as empty string.
         * 
         * @return new object, will never be null
         */
        public static TextPrivateFormField create(String value) {
            return new TextPrivateFormField(value);
        }

        /**
         * Create an object with value as an empty string.
         * 
         * @return new object, will never be null
         */
        public static TextPrivateFormField create() {
            return create(null);
        }
    }

    /**
     * This field enables an entity to gather or provide a single line or word
     * of text, which may be shown in an interface.
     */
    public static class TextSingleFormField extends GenericFormField<String> {
        private TextSingleFormField(String value) {
            super((value != null) ? value : "");
        }

        /**
         * Create an object with given value.
         * 
         * @param value Value for this field, can be null which will be stored
         *            as empty string.
         * 
         * @return new object, will never be null
         */
        public static TextSingleFormField create(String value) {
            return new TextSingleFormField(value);
        }

        /**
         * Create an object with value as an empty string.
         * 
         * @return new object, will never be null
         */
        public static TextSingleFormField create() {
            return create(null);
        }
    }

    /**
     * This field enables an entity to gather or provide a single Jabber ID.
     */
    public static class JIDSingleFormField extends GenericFormField<JID> {
        private JIDSingleFormField(JID value) {
            super((value != null) ? value : new JID());
        }

        /**
         * Create an object with given value.
         * 
         * @param value Value for this field, can be null which will be stored
         *            as an invalid JID.
         * 
         * @return new object, will never be null
         */
        public static JIDSingleFormField create(JID value) {
            return new JIDSingleFormField(value);
        }

        /**
         * Create an object with value as an invalid JID.
         * 
         * @return new object, will never be null
         */
        public static JIDSingleFormField create() {
            return create(null);
        }
    }

    /**
     * This field enables an entity to gather or provide multiple Jabber IDs.
     */
    public static class JIDMultiFormField extends GenericFormField<List<JID>> {
        private JIDMultiFormField(List<JID> value) {
            super((value == null) ? new ArrayList<JID>() : new ArrayList<JID>(
                    value));
        }

        /**
         * Create an object with given value.
         * 
         * @param value Value for this field, can be null which will be assumed
         *            to be an empty list. A copy of the given list will be kept
         *            by this object.
         * 
         * @return new object, will never be null
         */
        public static JIDMultiFormField create(List<JID> value) {
            return new JIDMultiFormField(value);
        }

        /**
         * Create an object with an empty list.
         * 
         * @return new object, will never be null
         */
        public static JIDMultiFormField create() {
            return create(null);
        }
    }

    /**
     * This field enables an entity to gather or provide one or more options
     * from among many. The order of items MAY be significant.
     */
    public static class ListMultiFormField extends
    GenericFormField<List<String>> {
        private ListMultiFormField(List<String> value) {
            super((value == null) ? new ArrayList<String>()
                    : new ArrayList<String>(value));
        }

        /**
         * Create an object with given value.
         * 
         * @param value Value for this field, can be null which will be assumed
         *            to be an empty list. A copy of the given list will be kept
         *            by this object.
         * 
         * @return new object, will never be null
         */
        public static ListMultiFormField create(List<String> value) {
            return new ListMultiFormField(value);
        }

        /**
         * Create an object with an empty list.
         * 
         * @return new object, will never be null
         */
        public static ListMultiFormField create() {
            return create(null);
        }
    }

    @Override
    public String toString() {
        return FormField.class.getSimpleName() + "\nname: " + name
                + "\nlabel: " + label + "\ndescription: " + description
                + "\nrequired: " + required;
    }
}
