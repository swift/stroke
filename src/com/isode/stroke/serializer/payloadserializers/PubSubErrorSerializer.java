/*
* Copyright (c) 2014, Isode Limited, London, England.
* All rights reserved.
*/
/*
* Copyright (c) 2014, Remko Tronçon.
* All rights reserved.
*/

package com.isode.stroke.serializer.payloadserializers;

import com.isode.stroke.elements.PubSubError;
import com.isode.stroke.serializer.GenericPayloadSerializer;
import com.isode.stroke.serializer.xml.XMLElement;

public class PubSubErrorSerializer extends GenericPayloadSerializer<PubSubError> {
public PubSubErrorSerializer() {
	super(PubSubError.class);
}

@Override
public String serializePayload(PubSubError payload) {
	if (payload.getType().equals(PubSubError.Type.UnknownType)) {
		return "";
	}
	XMLElement element = new XMLElement(serializeType(payload.getType()), "http://jabber.org/protocol/pubsub#errors");
	if (payload.getType().equals(PubSubError.Type.Unsupported)) {
		if (!payload.getUnsupportedFeatureType().equals(PubSubError.UnsupportedFeatureType.UnknownUnsupportedFeatureType)) {
			element.setAttribute("feature", serializeUnsupportedFeatureType(payload.getUnsupportedFeatureType()));
		}
	}
	return element.serialize();
}

static String serializeType(PubSubError.Type value) {
	switch (value) {
		case UnknownType: return "";
		case ClosedNode: return "closed-node";
		case ConfigurationRequired: return "configuration-required";
		case InvalidJID: return "invalid-jid";
		case InvalidOptions: return "invalid-options";
		case InvalidPayload: return "invalid-payload";
		case InvalidSubscriptionID: return "invalid-subid";
		case ItemForbidden: return "item-forbidden";
		case ItemRequired: return "item-required";
		case JIDRequired: return "jid-required";
		case MaximumItemsExceeded: return "max-items-exceeded";
		case MaximumNodesExceeded: return "max-nodes-exceeded";
		case NodeIDRequired: return "nodeid-required";
		case NotInRosterGroup: return "not-in-roster-group";
		case NotSubscribed: return "not-subscribed";
		case PayloadTooBig: return "payload-too-big";
		case PayloadRequired: return "payload-required";
		case PendingSubscription: return "pending-subscription";
		case PresenceSubscriptionRequired: return "presence-subscription-required";
		case SubscriptionIDRequired: return "subid-required";
		case TooManySubscriptions: return "too-many-subscriptions";
		case Unsupported: return "unsupported";
		case UnsupportedAccessModel: return "unsupported-access-model";
		default: return null;
	}
}

static String serializeUnsupportedFeatureType(PubSubError.UnsupportedFeatureType value) {
	switch (value) {
		case UnknownUnsupportedFeatureType: assert(false); return "";
		case AccessAuthorize: return "access-authorize";
		case AccessOpen: return "access-open";
		case AccessPresence: return "access-presence";
		case AccessRoster: return "access-roster";
		case AccessWhitelist: return "access-whitelist";
		case AutoCreate: return "auto-create";
		case AutoSubscribe: return "auto-subscribe";
		case Collections: return "collections";
		case ConfigNode: return "config-node";
		case CreateAndConfigure: return "create-and-configure";
		case CreateNodes: return "create-nodes";
		case DeleteItems: return "delete-items";
		case DeleteNodes: return "delete-nodes";
		case FilteredNotifications: return "filtered-notifications";
		case GetPending: return "get-pending";
		case InstantNodes: return "instant-nodes";
		case ItemIDs: return "item-ids";
		case LastPublished: return "last-published";
		case LeasedSubscription: return "leased-subscription";
		case ManageSubscriptions: return "manage-subscriptions";
		case MemberAffiliation: return "member-affiliation";
		case MetaData: return "meta-data";
		case ModifyAffiliations: return "modify-affiliations";
		case MultiCollection: return "multi-collection";
		case MultiSubscribe: return "multi-subscribe";
		case OutcastAffiliation: return "outcast-affiliation";
		case PersistentItems: return "persistent-items";
		case PresenceNotifications: return "presence-notifications";
		case PresenceSubscribe: return "presence-subscribe";
		case Publish: return "publish";
		case PublishOptions: return "publish-options";
		case PublishOnlyAffiliation: return "publish-only-affiliation";
		case PublisherAffiliation: return "publisher-affiliation";
		case PurgeNodes: return "purge-nodes";
		case RetractItems: return "retract-items";
		case RetrieveAffiliations: return "retrieve-affiliations";
		case RetrieveDefault: return "retrieve-default";
		case RetrieveItems: return "retrieve-items";
		case RetrieveSubscriptions: return "retrieve-subscriptions";
		case Subscribe: return "subscribe";
		case SubscriptionOptions: return "subscription-options";
		case SubscriptionNotifications: return "subscription-notifications";
		default: return null;
	}
}
}
