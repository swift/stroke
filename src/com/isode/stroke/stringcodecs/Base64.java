/*
 * Copyright (c) 2010 Remko Tronçon
 * Licensed under the GNU General Public License v3.
 * See Documentation/Licenses/GPLv3.txt for more information.
 */
/*
 * Copyright (c) 2010, Isode Limited, London, England.
 * All rights reserved.
 */
package com.isode.stroke.stringcodecs;

import com.isode.stroke.base.ByteArray;

public class Base64 {
    /* FIXME: Check license is ok (it is, it's BSD) */
    public static ByteArray decode(String input) {
        return new ByteArray(Base64BSD.decode(input));
    }

    public static String encode(ByteArray input) {
        return Base64BSD.encodeToString(input.getData(), false);
    }
}
