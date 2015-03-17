/*
 * Copyright (c) 2010-2012, Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2010, Remko Tronçon.
 * All rights reserved.
 */
/*
 * Copyright (c) 2015, Tarun Gupta.
 * All rights reserved.
 */

package com.isode.stroke.parser.payloadparsers;

import com.isode.stroke.parser.GenericPayloadParserFactory;
import com.isode.stroke.parser.GenericPayloadParserFactory2;
import com.isode.stroke.parser.PayloadParserFactory;
import com.isode.stroke.parser.PayloadParserFactoryCollection;
import com.isode.stroke.parser.PubSubOwnerPubSubParser;

public class FullPayloadParserFactoryCollection extends PayloadParserFactoryCollection {
    public FullPayloadParserFactoryCollection() {
        /* TODO: Port more */
        //addFactory(new GenericPayloadParserFactory<IBBParser>("", "http://jabber.org/protocol/ibb"));
	//addFactory(new GenericPayloadParserFactory<StatusShowParser>("show", StatusShowParser.class));
	//addFactory(new GenericPayloadParserFactory<StatusParser>("status", StatusParser.class));
	//addFactory(new GenericPayloadParserFactory<ReplaceParser>("replace", "http://swift.im/protocol/replace"));
	addFactory(new GenericPayloadParserFactory<LastParser>("query", "jabber:iq:last", LastParser.class));
	addFactory(new GenericPayloadParserFactory<BodyParser>("body", BodyParser.class));
	//addFactory(new GenericPayloadParserFactory<SubjectParser>("subject", SubjectParser.class));
	//addFactory(new GenericPayloadParserFactory<PriorityParser>("priority", PriorityParser.class));
	addFactory(new ErrorParserFactory(this));
	addFactory(new SoftwareVersionParserFactory());
	//addFactory(new StorageParserFactory());
	addFactory(new RosterParserFactory());
	addFactory(new GenericPayloadParserFactory<DiscoInfoParser>("query", "http://jabber.org/protocol/disco#info", DiscoInfoParser.class));
	addFactory(new GenericPayloadParserFactory<DiscoItemsParser>("query", "http://jabber.org/protocol/disco#items", DiscoItemsParser.class));
	addFactory(new GenericPayloadParserFactory<CapsInfoParser> ("c", "http://jabber.org/protocol/caps", CapsInfoParser.class));
	//addFactory(new CapsInfoParserFactory());
	addFactory(new ResourceBindParserFactory());
	addFactory(new StartSessionParserFactory());
	//addFactory(new SecurityLabelParserFactory());
	//addFactory(new SecurityLabelsCatalogParserFactory());
        addFactory(new FormParserFactory());
        addFactory(new GenericPayloadParserFactory<CommandParser>("command",
                "http://jabber.org/protocol/commands", CommandParser.class));
        //addFactery(new InBandRegistrationPayloadParserFactory());
        addFactory(new SearchPayloadParserFactory());
	//addFactory(new StreamInitiationParserFactory());
	//addFactory(new BytestreamsParserFactory());
	//addFactory(new VCardUpdateParserFactory());
	//addFactory(new VCardParserFactory());
	//addFactory(new PrivateStorageParserFactory(this));
	//addFactory(new ChatStateParserFactory());
	//addFactory(new DelayParserFactory());
	addFactory(new MUCUserPayloadParserFactory(this));
	addFactory(new MUCOwnerPayloadParserFactory(this));
	addFactory(new GenericPayloadParserFactory<MUCInvitationPayloadParser>("x", 
	        "jabber:x:conference",MUCInvitationPayloadParser.class));
	addFactory(new GenericPayloadParserFactory<MUCAdminPayloadParser>("query", 
	        "http://jabber.org/protocol/muc#admin",MUCAdminPayloadParser.class));
	addFactory(new GenericPayloadParserFactory<MUCDestroyPayloadParser>("destroy", 
	        "http://jabber.org/protocol/muc#user",MUCDestroyPayloadParser.class));
	addFactory(new GenericPayloadParserFactory<MUCDestroyPayloadParser>("destroy", 
	        "http://jabber.org/protocol/muc#owner",MUCDestroyPayloadParser.class));
	
	addFactory(new GenericPayloadParserFactory2<PubSubParser>("pubsub", "http://jabber.org/protocol/pubsub", this, PubSubParser.class));
	addFactory(new GenericPayloadParserFactory2<PubSubOwnerPubSubParser>("pubsub", "http://jabber.org/protocol/pubsub#owner", this, PubSubOwnerPubSubParser.class));
	addFactory(new GenericPayloadParserFactory2<PubSubEventParser>("event", "http://jabber.org/protocol/pubsub#event", this, PubSubEventParser.class));
	addFactory(new PubSubErrorParserFactory());
	
    addFactory(new GenericPayloadParserFactory<DelayParser>("delay", "urn:xmpp:delay", DelayParser.class));
    addFactory(new GenericPayloadParserFactory<ResultSetParser>("set", "http://jabber.org/protocol/rsm", ResultSetParser.class));
    addFactory(new GenericPayloadParserFactory2<ForwardedParser>("forwarded", "urn:xmpp:forward:0", this, ForwardedParser.class));
    addFactory(new GenericPayloadParserFactory2<MAMResultParser>("result", "urn:xmpp:mam:0", this, MAMResultParser.class));
    addFactory(new GenericPayloadParserFactory<MAMQueryParser>("query", "urn:xmpp:mam:0", MAMQueryParser.class));
    addFactory(new GenericPayloadParserFactory<MAMFinParser>("fin", "urn:xmpp:mam:0", MAMFinParser.class));
	
	//addFactory(new NicknameParserFactory());

        PayloadParserFactory defaultFactory = new RawXMLPayloadParserFactory();
        setDefaultFactory(defaultFactory);
    }
}
