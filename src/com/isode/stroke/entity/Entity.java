/*
 * Copyright (c) 2010 Isode Limited.
 * All rights reserved.
 * See the COPYING file for more information.
 */
/*
 * Copyright (c) 2015 Tarun Gupta.
 * Licensed under the simplified BSD license.
 * See Documentation/Licenses/BSD-simplified.txt for more information.
 */

package com.isode.stroke.entity;

import com.isode.stroke.parser.PayloadParserFactory;
import com.isode.stroke.serializer.PayloadSerializer;
import com.isode.stroke.parser.payloadparsers.FullPayloadParserFactoryCollection;
import com.isode.stroke.serializer.payloadserializers.FullPayloadSerializerCollection;
import com.isode.stroke.parser.PayloadParserFactoryCollection;
import com.isode.stroke.serializer.PayloadSerializerCollection;

/** 
 * The base class for XMPP entities (Clients, Components).
 */
public class Entity {

	private FullPayloadParserFactoryCollection payloadParserFactories;
	private FullPayloadSerializerCollection payloadSerializers;

	public Entity() {
		payloadParserFactories = new FullPayloadParserFactoryCollection();
		payloadSerializers = new FullPayloadSerializerCollection();
	}

	public void addPayloadParserFactory(PayloadParserFactory payloadParserFactory) {
		payloadParserFactories.addFactory(payloadParserFactory);
	}

	public void removePayloadParserFactory(PayloadParserFactory payloadParserFactory) {
		payloadParserFactories.removeFactory(payloadParserFactory);
	}

	public void addPayloadSerializer(PayloadSerializer payloadSerializer) {
		payloadSerializers.addSerializer(payloadSerializer);
	}

	public void removePayloadSerializer(PayloadSerializer payloadSerializer) {
		payloadSerializers.removeSerializer(payloadSerializer);
	}

	protected PayloadParserFactoryCollection getPayloadParserFactories() {
		return payloadParserFactories;
	}

	protected PayloadSerializerCollection getPayloadSerializers() {
		return payloadSerializers;
	}
}