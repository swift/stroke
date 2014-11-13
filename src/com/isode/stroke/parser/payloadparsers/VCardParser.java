/*
 * Copyright (c) 2010-2015, Isode Limited, London, England.
 * All rights reserved.
 */
package com.isode.stroke.parser.payloadparsers;

import java.util.Stack;

import com.isode.stroke.base.DateTime;
import com.isode.stroke.elements.VCard;
import com.isode.stroke.jid.JID;
import com.isode.stroke.parser.AttributeMap;
import com.isode.stroke.parser.GenericPayloadParser;
import com.isode.stroke.parser.SerializingParser;
import com.isode.stroke.stringcodecs.Base64;

public class VCardParser extends GenericPayloadParser<VCard> {

    Stack<String> elementStack_ = new Stack<String>();
    VCard.EMailAddress currentEMailAddress_;
    VCard.Telephone currentTelephone_;
    VCard.Address currentAddress_;
    VCard.AddressLabel currentAddressLabel_;
    VCard.Organization currentOrganization_;
    SerializingParser unknownContentParser_;
    String currentText_ = "";
    
    public VCardParser() {
        super(new VCard());
    }

    public void handleStartElement(String element, String ns, AttributeMap attributes) {
        elementStack_.add(element);
        String elementHierarchy = getElementHierarchy();
        if ("/vCard/EMAIL".equals(elementHierarchy)) {
            currentEMailAddress_ = new VCard.EMailAddress();
        }
        if ("/vCard/TEL".equals(elementHierarchy)) {
            currentTelephone_ = new VCard.Telephone();
        }
        if ("/vCard/ADR".equals(elementHierarchy)) {
            currentAddress_ = new VCard.Address();
        }
        if ("/vCard/LABEL".equals(elementHierarchy)) {
            currentAddressLabel_ = new VCard.AddressLabel();
        }
        if ("/vCard/ORG".equals(elementHierarchy)) {
            currentOrganization_ = new VCard.Organization();
        }
        if (elementStack_.size() == 2) {
            assert(unknownContentParser_ == null);
            unknownContentParser_ = new SerializingParser();
            unknownContentParser_.handleStartElement(element, ns, attributes);
        }
        else if (unknownContentParser_ != null) {
            unknownContentParser_.handleStartElement(element, ns, attributes);
        }

        currentText_ = "";
    }

    public void handleEndElement(String element, String ns) {
        if (unknownContentParser_ != null) {
            unknownContentParser_.handleEndElement(element, ns);
        }

        String elementHierarchy = getElementHierarchy();
        if ("/vCard/VERSION".equals(elementHierarchy)) {
            getPayloadInternal().setVersion(currentText_);
        }
        else if ("/vCard/FN".equals(elementHierarchy)) {
            getPayloadInternal().setFullName(currentText_);
        }
        else if ("/vCard/N/FAMILY".equals(elementHierarchy)) {
            getPayloadInternal().setFamilyName(currentText_);
        }
        else if ("/vCard/N/GIVEN".equals(elementHierarchy)) {
            getPayloadInternal().setGivenName(currentText_);
        }
        else if ("/vCard/N/MIDDLE".equals(elementHierarchy)) {
            getPayloadInternal().setMiddleName(currentText_);
        }
        else if ("/vCard/N/PREFIX".equals(elementHierarchy)) {
            getPayloadInternal().setPrefix(currentText_);
        }
        else if ("/vCard/N/SUFFIX".equals(elementHierarchy)) {
            getPayloadInternal().setSuffix(currentText_);
        }
        else if ("/vCard/N".equals(elementHierarchy)) {
        }
        else if ("/vCard/NICKNAME".equals(elementHierarchy)) {
            getPayloadInternal().setNickname(currentText_);
        }
        else if ("/vCard/PHOTO/TYPE".equals(elementHierarchy)) {
            getPayloadInternal().setPhotoType(currentText_);
        }
        else if ("/vCard/PHOTO/BINVAL".equals(elementHierarchy)) {
            getPayloadInternal().setPhoto(Base64.decode(currentText_.replace("\n", "").replace("\r", "")));
        }
        else if ("/vCard/PHOTO".equals(elementHierarchy)) {
        }
        else if ("/vCard/EMAIL/USERID".equals(elementHierarchy)) {
            currentEMailAddress_.address = currentText_;
        }
        else if ("/vCard/EMAIL/HOME".equals(elementHierarchy)) {
            currentEMailAddress_.isHome = true;
        }
        else if ("/vCard/EMAIL/WORK".equals(elementHierarchy)) {
            currentEMailAddress_.isWork = true;
        }
        else if ("/vCard/EMAIL/INTERNET".equals(elementHierarchy)) {
            currentEMailAddress_.isInternet = true;
        }
        else if ("/vCard/EMAIL/X400".equals(elementHierarchy)) {
            currentEMailAddress_.isX400 = true;
        }
        else if ("/vCard/EMAIL/PREF".equals(elementHierarchy)) {
            currentEMailAddress_.isPreferred = true;
        }
        else if ("/vCard/EMAIL".equals(elementHierarchy)  && currentEMailAddress_.address != null && !currentEMailAddress_.address.isEmpty()) {
            getPayloadInternal().addEMailAddress(currentEMailAddress_);
        }
        else if ("/vCard/BDAY".equals(elementHierarchy) && !currentText_.isEmpty()) {
            getPayloadInternal().setBirthday(DateTime.stringToDate(currentText_));
        }
        else if ("/vCard/TEL/NUMBER".equals(elementHierarchy)) {
            currentTelephone_.number = currentText_;
        }
        else if ("/vCard/TEL/HOME".equals(elementHierarchy)) {
            currentTelephone_.isHome = true;
        }
        else if ("/vCard/TEL/WORK".equals(elementHierarchy)) {
            currentTelephone_.isWork = true;
        }
        else if ("/vCard/TEL/VOICE".equals(elementHierarchy)) {
            currentTelephone_.isVoice = true;
        }
        else if ("/vCard/TEL/FAX".equals(elementHierarchy)) {
            currentTelephone_.isFax = true;
        }
        else if ("/vCard/TEL/PAGER".equals(elementHierarchy)) {
            currentTelephone_.isPager = true;
        }
        else if ("/vCard/TEL/MSG".equals(elementHierarchy)) {
            currentTelephone_.isMSG = true;
        }
        else if ("/vCard/TEL/CELL".equals(elementHierarchy)) {
            currentTelephone_.isCell = true;
        }
        else if ("/vCard/TEL/VIDEO".equals(elementHierarchy)) {
            currentTelephone_.isVideo = true;
        }
        else if ("/vCard/TEL/BBS".equals(elementHierarchy)) {
            currentTelephone_.isBBS = true;
        }
        else if ("/vCard/TEL/MODEM".equals(elementHierarchy)) {
            currentTelephone_.isModem = true;
        }
        else if ("/vCard/TEL/ISDN".equals(elementHierarchy)) {
            currentTelephone_.isISDN = true;
        }
        else if ("/vCard/TEL/PCS".equals(elementHierarchy)) {
            currentTelephone_.isPCS = true;
        }
        else if ("/vCard/TEL/PREF".equals(elementHierarchy)) {
            currentTelephone_.isPreferred = true;
        }
        else if ("/vCard/TEL".equals(elementHierarchy) && currentTelephone_.number != null && !currentTelephone_.number.isEmpty()) {
            getPayloadInternal().addTelephone(currentTelephone_);
        }
        else if ("/vCard/ADR/HOME".equals(elementHierarchy)) {
            currentAddress_.isHome = true;
        }
        else if ("/vCard/ADR/WORK".equals(elementHierarchy)) {
            currentAddress_.isWork = true;
        }
        else if ("/vCard/ADR/POSTAL".equals(elementHierarchy)) {
            currentAddress_.isPostal = true;
        }
        else if ("/vCard/ADR/PARCEL".equals(elementHierarchy)) {
            currentAddress_.isParcel = true;
        }
        else if ("/vCard/ADR/DOM".equals(elementHierarchy)) {
            currentAddress_.deliveryType = VCard.DeliveryType.DomesticDelivery;
        }
        else if ("/vCard/ADR/INTL".equals(elementHierarchy)) {
            currentAddress_.deliveryType = VCard.DeliveryType.InternationalDelivery;
        }
        else if ("/vCard/ADR/PREF".equals(elementHierarchy)) {
            currentAddress_.isPreferred = true;
        }
        else if ("/vCard/ADR/POBOX".equals(elementHierarchy)) {
            currentAddress_.poBox = currentText_;
        }
        else if ("/vCard/ADR/EXTADD".equals(elementHierarchy)) {
            currentAddress_.addressExtension = currentText_;
        }
        else if ("/vCard/ADR/STREET".equals(elementHierarchy)) {
            currentAddress_.street = currentText_;
        }
        else if ("/vCard/ADR/LOCALITY".equals(elementHierarchy)) {
            currentAddress_.locality = currentText_;
        }
        else if ("/vCard/ADR/REGION".equals(elementHierarchy)) {
            currentAddress_.region = currentText_;
        }
        else if ("/vCard/ADR/PCODE".equals(elementHierarchy)) {
            currentAddress_.postalCode = currentText_;
        }
        else if ("/vCard/ADR/CTRY".equals(elementHierarchy)) {
            currentAddress_.country = currentText_;
        }
        else if ("/vCard/ADR".equals(elementHierarchy)) {
            if (currentAddress_.poBox != null                   && !currentAddress_.poBox.isEmpty()
                    || currentAddress_.addressExtension != null && !currentAddress_.addressExtension.isEmpty()
                    || currentAddress_.street != null           && !currentAddress_.street.isEmpty()
                    || currentAddress_.locality != null         && !currentAddress_.locality.isEmpty()
                    || currentAddress_.region != null           && !currentAddress_.region.isEmpty()
                    || currentAddress_.postalCode != null       && !currentAddress_.postalCode.isEmpty()
                    || currentAddress_.country != null          && !currentAddress_.country.isEmpty()) {
                getPayloadInternal().addAddress(currentAddress_);
            }
        }
        else if ("/vCard/LABEL/HOME".equals(elementHierarchy)) {
            currentAddressLabel_.isHome = true;
        }
        else if ("/vCard/LABEL/WORK".equals(elementHierarchy)) {
            currentAddressLabel_.isWork = true;
        }
        else if ("/vCard/LABEL/POSTAL".equals(elementHierarchy)) {
            currentAddressLabel_.isPostal = true;
        }
        else if ("/vCard/LABEL/PARCEL".equals(elementHierarchy)) {
            currentAddressLabel_.isParcel = true;
        }
        else if ("/vCard/LABEL/DOM".equals(elementHierarchy)) {
            currentAddressLabel_.deliveryType = VCard.DeliveryType.DomesticDelivery;
        }
        else if ("/vCard/LABEL/INTL".equals(elementHierarchy)) {
            currentAddressLabel_.deliveryType = VCard.DeliveryType.InternationalDelivery;
        }
        else if ("/vCard/LABEL/PREF".equals(elementHierarchy)) {
            currentAddressLabel_.isPreferred = true;
        }
        else if ("/vCard/LABEL/LINE".equals(elementHierarchy)) {
            currentAddressLabel_.lines.add(currentText_);
        }
        else if ("/vCard/LABEL".equals(elementHierarchy)) {
            getPayloadInternal().addAddressLabel(currentAddressLabel_);
        }
        else if ("/vCard/JID".equals(elementHierarchy) && !currentText_.isEmpty()) {
            getPayloadInternal().addJID(new JID(currentText_));
        }
        else if ("/vCard/DESC".equals(elementHierarchy)) {
            getPayloadInternal().setDescription(currentText_);
        }
        else if ("/vCard/ORG/ORGNAME".equals(elementHierarchy)) {
            currentOrganization_.name = currentText_;
        }
        else if ("/vCard/ORG/ORGUNIT".equals(elementHierarchy) && !currentText_.isEmpty()) {
            currentOrganization_.units.add(currentText_);
        }
        else if ("/vCard/ORG".equals(elementHierarchy)) {
            if (!currentOrganization_.name.isEmpty() || !currentOrganization_.units.isEmpty()) {
                getPayloadInternal().addOrganization(currentOrganization_);
            }
        }
        else if ("/vCard/TITLE".equals(elementHierarchy) && !currentText_.isEmpty()) {
            getPayloadInternal().addTitle(currentText_);
        }
        else if ("/vCard/ROLE".equals(elementHierarchy) && !currentText_.isEmpty()) {
            getPayloadInternal().addRole(currentText_);
        }
        else if ("/vCard/URL".equals(elementHierarchy) && !currentText_.isEmpty()) {
            getPayloadInternal().addURL(currentText_);
        }
        else if (elementStack_.size() == 2 && unknownContentParser_ != null) {
            getPayloadInternal().addUnknownContent(unknownContentParser_.getResult());
        }

        if (elementStack_.size() == 2 && unknownContentParser_ != null) {
            unknownContentParser_ = null;
        }
        elementStack_.pop();
    }
    
    public void handleCharacterData(String text) {
        if (unknownContentParser_ != null) {
            unknownContentParser_.handleCharacterData(text);
        }
        currentText_ += text;
    }
    
    private String getElementHierarchy()  {
        String result = "";
        for(String element : elementStack_) {
            result += "/" + element;
        }
        return result;
    }
}
