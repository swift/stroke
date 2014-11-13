/*
 * Copyright (c) 2010-2015, Isode Limited, London, England.
 * All rights reserved.
 */

package com.isode.stroke.serializer.payloadserializers;

import com.isode.stroke.base.DateTime;
import com.isode.stroke.elements.VCard;
import com.isode.stroke.jid.JID;
import com.isode.stroke.serializer.GenericPayloadSerializer;
import com.isode.stroke.serializer.xml.XMLElement;
import com.isode.stroke.serializer.xml.XMLRawTextNode;
import com.isode.stroke.stringcodecs.Base64;

class VCardSerializer extends GenericPayloadSerializer<VCard>{
    
    public VCardSerializer() {
        super(VCard.class);
    }

    @Override
    protected String serializePayload(VCard vcard) {
        XMLElement queryElement = new XMLElement("vCard", "vcard-temp");
        if (vcard.getVersion() != null && !vcard.getVersion().isEmpty()) {
            queryElement.addNode(new XMLElement("VERSION", "", vcard.getVersion()));
        }
        if (vcard.getFullName() != null && !vcard.getFullName().isEmpty()) {
            queryElement.addNode(new XMLElement("FN", "", vcard.getFullName()));
        }
        if (vcard.getGivenName() != null && !vcard.getGivenName().isEmpty()
                || vcard.getFamilyName() != null && !vcard.getFamilyName().isEmpty()
                || vcard.getMiddleName() != null && !vcard.getMiddleName().isEmpty()
                || vcard.getPrefix() != null && !vcard.getPrefix().isEmpty()
                || vcard.getSuffix() != null && !vcard.getSuffix().isEmpty()) {
            XMLElement nameElement = new XMLElement("N");
            if (vcard.getFamilyName() != null && !vcard.getFamilyName().isEmpty()) {
                nameElement.addNode(new XMLElement("FAMILY", "", vcard.getFamilyName()));
            }
            if (vcard.getGivenName() != null && !vcard.getGivenName().isEmpty()) {
                nameElement.addNode(new XMLElement("GIVEN", "", vcard.getGivenName()));
            }
            if (vcard.getMiddleName() != null && !vcard.getMiddleName().isEmpty()) {
                nameElement.addNode(new XMLElement("MIDDLE", "", vcard.getMiddleName()));
            }
            if (vcard.getPrefix() != null && !vcard.getPrefix().isEmpty()) {
                nameElement.addNode(new XMLElement("PREFIX", "", vcard.getPrefix()));
            }
            if (vcard.getSuffix() != null && !vcard.getSuffix().isEmpty()) {
                nameElement.addNode(new XMLElement("SUFFIX", "", vcard.getSuffix()));
            }
            queryElement.addNode(nameElement);
        }
        if (vcard.getEMailAddresses() != null) for (final VCard.EMailAddress emailAddress : vcard.getEMailAddresses()) {
            XMLElement emailElement = new XMLElement("EMAIL");
            emailElement.addNode(new XMLElement("USERID", "", emailAddress.address));
            if (emailAddress.isHome) {
                emailElement.addNode(new XMLElement("HOME"));
            }
            if (emailAddress.isWork) {
                emailElement.addNode(new XMLElement("WORK"));
            }
            if (emailAddress.isInternet) {
                emailElement.addNode(new XMLElement("INTERNET"));
            }
            if (emailAddress.isPreferred) {
                emailElement.addNode(new XMLElement("PREF"));
            }
            if (emailAddress.isX400) {
                emailElement.addNode(new XMLElement("X400"));
            }
            queryElement.addNode(emailElement);
        }
        if (vcard.getNickname() != null && !vcard.getNickname().isEmpty()) {
            queryElement.addNode(new XMLElement("NICKNAME", "", vcard.getNickname()));
        }
        if (vcard.getPhoto() != null && !vcard.getPhoto().isEmpty() || vcard.getPhotoType() != null && !vcard.getPhotoType().isEmpty()) {
            XMLElement photoElement = new XMLElement("PHOTO");
            if (vcard.getPhotoType() != null && !vcard.getPhotoType().isEmpty()) {
                photoElement.addNode(new XMLElement("TYPE", "", vcard.getPhotoType()));
            }
            if (vcard.getPhoto() != null && !vcard.getPhoto().isEmpty()) {
                photoElement.addNode(new XMLElement("BINVAL", "", Base64.encode(vcard.getPhoto())));
            }
            queryElement.addNode(photoElement);
        }
        if (vcard.getBirthday() != null) {
            queryElement.addNode(new XMLElement("BDAY", "", DateTime.dateToString(vcard.getBirthday())));
        }

        if (vcard.getTelephones() != null) for (final VCard.Telephone telephone : vcard.getTelephones()) {
            XMLElement telElement = new XMLElement("TEL");
            telElement.addNode(new XMLElement("NUMBER", "", telephone.number));
            if (telephone.isHome) {
                telElement.addNode(new XMLElement("HOME"));
            }
            if (telephone.isWork) {
                telElement.addNode(new XMLElement("WORK"));
            }
            if (telephone.isVoice) {
                telElement.addNode(new XMLElement("VOICE"));
            }
            if (telephone.isFax) {
                telElement.addNode(new XMLElement("FAX"));
            }
            if (telephone.isPager) {
                telElement.addNode(new XMLElement("PAGER"));
            }
            if (telephone.isMSG) {
                telElement.addNode(new XMLElement("MSG"));
            }
            if (telephone.isCell) {
                telElement.addNode(new XMLElement("CELL"));
            }
            if (telephone.isVideo) {
                telElement.addNode(new XMLElement("VIDEO"));
            }
            if (telephone.isBBS) {
                telElement.addNode(new XMLElement("BBS"));
            }
            if (telephone.isModem) {
                telElement.addNode(new XMLElement("MODEM"));
            }
            if (telephone.isISDN) {
                telElement.addNode(new XMLElement("ISDN"));
            }
            if (telephone.isPCS) {
                telElement.addNode(new XMLElement("PCS"));
            }
            if (telephone.isPreferred) {
                telElement.addNode(new XMLElement("PREF"));
            }
            queryElement.addNode(telElement);
        }

        if (vcard.getAddresses() != null) for (final VCard.Address address : vcard.getAddresses()) {
            XMLElement adrElement = new XMLElement("ADR");
            if (!address.poBox.isEmpty()) {
                adrElement.addNode(new XMLElement("POBOX", "", address.poBox));
            }
            if (!address.addressExtension.isEmpty()) {
                adrElement.addNode(new XMLElement("EXTADD", "", address.addressExtension));
            }
            if (!address.street.isEmpty()) {
                adrElement.addNode(new XMLElement("STREET", "", address.street));
            }
            if (!address.locality.isEmpty()) {
                adrElement.addNode(new XMLElement("LOCALITY", "", address.locality));
            }
            if (!address.region.isEmpty()) {
                adrElement.addNode(new XMLElement("REGION", "", address.region));
            }
            if (!address.postalCode.isEmpty()) {
                adrElement.addNode(new XMLElement("PCODE", "", address.postalCode));
            }
            if (!address.country.isEmpty()) {
                adrElement.addNode(new XMLElement("CTRY", "", address.country));
            }

            if (address.isHome) {
                adrElement.addNode(new XMLElement("HOME"));
            }
            if (address.isWork) {
                adrElement.addNode(new XMLElement("WORK"));
            }
            if (address.isPostal) {
                adrElement.addNode(new XMLElement("POSTAL"));
            }
            if (address.isParcel) {
                adrElement.addNode(new XMLElement("PARCEL"));
            }
            if (address.deliveryType == VCard.DeliveryType.DomesticDelivery) {
                adrElement.addNode(new XMLElement("DOM"));
            }
            if (address.deliveryType == VCard.DeliveryType.InternationalDelivery) {
                adrElement.addNode(new XMLElement("INTL"));
            }
            if (address.isPreferred) {
                adrElement.addNode(new XMLElement("PREF"));
            }
            queryElement.addNode(adrElement);
        }

        if (vcard.getAddressLabels() != null) for (final VCard.AddressLabel addressLabel : vcard.getAddressLabels()) {
            XMLElement labelElement = new XMLElement("LABEL");

            for (final String line : addressLabel.lines) {
                labelElement.addNode(new XMLElement("LINE", "", line));
            }

            if (addressLabel.isHome) {
                labelElement.addNode(new XMLElement("HOME"));
            }
            if (addressLabel.isWork) {
                labelElement.addNode(new XMLElement("WORK"));
            }
            if (addressLabel.isPostal) {
                labelElement.addNode(new XMLElement("POSTAL"));
            }
            if (addressLabel.isParcel) {
                labelElement.addNode(new XMLElement("PARCEL"));
            }
            if (addressLabel.deliveryType == VCard.DeliveryType.DomesticDelivery) {
                labelElement.addNode(new XMLElement("DOM"));
            }
            if (addressLabel.deliveryType == VCard.DeliveryType.InternationalDelivery) {
                labelElement.addNode(new XMLElement("INTL"));
            }
            if (addressLabel.isPreferred) {
                labelElement.addNode(new XMLElement("PREF"));
            }
            queryElement.addNode(labelElement);
        }

        if (vcard.getJIDs() != null) for (final JID jid : vcard.getJIDs()) {
            queryElement.addNode(new XMLElement("JID", "", jid.toString()));
        }

        if (vcard.getDescription() != null && !vcard.getDescription().isEmpty()) {
            queryElement.addNode(new XMLElement("DESC", "", vcard.getDescription()));
        }

        if (vcard.getOrganizations() != null) for (final VCard.Organization org : vcard.getOrganizations()) {
            XMLElement orgElement = new XMLElement("ORG");
            if (!org.name.isEmpty()) {
                orgElement.addNode(new XMLElement("ORGNAME", "", org.name));
            }
            if (!org.units.isEmpty()) {
                for (final String unit : org.units) {
                    orgElement.addNode(new XMLElement("ORGUNIT", "", unit));
                }
            }
            queryElement.addNode(orgElement);
        }

        if (vcard.getTitles() != null) for (final String title : vcard.getTitles()) {
            queryElement.addNode(new XMLElement("TITLE", "", title));
        }

        if (vcard.getRoles() != null) for (final String role : vcard.getRoles()) {
            queryElement.addNode(new XMLElement("ROLE", "", role));
        }

        if (vcard.getURLs() != null) for (final String url : vcard.getURLs()) {
            queryElement.addNode(new XMLElement("URL", "", url));
        }

        if (vcard.getUnknownContent() != null && !vcard.getUnknownContent().isEmpty()) {
            queryElement.addNode(new XMLRawTextNode(vcard.getUnknownContent()));
        }
        return queryElement.serialize();
    }

}
