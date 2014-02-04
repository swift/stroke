/*
* Copyright (c) 2014, Isode Limited, London, England.
* All rights reserved.
*/
/*
* Copyright (c) 2014, Remko Tronçon.
* All rights reserved.
*/

package com.isode.stroke.pubsub;

import com.isode.stroke.elements.PubSubEventPayload;
import com.isode.stroke.jid.JID;
import com.isode.stroke.signals.Signal2;

public class PubSubManager {
public Signal2<JID, PubSubEventPayload> onEvent = new Signal2<JID, PubSubEventPayload>();
}
