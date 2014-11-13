/*
 * Copyright (c) 2010-2015, Isode Limited, London, England.
 * All rights reserved.
 */
package com.isode.stroke.client;

import com.isode.stroke.signals.Signal1;

public abstract class NickManager {
	public abstract String getOwnNick();
	public abstract void setOwnNick(final String nick);

	public final Signal1<String> onOwnNickChanged = new Signal1<String>();
}
