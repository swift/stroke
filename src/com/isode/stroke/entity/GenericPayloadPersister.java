/*
 * Copyright (c) 2015, Isode Limited.
 * All rights reserved.
 */
package com.isode.stroke.entity;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import com.isode.stroke.elements.Payload;
import com.isode.stroke.serializer.PayloadSerializer;

public class GenericPayloadPersister<PayloadType extends Payload, Serializer extends PayloadSerializer> {
	
	private final Serializer serializer;
	
	public GenericPayloadPersister(Serializer serializer) {
		this.serializer = serializer;
	}
	
	public void savePayload(PayloadType payload, OutputStream os) throws IOException {
		String s = serializer.serialize(payload);
		OutputStreamWriter ow = new OutputStreamWriter(os, "UTF-8");
		ow.append(s);
		ow.close();
	}
}
