/*
 * Copyright (c) 2012 Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2010 Remko Tronçon
 * All rights reserved.
 */ 

package com.isode.stroke.parser.payloadparsers;

import com.isode.stroke.elements.ErrorPayload;
import com.isode.stroke.parser.AttributeMap;
import com.isode.stroke.parser.GenericPayloadParser;
import com.isode.stroke.parser.PayloadParser;
import com.isode.stroke.parser.PayloadParserFactory;
import com.isode.stroke.parser.PayloadParserFactoryCollection;

/**
 * Class that represents a parser for the Error payload
 *
 */
public class ErrorParser extends GenericPayloadParser<ErrorPayload> {
    private enum Level { 
        TopLevel(0), 
        PayloadLevel(1);
        private Level(int val) {
            this.value = val;
        }
        int value;
    }
    private PayloadParserFactoryCollection factories_;
    private int level_;
    private String currentText_ = "";
    private PayloadParser currentPayloadParser;

    /**
     * Create the parse
     * @param factories reference to the Payload parser factory, 
     *          should not be null 
     */
    public ErrorParser(PayloadParserFactoryCollection factories) {
        super(new ErrorPayload());
        level_ = Level.TopLevel.value; 
        this.factories_ = factories;
    }

    @Override
    public void handleStartElement(String element, String ns, final AttributeMap attributes) { 
        if (level_ == Level.TopLevel.value) {
            String type = attributes.getAttribute("type");
            if (type.equals("continue")) {
                getPayloadInternal().setType(ErrorPayload.Type.Continue);
            } else if (type.equals("modify")) {
                getPayloadInternal().setType(ErrorPayload.Type.Modify);
            }else if (type.equals("auth")) {
                getPayloadInternal().setType(ErrorPayload.Type.Auth);
            }else if (type.equals("wait")) {
                getPayloadInternal().setType(ErrorPayload.Type.Wait);
            }else {  
                getPayloadInternal().setType(ErrorPayload.Type.Cancel);
            }
        }else if (level_ == Level.PayloadLevel.value) {
            if (element.equals("text")) {

            }else if (element.equals("bad-request")) {
                getPayloadInternal().setCondition(ErrorPayload.Condition.BadRequest);
            }else if (element.equals("conflict")) {
                getPayloadInternal().setCondition(ErrorPayload.Condition.Conflict);
            }else if (element.equals("feature-not-implemented")) {
                getPayloadInternal().setCondition(ErrorPayload.Condition.FeatureNotImplemented);
            }else if (element.equals("forbidden")) {
                getPayloadInternal().setCondition(ErrorPayload.Condition.Forbidden);
            }else if (element.equals("gone")) {
                getPayloadInternal().setCondition(ErrorPayload.Condition.Gone);
            }else if (element.equals("internal-server-error")) {
                getPayloadInternal().setCondition(ErrorPayload.Condition.InternalServerError);
            }else if (element.equals("item-not-found")) {
                getPayloadInternal().setCondition(ErrorPayload.Condition.ItemNotFound);
            }else if (element.equals("jid-malformed")) {
                getPayloadInternal().setCondition(ErrorPayload.Condition.JIDMalformed);
            }else if (element.equals("not-acceptable")) {
                getPayloadInternal().setCondition(ErrorPayload.Condition.NotAcceptable);
            }else if (element.equals("not-allowed")) {
                getPayloadInternal().setCondition(ErrorPayload.Condition.NotAllowed);
            }else if (element.equals("not-authorized")) {
                getPayloadInternal().setCondition(ErrorPayload.Condition.NotAuthorized);
            }else if (element.equals("payment-required")) {
                getPayloadInternal().setCondition(ErrorPayload.Condition.PaymentRequired);
            }else if (element.equals("recipient-unavailable")) {
                getPayloadInternal().setCondition(ErrorPayload.Condition.RecipientUnavailable);
            }else if (element.equals("redirect")) {
                getPayloadInternal().setCondition(ErrorPayload.Condition.Redirect);
            }else if (element.equals("registration-required")) {
                getPayloadInternal().setCondition(ErrorPayload.Condition.RegistrationRequired);
            }else if (element.equals("remote-server-not-found")) {
                getPayloadInternal().setCondition(ErrorPayload.Condition.RemoteServerNotFound);
            }else if (element.equals("remote-server-timeout")) {
                getPayloadInternal().setCondition(ErrorPayload.Condition.RemoteServerTimeout);
            }else if (element.equals("resource-constraint")) {
                getPayloadInternal().setCondition(ErrorPayload.Condition.ResourceConstraint);
            }else if (element.equals("service-unavailable")) {
                getPayloadInternal().setCondition(ErrorPayload.Condition.ServiceUnavailable);
            }else if (element.equals("subscription-required")) {
                getPayloadInternal().setCondition(ErrorPayload.Condition.SubscriptionRequired);
            }else if (element.equals("unexpected-request")) {
                getPayloadInternal().setCondition(ErrorPayload.Condition.UnexpectedRequest);
            }else {
                PayloadParserFactory payloadParserFactory = factories_.getPayloadParserFactory(element, ns, attributes);
                if (payloadParserFactory != null) {
                    currentPayloadParser = payloadParserFactory.createPayloadParser();
                } else {
                    getPayloadInternal().setCondition(ErrorPayload.Condition.UndefinedCondition);
                }
            }
        }
        if (level_ >= Level.PayloadLevel.value && currentPayloadParser != null) {
            currentPayloadParser.handleStartElement(element, ns, attributes);
        }
        ++level_;
    }

    @Override
    public void handleEndElement(final String element, final String ns) {
        --level_;
        ErrorPayload payloadInternal = getPayloadInternal();
        if (currentPayloadParser != null) {
            if (level_ >= Level.PayloadLevel.value) {
                currentPayloadParser.handleEndElement(element, ns);
            }
            if (level_ == Level.PayloadLevel.value) {
                getPayloadInternal().setPayload(currentPayloadParser.getPayload());
                currentPayloadParser= null;
            }
        }
        else if (level_ == Level.PayloadLevel.value) {
            if (element.equals("text")) {
                payloadInternal.setText(currentText_);
            }
        }
    }

    @Override
    public void handleCharacterData(final String data) {
        if (level_ > Level.PayloadLevel.value && currentPayloadParser != null) {
            currentPayloadParser.handleCharacterData(data);
        }else{
            currentText_ += data;
        }
    }
}
