package com.isode.stroke.serializer.payloadserializers;

import com.isode.stroke.elements.Subject;
import com.isode.stroke.serializer.GenericPayloadSerializer;
import com.isode.stroke.serializer.xml.XMLTextNode;

/**
 * Subject to String
 */
public class SubjectSerializer extends GenericPayloadSerializer<Subject> {

	public SubjectSerializer() {
		super(Subject.class);
	}

	@Override
	protected String serializePayload(Subject payload) {
		XMLTextNode textNode = new XMLTextNode(payload.getText());
		return "<subject>"+textNode.serialize()+"</subject>";
	}

}
