/*
 * Copyright (c) 2010, Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2010, Remko Tronçon.
 * All rights reserved.
 */

package com.isode.stroke.serializer.payloadserializers;

import com.isode.stroke.elements.ErrorPayload;
import com.isode.stroke.serializer.GenericPayloadSerializer;
import com.isode.stroke.serializer.xml.XMLTextNode;
import com.isode.stroke.serializer.PayloadSerializerCollection;
import com.isode.stroke.serializer.PayloadSerializer;

public class ErrorSerializer extends GenericPayloadSerializer<ErrorPayload> {

	private PayloadSerializerCollection serializers;

    public ErrorSerializer(PayloadSerializerCollection serializers) {
        super(ErrorPayload.class);
        this.serializers = serializers;
    }

    @Override
    public String serializePayload(ErrorPayload error) {
        String result = "<error type=\"";
	switch (error.getType()) {
		case Continue: result += "continue"; break;
		case Modify: result += "modify"; break;
		case Auth: result += "auth"; break;
		case Wait: result += "wait"; break;
		default: result += "cancel"; break;
	}
	result += "\">";

	String conditionElement;
	switch (error.getCondition()) {
		case BadRequest: conditionElement = "bad-request"; break;
		case Conflict: conditionElement = "conflict"; break;
		case FeatureNotImplemented: conditionElement = "feature-not-implemented"; break;
		case Forbidden: conditionElement = "forbidden"; break;
		case Gone: conditionElement = "gone"; break;
		case InternalServerError: conditionElement = "internal-server-error"; break;
		case ItemNotFound: conditionElement = "item-not-found"; break;
		case JIDMalformed: conditionElement = "jid-malformed"; break;
		case NotAcceptable: conditionElement = "not-acceptable"; break;
		case NotAllowed: conditionElement = "not-allowed"; break;
		case NotAuthorized: conditionElement = "not-authorized"; break;
		case PaymentRequired: conditionElement = "payment-required"; break;
		case RecipientUnavailable: conditionElement = "recipient-unavailable"; break;
		case Redirect: conditionElement = "redirect"; break;
		case RegistrationRequired: conditionElement = "registration-required"; break;
		case RemoteServerNotFound: conditionElement = "remote-server-not-found"; break;
		case RemoteServerTimeout: conditionElement = "remote-server-timeout"; break;
		case ResourceConstraint: conditionElement = "resource-constraint"; break;
		case ServiceUnavailable: conditionElement = "service-unavailable"; break;
		case SubscriptionRequired: conditionElement = "subscription-required"; break;
		case UnexpectedRequest: conditionElement = "unexpected-request"; break;
		default: conditionElement = "undefined-condition"; break;
	}
	result += "<" + conditionElement + " xmlns=\"urn:ietf:params:xml:ns:xmpp-stanzas\"/>";

	if (error.getText().length() != 0) {
		XMLTextNode textNode = new XMLTextNode(error.getText());
		result += "<text xmlns=\"urn:ietf:params:xml:ns:xmpp-stanzas\">" + textNode.serialize() + "</text>";
	}

	if (error.getPayload() != null) {
		PayloadSerializer serializer = serializers.getPayloadSerializer(error.getPayload());
		if (serializer != null) {
			result += serializer.serialize(error.getPayload());
		}
	}

	result += "</error>";
	return result;
    }

}
