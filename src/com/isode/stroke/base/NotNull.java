/*
 * Copyright (c) 2012, Isode Limited, London, England.
 * All rights reserved.
 */

package com.isode.stroke.base;

/**
 * Helper to check for nulls.
 */
public class NotNull {

    /**
     * Throws a NullPointerException if null.
     * @param o Object to check for non-nullness
     */
    public static void exceptIfNull(Object o) {
        if (o == null) {
            throw new NullPointerException();
        }
    }

    /**
     * Throws a NullPointerException if null.
     * @param o Object to check for non-nullness
     * @param name Variable name to include in error.
     */
    public static void exceptIfNull(Object o, String name) {
        if (name == null) {
            throw new NullPointerException("Variable name passed to exceptIfNull must not be null");
        }
        if (o == null) {
            throw new NullPointerException(name + " must not be null");
        }
    }
}
