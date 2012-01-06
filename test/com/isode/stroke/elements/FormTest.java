/*
 * Copyright (c) 2012 Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2010 Remko Tronçon
 * All rights reserved.
 */

package com.isode.stroke.elements;

import static org.junit.Assert.assertEquals;

import org.junit.BeforeClass;
import org.junit.Test;

import com.isode.stroke.elements.FormField.FixedFormField;
import com.isode.stroke.elements.FormField.HiddenFormField;

public class FormTest {
    @BeforeClass
    public static void init() throws Exception {
    }

    @Test
    public void testGetFormType() {
        Form form = new Form();

        form.addField(FixedFormField.create("Foo"));

        FormField field = HiddenFormField.create("jabber:bot");
        field.setName("FORM_TYPE");
        form.addField(field);

        form.addField(FixedFormField.create("Bar"));

        assertEquals("jabber:bot", form.getFormType());
    }

    @Test
    public void testGetFormType_InvalidFormType() {
        Form form = new Form();

        FormField field = FixedFormField.create("jabber:bot");
        field.setName("FORM_TYPE");
        form.addField(field);

        assertEquals("", form.getFormType());
    }

    @Test
    public void testGetFormType_NoFormType() {
        Form form = new Form();

        form.addField(FixedFormField.create("Foo"));

        assertEquals("", form.getFormType());
    }
}
