/*
 * Copyright (c) 2010-2015, Isode Limited, London, England.
 * All rights reserved.
 */
package com.isode.stroke.disco;

import java.util.HashMap;
import java.util.Map;

import com.isode.stroke.elements.DiscoInfo;

public class CapsMemoryStorage implements CapsStorage {
    private Map<String, DiscoInfo> caps = new HashMap<String, DiscoInfo>();

    @Override
    public DiscoInfo getDiscoInfo(String s) {
        return caps.get(s);
    }

    @Override
    public void setDiscoInfo(String s, DiscoInfo d) {
        caps.put(s, d);

    }

}
