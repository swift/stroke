/*
* Copyright (c) 2014, Isode Limited, London, England.
* All rights reserved.
*/
/*
* Copyright (c) 2014, Remko Tronçon.
* All rights reserved.
*/

package com.isode.stroke.parser.payloadparsers;

import java.util.HashMap;

import com.isode.stroke.elements.PubSubError;
import com.isode.stroke.parser.AttributeMap;
import com.isode.stroke.parser.GenericPayloadParser;

public class PubSubErrorParser extends GenericPayloadParser<PubSubError> {

public PubSubErrorParser()
{
	super(new PubSubError(PubSubError.Type.UnknownType));

	typeParser_.put("closed-node", PubSubError.Type.ClosedNode);
	typeParser_.put("configuration-required", PubSubError.Type.ConfigurationRequired);
	typeParser_.put("invalid-jid", PubSubError.Type.InvalidJID);
	typeParser_.put("invalid-options", PubSubError.Type.InvalidOptions);
	typeParser_.put("invalid-payload", PubSubError.Type.InvalidPayload);
	typeParser_.put("invalid-subid", PubSubError.Type.InvalidSubscriptionID);
	typeParser_.put("item-forbidden", PubSubError.Type.ItemForbidden);
	typeParser_.put("item-required", PubSubError.Type.ItemRequired);
	typeParser_.put("jid-required", PubSubError.Type.JIDRequired);
	typeParser_.put("max-items-exceeded", PubSubError.Type.MaximumItemsExceeded);
	typeParser_.put("max-nodes-exceeded", PubSubError.Type.MaximumNodesExceeded);
	typeParser_.put("nodeid-required", PubSubError.Type.NodeIDRequired);
	typeParser_.put("not-in-roster-group", PubSubError.Type.NotInRosterGroup);
	typeParser_.put("not-subscribed", PubSubError.Type.NotSubscribed);
	typeParser_.put("payload-too-big", PubSubError.Type.PayloadTooBig);
	typeParser_.put("payload-required", PubSubError.Type.PayloadRequired);
	typeParser_.put("pending-subscription", PubSubError.Type.PendingSubscription);
	typeParser_.put("presence-subscription-required", PubSubError.Type.PresenceSubscriptionRequired);
	typeParser_.put("subid-required", PubSubError.Type.SubscriptionIDRequired);
	typeParser_.put("too-many-subscriptions", PubSubError.Type.TooManySubscriptions);
	typeParser_.put("unsupported", PubSubError.Type.Unsupported);
	typeParser_.put("unsupported-access-model", PubSubError.Type.UnsupportedAccessModel);

	unsupportedTypeParser_.put("access-authorize", PubSubError.UnsupportedFeatureType.AccessAuthorize);
	unsupportedTypeParser_.put("access-open", PubSubError.UnsupportedFeatureType.AccessOpen);
	unsupportedTypeParser_.put("access-presence", PubSubError.UnsupportedFeatureType.AccessPresence);
	unsupportedTypeParser_.put("access-roster", PubSubError.UnsupportedFeatureType.AccessRoster);
	unsupportedTypeParser_.put("access-whitelist", PubSubError.UnsupportedFeatureType.AccessWhitelist);
	unsupportedTypeParser_.put("auto-create", PubSubError.UnsupportedFeatureType.AutoCreate);
	unsupportedTypeParser_.put("auto-subscribe", PubSubError.UnsupportedFeatureType.AutoSubscribe);
	unsupportedTypeParser_.put("collections", PubSubError.UnsupportedFeatureType.Collections);
	unsupportedTypeParser_.put("config-node", PubSubError.UnsupportedFeatureType.ConfigNode);
	unsupportedTypeParser_.put("create-and-configure", PubSubError.UnsupportedFeatureType.CreateAndConfigure);
	unsupportedTypeParser_.put("create-nodes", PubSubError.UnsupportedFeatureType.CreateNodes);
	unsupportedTypeParser_.put("delete-items", PubSubError.UnsupportedFeatureType.DeleteItems);
	unsupportedTypeParser_.put("delete-nodes", PubSubError.UnsupportedFeatureType.DeleteNodes);
	unsupportedTypeParser_.put("filtered-notifications", PubSubError.UnsupportedFeatureType.FilteredNotifications);
	unsupportedTypeParser_.put("get-pending", PubSubError.UnsupportedFeatureType.GetPending);
	unsupportedTypeParser_.put("instant-nodes", PubSubError.UnsupportedFeatureType.InstantNodes);
	unsupportedTypeParser_.put("item-ids", PubSubError.UnsupportedFeatureType.ItemIDs);
	unsupportedTypeParser_.put("last-published", PubSubError.UnsupportedFeatureType.LastPublished);
	unsupportedTypeParser_.put("leased-subscription", PubSubError.UnsupportedFeatureType.LeasedSubscription);
	unsupportedTypeParser_.put("manage-subscriptions", PubSubError.UnsupportedFeatureType.ManageSubscriptions);
	unsupportedTypeParser_.put("member-affiliation", PubSubError.UnsupportedFeatureType.MemberAffiliation);
	unsupportedTypeParser_.put("meta-data", PubSubError.UnsupportedFeatureType.MetaData);
	unsupportedTypeParser_.put("modify-affiliations", PubSubError.UnsupportedFeatureType.ModifyAffiliations);
	unsupportedTypeParser_.put("multi-collection", PubSubError.UnsupportedFeatureType.MultiCollection);
	unsupportedTypeParser_.put("multi-subscribe", PubSubError.UnsupportedFeatureType.MultiSubscribe);
	unsupportedTypeParser_.put("outcast-affiliation", PubSubError.UnsupportedFeatureType.OutcastAffiliation);
	unsupportedTypeParser_.put("persistent-items", PubSubError.UnsupportedFeatureType.PersistentItems);
	unsupportedTypeParser_.put("presence-notifications", PubSubError.UnsupportedFeatureType.PresenceNotifications);
	unsupportedTypeParser_.put("presence-subscribe", PubSubError.UnsupportedFeatureType.PresenceSubscribe);
	unsupportedTypeParser_.put("publish", PubSubError.UnsupportedFeatureType.Publish);
	unsupportedTypeParser_.put("publish-options", PubSubError.UnsupportedFeatureType.PublishOptions);
	unsupportedTypeParser_.put("publish-only-affiliation", PubSubError.UnsupportedFeatureType.PublishOnlyAffiliation);
	unsupportedTypeParser_.put("publisher-affiliation", PubSubError.UnsupportedFeatureType.PublisherAffiliation);
	unsupportedTypeParser_.put("purge-nodes", PubSubError.UnsupportedFeatureType.PurgeNodes);
	unsupportedTypeParser_.put("retract-items", PubSubError.UnsupportedFeatureType.RetractItems);
	unsupportedTypeParser_.put("retrieve-affiliations", PubSubError.UnsupportedFeatureType.RetrieveAffiliations);
	unsupportedTypeParser_.put("retrieve-default", PubSubError.UnsupportedFeatureType.RetrieveDefault);
	unsupportedTypeParser_.put("retrieve-items", PubSubError.UnsupportedFeatureType.RetrieveItems);
	unsupportedTypeParser_.put("retrieve-subscriptions", PubSubError.UnsupportedFeatureType.RetrieveSubscriptions);
	unsupportedTypeParser_.put("subscribe", PubSubError.UnsupportedFeatureType.Subscribe);
	unsupportedTypeParser_.put("subscription-options", PubSubError.UnsupportedFeatureType.SubscriptionOptions);
	unsupportedTypeParser_.put("subscription-notifications", PubSubError.UnsupportedFeatureType.SubscriptionNotifications);
}

@Override
public void handleStartElement(String element, String ns, AttributeMap attributes)
{
	if (level_ == 1) {
		PubSubError.Type type = typeParser_.get(element);
		if (type != null) {
			getPayloadInternal().setType(type);
			if (type.equals(PubSubError.Type.Unsupported)) {
				String feature = attributes.getAttributeValue("feature");
				if (feature != null) {
					PubSubError.UnsupportedFeatureType unsupportedType = unsupportedTypeParser_.get(feature);
					if (unsupportedType != null) {
						getPayloadInternal().setUnsupportedFeatureType(unsupportedType);
					}
				}
			}
		}
	}
	++level_;
}

@Override
public void handleEndElement(String element, String ns)
{
	--level_;
}

@Override
public void handleCharacterData(String data)
{
}

int level_;
HashMap<String, PubSubError.Type> typeParser_ = new HashMap<String, PubSubError.Type>();
HashMap<String, PubSubError.UnsupportedFeatureType> unsupportedTypeParser_ = new HashMap<String, PubSubError.UnsupportedFeatureType>();
}
