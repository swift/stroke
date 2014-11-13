/*
 * Copyright (c) 2013-2015, Isode Limited, London, England.
 * All rights reserved.
 */
package com.isode.stroke.crypto;

import com.isode.stroke.base.ByteArray;
import com.isode.stroke.base.SafeByteArray;

public interface Hash {
     Hash update(final ByteArray data);
     Hash update(final SafeByteArray data);

     ByteArray getHash();
}
