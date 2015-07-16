/*
 * Copyright (c) 2010-2015, Isode Limited, London, England.
 * All rights reserved.
 */
package com.isode.stroke.parser;

import com.isode.stroke.elements.ProtocolHeader;
import java.util.logging.Logger;

public class XMPPParser implements XMLParserClient {
    
    private XMLParser xmlParser_;
    private XMPPParserClient client_;
    private PayloadParserFactoryCollection payloadParserFactories_;
    private int level_ = 0;
    private ElementParser currentElementParser_ = null;
    private boolean parseErrorOccurred_ = false;
    private Logger logger_ = Logger.getLogger(this.getClass().getName());
    private final int TopLevel = 0;
    private final int StreamLevel = 1;
    private final int ElementLevel = 2;

    public XMPPParser(XMPPParserClient parserClient,
            PayloadParserFactoryCollection payloadParserFactories) {
        xmlParser_ = null;
        client_ = parserClient;
        payloadParserFactories_ = payloadParserFactories;
        level_ = 0;
        currentElementParser_ = null;
        parseErrorOccurred_ = false;
        xmlParser_ = PlatformXMLParserFactory.createXMLParser(this);
    }
    
    public boolean parse(String data) {
        boolean xmlParseResult = xmlParser_.parse(data);
        return xmlParseResult && !parseErrorOccurred_;
    }
    
    public void handleStartElement(String element, String ns,
            AttributeMap attributes) {
        if (!parseErrorOccurred_) {
            if (level_ == TopLevel) {
                if (element.equals("stream") && ns.equals("http://etherx.jabber.org/streams")) {
                    ProtocolHeader header = new ProtocolHeader();
                    header.setFrom(attributes.getAttribute("from"));
                    header.setTo(attributes.getAttribute("to"));
                    header.setID(attributes.getAttribute("id"));
                    header.setVersion(attributes.getAttribute("version"));
                    client_.handleStreamStart(header);
                }
                else {
                    parseErrorOccurred_ = true;
                }
            }
            else {
                if (level_ == StreamLevel) {
                    assert(currentElementParser_ == null);
                    currentElementParser_ = createElementParser(element, ns);
                }
                currentElementParser_.handleStartElement(element, ns, attributes);
            }
        }
        ++level_;
    }
    
    public void handleEndElement(String element, String ns) {
        assert(level_ > TopLevel);
        --level_;
        if (!parseErrorOccurred_) {
            if (level_ == TopLevel) {
                assert(element.equals("stream"));
                client_.handleStreamEnd();
            }
            else {
                assert(currentElementParser_ != null);
                currentElementParser_.handleEndElement(element, ns);
                if (level_ == StreamLevel) {
                    client_.handleElement(currentElementParser_.getElement());
                    currentElementParser_ = null;
                }
            }
        }
    }
    
    public void handleCharacterData(String data) {
        if (!parseErrorOccurred_) {
            if (currentElementParser_ != null) {
                currentElementParser_.handleCharacterData(data);
            }
        //else {
        //  std::cerr << "XMPPParser: Ignoring stray character data: " << data << std::endl;
        //}
        }
    }

    private ElementParser createElementParser(String element, String xmlns) {
        if (element.equals("presence")) {
            return new PresenceParser(payloadParserFactories_);
        } else if (element.equals("iq")) {
            return new IQParser(payloadParserFactories_);
        } else if (element.equals("message")) {
            return new MessageParser(payloadParserFactories_);
        } else if (element.equals("features")
                && xmlns.equals("http://etherx.jabber.org/streams")) {
            return new StreamFeaturesParser();
        } else if (element.equals("error") && xmlns.equals("http://etherx.jabber.org/streams")) {
            return new StreamErrorParser();
        } else if (element.equals("auth")) {
            return new AuthRequestParser();
        } else if (element.equals("success")) {
            return new AuthSuccessParser();
        } else if (element.equals("failure")
                && xmlns.equals("urn:ietf:params:xml:ns:xmpp-sasl")) {
            return new AuthFailureParser();
        } else if (element.equals("challenge")
                && xmlns.equals("urn:ietf:params:xml:ns:xmpp-sasl")) {
            return new AuthChallengeParser();
        } else if (element.equals("response")
                && xmlns.equals("urn:ietf:params:xml:ns:xmpp-sasl")) {
            return new AuthResponseParser();
        } else if (element.equals("starttls")) {
            return new StartTLSParser();
        } else if (element.equals("failure")
                && xmlns.equals("urn:ietf:params:xml:ns:xmpp-tls")) {
            return new StartTLSFailureParser();
        } else if (element.equals("compress")) {
            return new CompressParser();
        } else if (element.equals("compressed")) {
            return new CompressedParser();
        } else if (element.equals("failure")
                && xmlns.equals("http://jabber.org/protocol/compress")) {
            return new CompressFailureParser();
        } else if (element.equals("proceed")) {
            return new TLSProceedParser();
        } else if (element.equals("enable") && xmlns.equals("urn:xmpp:sm:2")) {
            return new EnableStreamManagementParser();
        } else if (element.equals("enabled") && xmlns.equals("urn:xmpp:sm:2")) {
            return new StreamManagementEnabledParser();
        } else if (element.equals("failed") && xmlns.equals("urn:xmpp:sm:2")) {
            return new StreamManagementFailedParser();
        } else if (element.equals("resume") && xmlns.equals("urn:xmpp:sm:2")) {
            return new StreamResumeParser();
        } else if (element.equals("resumed") && xmlns.equals("urn:xmpp:sm:2")) {
            return new StreamResumedParser();
        } else if (element.equals("a") && xmlns.equals("urn:xmpp:sm:2")) {
            return new StanzaAckParser();
        } else if (element.equals("r") && xmlns.equals("urn:xmpp:sm:2")) {
            return new StanzaAckRequestParser();
        } else if (element.equals("handshake")) {
            return new ComponentHandshakeParser();
        }
        return new UnknownElementParser();
        
    }
}
