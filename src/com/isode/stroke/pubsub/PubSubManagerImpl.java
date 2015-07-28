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
import com.isode.stroke.signals.SignalConnection;

public class PubSubManagerImpl extends PubSubManager {
    
    public PubSubManagerImpl(StanzaChannel stanzaChannel, IQRouter router) {
        stanzaChannel_ = stanzaChannel;
        router_ = router;
        
        onMessageReceivedConnection = stanzaChannel.onMessageReceived.connect(new Slot1<Message>() {
            public void call(Message message) {
                handleMessageRecevied(message);
            }
        });
    }

    protected void finalize() throws Throwable {
        try {
            onMessageReceivedConnection.disconnect();
        }
        finally {
            super.finalize();
        }
    }

    private void handleMessageRecevied(Message message) {
        if (message.getPayload(new PubSubEvent()) != null) {
            PubSubEvent event = (PubSubEvent)message.getPayload(new PubSubEvent());
            onEvent.emit(message.getFrom(), event.getPayload());
        }
    }

    private SignalConnection onMessageReceivedConnection;
    private StanzaChannel stanzaChannel_;
    private IQRouter router_;
}
