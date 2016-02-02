/*
 * Copyright (c) 2010-2016, Isode Limited, London, England.
 * All rights reserved.
 */
package com.isode.stroke.parser.payloadparsers;

import com.isode.stroke.parser.GenericPayloadParserFactory;
import com.isode.stroke.parser.GenericPayloadParserFactory2;
import com.isode.stroke.parser.PayloadParserFactory;
import com.isode.stroke.parser.PayloadParserFactoryCollection;

public class FullPayloadParserFactoryCollection extends PayloadParserFactoryCollection {
    public FullPayloadParserFactoryCollection() {
        addFactory(new GenericPayloadParserFactory<IBBParser>("", "http://jabber.org/protocol/ibb", IBBParser.class));
        addFactory(new GenericPayloadParserFactory<StatusShowParser>("show", StatusShowParser.class));
        addFactory(new GenericPayloadParserFactory<StatusParser>("status", StatusParser.class));
        addFactory(new GenericPayloadParserFactory<ReplaceParser>("replace", "http://swift.im/protocol/replace", ReplaceParser.class));
        addFactory(new GenericPayloadParserFactory<ReplaceParser>("replace", "urn:xmpp:message-correct:0", ReplaceParser.class));
        addFactory(new GenericPayloadParserFactory<LastParser>("query", "jabber:iq:last", LastParser.class));
        addFactory(new GenericPayloadParserFactory<BodyParser>("body", BodyParser.class));
        addFactory(new GenericPayloadParserFactory<SubjectParser>("subject", SubjectParser.class));
        addFactory(new GenericPayloadParserFactory<ThreadParser>("thread", ThreadParser.class));
        addFactory(new GenericPayloadParserFactory<PriorityParser>("priority", PriorityParser.class));
        addFactory(new ErrorParserFactory(this));
        addFactory(new GenericPayloadParserFactory<DelayParser>("delay", "urn:xmpp:delay", DelayParser.class));
        addFactory(new SoftwareVersionParserFactory());
        addFactory(new GenericPayloadParserFactory<StorageParser>("storage", "storage:bookmarks", StorageParser.class));
        addFactory(new GenericPayloadParserFactory<RosterItemExchangeParser>("x", "http://jabber.org/protocol/rosterx", RosterItemExchangeParser.class));
        addFactory(new RosterParserFactory());
        addFactory(new GenericPayloadParserFactory<DiscoInfoParser>("query", "http://jabber.org/protocol/disco#info", DiscoInfoParser.class));
        addFactory(new GenericPayloadParserFactory<DiscoItemsParser>("query", "http://jabber.org/protocol/disco#items", DiscoItemsParser.class));
        addFactory(new GenericPayloadParserFactory<CapsInfoParser> ("c", "http://jabber.org/protocol/caps", CapsInfoParser.class));
        addFactory(new ResourceBindParserFactory());
        addFactory(new StartSessionParserFactory());
        addFactory(new GenericPayloadParserFactory<BlockBlockPayloadParser>("block", "urn:xmpp:blocking", BlockBlockPayloadParser.class));
        addFactory(new GenericPayloadParserFactory<BlockBlockListPayloadParser>("blocklist", "urn:xmpp:blocking", BlockBlockListPayloadParser.class));
        addFactory(new GenericPayloadParserFactory<BlockUnblockPayloadPaser>("unblock", "urn:xmpp:blocking", BlockUnblockPayloadPaser.class));
        addFactory(new SecurityLabelParserFactory());
        addFactory(new GenericPayloadParserFactory<SecurityLabelsCatalogParser>("catalog", "urn:xmpp:sec-label:catalog:2", SecurityLabelsCatalogParser.class));
        addFactory(new FormParserFactory());
        addFactory(new GenericPayloadParserFactory<CommandParser>("command","http://jabber.org/protocol/commands", CommandParser.class));
        addFactory(new GenericPayloadParserFactory<InBandRegistrationPayloadParser>("query", "jabber:iq:register", InBandRegistrationPayloadParser.class));
        addFactory(new SearchPayloadParserFactory());
        addFactory(new GenericPayloadParserFactory<StreamInitiationParser>("si", "http://jabber.org/protocol/si", StreamInitiationParser.class)); 
        addFactory(new GenericPayloadParserFactory<BytestreamsParser>("query", "http://jabber.org/protocol/bytestreams", BytestreamsParser.class));
        addFactory(new GenericPayloadParserFactory<VCardUpdateParser>("x", "vcard-temp:x:update", VCardUpdateParser.class));
        addFactory(new GenericPayloadParserFactory<VCardParser>("vCard", "vcard-temp", VCardParser.class));
        addFactory(new PrivateStorageParserFactory(this));
        addFactory(new ChatStateParserFactory());
        addFactory(new MUCUserPayloadParserFactory(this));
        addFactory(new MUCOwnerPayloadParserFactory(this));
        addFactory(new GenericPayloadParserFactory<MUCInvitationPayloadParser>("x","jabber:x:conference",MUCInvitationPayloadParser.class));
        addFactory(new GenericPayloadParserFactory<MUCAdminPayloadParser>("query","http://jabber.org/protocol/muc#admin",MUCAdminPayloadParser.class));
        addFactory(new GenericPayloadParserFactory<MUCDestroyPayloadParser>("destroy","http://jabber.org/protocol/muc#user",MUCDestroyPayloadParser.class));
        addFactory(new GenericPayloadParserFactory<MUCDestroyPayloadParser>("destroy","http://jabber.org/protocol/muc#owner",MUCDestroyPayloadParser.class));
        addFactory(new GenericPayloadParserFactory<NicknameParser>("nick", "http://jabber.org/protocol/nick", NicknameParser.class));
        addFactory(new JingleParserFactory(this));
        addFactory(new GenericPayloadParserFactory<JingleReasonParser>("reason", "urn:xmpp:jingle:1", JingleReasonParser.class));
        addFactory(new JingleContentPayloadParserFactory(this));
        addFactory(new GenericPayloadParserFactory<JingleIBBTransportMethodPayloadParser>("transport", "urn:xmpp:jingle:transports:ibb:1", JingleIBBTransportMethodPayloadParser.class));
        addFactory(new GenericPayloadParserFactory<JingleS5BTransportMethodPayloadParser>("transport", "urn:xmpp:jingle:transports:s5b:1", JingleS5BTransportMethodPayloadParser.class));
        addFactory(new JingleFileTransferDescriptionParserFactory(this));
        addFactory(new GenericPayloadParserFactory<StreamInitiationFileInfoParser>("file", "http://jabber.org/protocol/si/profile/file-transfer", StreamInitiationFileInfoParser.class));
        addFactory(new GenericPayloadParserFactory<JingleFileTransferFileInfoParser>("file", JingleFileTransferFileInfoParser.class));
        addFactory(new GenericPayloadParserFactory<JingleFileTransferHashParser>("checksum", JingleFileTransferHashParser.class));
        addFactory(new GenericPayloadParserFactory<S5BProxyRequestParser>("query", "http://jabber.org/protocol/bytestreams",S5BProxyRequestParser.class));
        // addFactory(new GenericPayloadParserFactory<WhiteboardParser>("wb","http://swift.im/whiteboard",WhiteboardParser.class));
        addFactory(new GenericPayloadParserFactory<UserLocationParser>("geoloc", "http://jabber.org/protocol/geoloc", UserLocationParser.class));
        addFactory(new GenericPayloadParserFactory<UserTuneParser>("tune", "http://jabber.org/protocol/tune", UserTuneParser.class));
        addFactory(new DeliveryReceiptParserFactory());
        addFactory(new DeliveryReceiptRequestParserFactory());
        addFactory(new GenericPayloadParserFactory<IdleParser>("idle", "urn:xmpp:idle:1",IdleParser.class));
        addFactory(new GenericPayloadParserFactory2<PubSubParser>("pubsub", "http://jabber.org/protocol/pubsub", this, PubSubParser.class));
        addFactory(new GenericPayloadParserFactory2<PubSubOwnerPubSubParser>("pubsub", "http://jabber.org/protocol/pubsub#owner", this, PubSubOwnerPubSubParser.class));
        addFactory(new GenericPayloadParserFactory2<PubSubEventParser>("event", "http://jabber.org/protocol/pubsub#event", this, PubSubEventParser.class));
        addFactory(new PubSubErrorParserFactory());
        addFactory(new GenericPayloadParserFactory<ResultSetParser>("set", "http://jabber.org/protocol/rsm", ResultSetParser.class));
        addFactory(new GenericPayloadParserFactory2<ForwardedParser>("forwarded", "urn:xmpp:forward:0", this, ForwardedParser.class));
        addFactory(new GenericPayloadParserFactory2<MAMResultParser>("result", "urn:xmpp:mam:0", this, MAMResultParser.class));
        addFactory(new GenericPayloadParserFactory<MAMQueryParser>("query", "urn:xmpp:mam:0", MAMQueryParser.class));
        addFactory(new GenericPayloadParserFactory<MAMFinParser>("fin", "urn:xmpp:mam:0", MAMFinParser.class));
        addFactory(new GenericPayloadParserFactory<IsodeIQDelegationParser>("delegate", "http://isode.com/iq_delegation", IsodeIQDelegationParser.class));
        addFactory(new GenericPayloadParserFactory<CarbonsEnableParser>("enable", "urn:xmpp:carbons:2", CarbonsEnableParser.class));
        addFactory(new GenericPayloadParserFactory<CarbonsDisableParser>("disable", "urn:xmpp:carbons:2", CarbonsDisableParser.class));
        addFactory(new GenericPayloadParserFactory2<CarbonsReceivedParser>("received", "urn:xmpp:carbons:2", this, CarbonsReceivedParser.class));
        addFactory(new GenericPayloadParserFactory2<CarbonsSentParser>("sent", "urn:xmpp:carbons:2", this, CarbonsSentParser.class));
        addFactory(new GenericPayloadParserFactory<CarbonsPrivateParser>("private", "urn:xmpp:carbons:2", CarbonsPrivateParser.class));

        PayloadParserFactory defaultFactory = new RawXMLPayloadParserFactory();
        setDefaultFactory(defaultFactory);
    }
}
