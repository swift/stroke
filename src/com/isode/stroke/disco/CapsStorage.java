/*
 * Copyright (c) 2010-2015, Isode Limited, London, England.
 * All rights reserved.
 */
package com.isode.stroke.disco;

import com.isode.stroke.elements.DiscoInfo;

public interface CapsStorage {
    DiscoInfo getDiscoInfo(final String s);
    void setDiscoInfo(final String s, DiscoInfo d);
}
