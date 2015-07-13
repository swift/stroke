/*
 * Copyright (c) 2010 Remko Tronçon
 * Licensed under the GNU General Public License v3.
 * See Documentation/Licenses/GPLv3.txt for more information.
 */
/*
 * Copyright (c) 2011-2015, Isode Limited, London, England.
 * All rights reserved.
 */
package com.isode.stroke.idn;

import java.net.IDN;

public class IDNA {
    public static String getEncoded(String s) {
        try {
            return IDN.toASCII(s, IDN.USE_STD3_ASCII_RULES);
        }
        catch (IllegalArgumentException e) {
            return null;
        }
        catch (StringIndexOutOfBoundsException e) {
            // In java 7 IDN.toASCII sometimes throws StringIndexOutOfBoundException
            // (instead of IllegalArgumentException)
            return null;
        }
    }
}
