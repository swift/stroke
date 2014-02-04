/*
* Copyright (c) 2014, Isode Limited, London, England.
* All rights reserved.
*/
/*
* Copyright (c) 2014, Remko Tronçon.
* All rights reserved.
*/

package com.isode.stroke.pubsub;

import com.isode.stroke.client.StanzaChannel;
import com.isode.stroke.elements.Message;
import com.isode.stroke.parser.payloadparsers.PubSubEvent;
import com.isode.stroke.queries.IQRouter;
import com.isode.stroke.signals.Slot1;

public class PubSubManagerImpl extends PubSubManager {
    
    public PubSubManagerImpl(StanzaChannel stanzaChannel, IQRouter router) {
        stanzaChannel_ = stanzaChannel;
        router_ = router;
        
        stanzaChannel.onMessageReceived.connect(new Slot1<Message>() {
            public void call(Message message) {
                PubSubEvent event = (PubSubEvent)message.getPayload(new PubSubEvent());
                if (event != null) {
                    onEvent.emit(message.getFrom(), event.getPayload());
                }
            }
        });
    }
    
    StanzaChannel stanzaChannel_;
    IQRouter router_;
}
