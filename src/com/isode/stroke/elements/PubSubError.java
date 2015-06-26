/*
 * Copyright (c) 2014, Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2014, Remko Tronçon.
 * All rights reserved.
 */

package com.isode.stroke.elements;

public class PubSubError extends Payload {
    public enum Type {
        UnknownType,
        ClosedNode,
        ConfigurationRequired,
        InvalidJID,
        InvalidOptions,
        InvalidPayload,
        InvalidSubscriptionID,
        ItemForbidden,
        ItemRequired,
        JIDRequired,
        MaximumItemsExceeded,
        MaximumNodesExceeded,
        NodeIDRequired,
        NotInRosterGroup,
        NotSubscribed,
        PayloadTooBig,
        PayloadRequired,
        PendingSubscription,
        PresenceSubscriptionRequired,
        SubscriptionIDRequired,
        TooManySubscriptions,
        Unsupported,
        UnsupportedAccessModel
    };
    
    public enum UnsupportedFeatureType {
        UnknownUnsupportedFeatureType,
        AccessAuthorize,
        AccessOpen,
        AccessPresence,
        AccessRoster,
        AccessWhitelist,
        AutoCreate,
        AutoSubscribe,
        Collections,
        ConfigNode,
        CreateAndConfigure,
        CreateNodes,
        DeleteItems,
        DeleteNodes,
        FilteredNotifications,
        GetPending,
        InstantNodes,
        ItemIDs,
        LastPublished,
        LeasedSubscription,
        ManageSubscriptions,
        MemberAffiliation,
        MetaData,
        ModifyAffiliations,
        MultiCollection,
        MultiSubscribe,
        OutcastAffiliation,
        PersistentItems,
        PresenceNotifications,
        PresenceSubscribe,
        Publish,
        PublishOptions,
        PublishOnlyAffiliation,
        PublisherAffiliation,
        PurgeNodes,
        RetractItems,
        RetrieveAffiliations,
        RetrieveDefault,
        RetrieveItems,
        RetrieveSubscriptions,
        Subscribe,
        SubscriptionOptions,
        SubscriptionNotifications
    };
    
    public PubSubError(Type type) {
        type_ = Type.UnknownType;
        unsupportedType_ = UnsupportedFeatureType.UnknownUnsupportedFeatureType;
    }
    
    public Type getType() {
        return type_;
    }
    
    public void setType(Type type) {
        type_ = type;
    }
    
    public UnsupportedFeatureType getUnsupportedFeatureType() {
        return unsupportedType_;
    }
    
    public void setUnsupportedFeatureType(UnsupportedFeatureType unsupportedType) {
        unsupportedType_ = unsupportedType;
    }
    
    private Type type_;
    private UnsupportedFeatureType unsupportedType_;
}
