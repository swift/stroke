/*
 * Copyright (c) 2012-2014 Isode Limited, London, England.
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

import com.isode.stroke.elements.FormField.Type;

public class FormTest {
    @BeforeClass
    public static void init() throws Exception {
    }

    @Test
    public void testGetFormType() {
        Form form = new Form();

        form.addField(new FormField(Type.FIXED_TYPE, "Foo"));

        FormField field = new FormField(Type.HIDDEN_TYPE, "jabber:bot");
        field.setName("FORM_TYPE");
        form.addField(field);

        form.addField(new FormField(Type.FIXED_TYPE, "Bar"));

        assertEquals("jabber:bot", form.getFormType());
    }

    @Test
    public void testGetFormType_InvalidFormType() {
        Form form = new Form();

        FormField field = new FormField(Type.FIXED_TYPE, "jabber:bot");
        field.setName("FORM_TYPE");
        form.addField(field);

        assertEquals("", form.getFormType());
    }

    @Test
    public void testGetFormType_NoFormType() {
        Form form = new Form();

        form.addField(new FormField(Type.FIXED_TYPE, "Foo"));

        assertEquals("", form.getFormType());
    }
}
