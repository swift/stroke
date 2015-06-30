/*
 * Copyright (c) 2010-2015, Isode Limited, London, England.
 * All rights reserved.
 */
package com.isode.stroke.elements;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.isode.stroke.base.ByteArray;
import com.isode.stroke.jid.JID;

public class VCard extends Payload implements Serializable {
    private String version_ = "";
    private String fullName_ = "";
    private String familyName_ = "";
    private String givenName_ = "";
    private String middleName_ = "";
    private String prefix_ = "";
    private String suffix_ = "";
//	private //String email_;
    private ByteArray photo_;
    private String photoType_ = "";
    private String nick_ = "";
    private Date birthday_;
    private String unknownContent_ = "";
    private List<EMailAddress> emailAddresses_ = new ArrayList<EMailAddress>();
    private List<Telephone> telephones_ = new ArrayList<Telephone>();
    private List<Address> addresses_ = new ArrayList<Address>();
    private List<AddressLabel> addressLabels_ = new ArrayList<AddressLabel>();
    private List<JID> jids_ = new ArrayList<JID>();
    private String description_ = "";
    private List<Organization> organizations_ = new ArrayList<Organization>();
    private List<String> titles_ = new ArrayList<String>();
    private List<String> roles_ = new ArrayList<String>();
    private List<String> urls_ = new ArrayList<String>();

    public static class EMailAddress {
        public boolean isHome;
        public boolean isWork;
        public boolean isInternet;
        public boolean isPreferred;
        public boolean isX400;
        public String address = "";
    };

    public static class Telephone {
        public boolean isHome;
        public boolean isWork;
        public boolean isVoice;
        public boolean isFax;
        public boolean isPager;
        public boolean isMSG;
        public boolean isCell;
        public boolean isVideo;
        public boolean isBBS;
        public boolean isModem;
        public boolean isISDN;
        public boolean isPCS;
        public boolean isPreferred;
        public String number = "";
    };

    public static enum DeliveryType {
        DomesticDelivery,
        InternationalDelivery,
        None
    };

    public static class Address {
        public boolean isHome;
        public boolean isWork;
        public boolean isPostal;
        public boolean isParcel;
        public DeliveryType deliveryType;
        public boolean isPreferred;

        public String poBox = "";
        public String addressExtension = "";
        public String street = "";
        public String locality = "";
        public String region = "";
        public String postalCode = "";
        public String country = "";
    };

    public static class AddressLabel {
        public boolean isHome;
        public boolean isWork;
        public boolean isPostal;
        public boolean isParcel;
        public DeliveryType deliveryType;
        public boolean isPreferred;
        public List<String> lines = new ArrayList<String>();
    };

    public static class Organization {
        public String name = "";
        public List<String> units = new ArrayList<String>();
    };

    public VCard() {}

    public void setVersion(final String version) { version_ = version; }
    public final String getVersion() { return version_; }

    public void setFullName(final String fullName) { fullName_ = fullName; }
    public final String getFullName() { return fullName_; }

    public void setFamilyName(final String familyName) { familyName_ = familyName; }
    public final String getFamilyName() { return familyName_; }

    public void setGivenName(final String givenName) { givenName_ = givenName; }
    public final String getGivenName() { return givenName_; }

    public void setMiddleName(final String middleName) { middleName_ = middleName; }
    public final String getMiddleName() { return middleName_; }

    public void setPrefix(final String prefix) { prefix_ = prefix; }
    public final String getPrefix() { return prefix_; }

    public void setSuffix(final String suffix) { suffix_ = suffix; }
    public final String getSuffix() { return suffix_; }


    //void setEMailAddress(final String email) { email_ = email; }
    //final String getEMailAddress() { return email_; }

    public void setNickname(final String nick) { nick_ = nick; }
    public final String getNickname() { return nick_; }

    public void setPhoto(final ByteArray photo) { photo_ = photo; }
    public final ByteArray getPhoto() {
    	if(this.photo_ != null) {
    		return photo_;
    	}
    	else {
    		return new ByteArray();
    	}
    }

    public void setPhotoType(final String photoType) { photoType_ = photoType; }
    public final String getPhotoType() { return photoType_; }

    public final String getUnknownContent() { return unknownContent_; }
    public void addUnknownContent(final String c) { 
        unknownContent_ += c;
    }

    public final List<EMailAddress> getEMailAddresses() {
        return emailAddresses_;
    }

    public void addEMailAddress(final EMailAddress email) {
        if (emailAddresses_ == null) emailAddresses_ = new ArrayList<EMailAddress>();
        emailAddresses_.add(email);
    }

    public void clearEMailAddresses() {
        if (emailAddresses_ != null) emailAddresses_.clear();
    }

    /**
    * @param date, null indicates invalid date.
    */
    public void setBirthday(final Date birthday) {
        birthday_ = birthday;
    }

    /**
    * @return date, may be null which indicates invalid date.
    */
    public final Date getBirthday() {
        return birthday_;
    }

    public void addTelephone(final Telephone phone) {
        if (telephones_ == null) telephones_ = new ArrayList<Telephone>();
        telephones_.add(phone);
    }

    public void clearTelephones() {
        if (telephones_ != null) telephones_.clear();
    }

    public final List<Address> getAddresses() {
        return addresses_;
    }

    public void addAddress(final Address address) {
        if (addresses_ == null) addresses_ = new ArrayList<Address>();
        addresses_.add(address);
    }

    public void clearAddresses() {
        if (addresses_ != null) addresses_.clear();
    }

    public final List<AddressLabel> getAddressLabels() {
        return addressLabels_;
    }

    public void addAddressLabel(final AddressLabel addressLabel) {
        if (addressLabels_ == null) addressLabels_ = new ArrayList<AddressLabel>();
        addressLabels_.add(addressLabel);
    }

    public void clearAddressLabels() {
        if (addressLabels_ != null) addressLabels_.clear();
    }

    public final List<JID> getJIDs() {
        if (jids_ == null) jids_ = new ArrayList<JID>();
        return jids_;
    }

    public void clearJIDs() {
        if (jids_ != null) jids_.clear();
    }

    public final String getDescription() {
        return description_;
    }

    public void setDescription(final String description) {
        this.description_ = description;
    }

    public final List<Organization> getOrganizations() {
        return organizations_;
    }

    public void addOrganization(final Organization organization) {
        if (organizations_ == null) organizations_ = new ArrayList<Organization>();
        organizations_.add(organization);
    }

    public void clearOrganizations() {
        if (organizations_ != null) organizations_.clear();
    }

    public final List<String> getTitles() {
        return titles_;
    }

    public void addTitle(final String title) {
        if (titles_ == null) titles_ = new ArrayList<String>();
        titles_.add(title);
    }

    public void clearTitles() {
        if (titles_ != null) titles_.clear();
    }

    public final List<String> getRoles() {
        return roles_;
    }

    public void addRole(final String role) {
        if (roles_ == null) roles_ = new ArrayList<String>();
        roles_.add(role);
    }

    public void clearRoles() {
        if (roles_ != null) roles_.clear();
    }

    public final List<String> getURLs() {
        return urls_;
    }

    public void addURL(final String url) {
        if (urls_ == null) urls_ = new ArrayList<String>();
        urls_.add(url);
    }

    public void clearURLs() {
        if (urls_ != null) urls_.clear();
    }

    public boolean isEmpty() {
        boolean empty = version_.isEmpty() && fullName_.isEmpty() && familyName_.isEmpty() && givenName_.isEmpty() && middleName_.isEmpty() && prefix_.isEmpty() && suffix_.isEmpty();
        empty &= photo_ == null || photo_.isEmpty();
        empty &= photoType_.isEmpty();
        empty &= nick_.isEmpty();
        empty &= birthday_ == null;
        empty &= unknownContent_.isEmpty();
        empty &= emailAddresses_ == null || emailAddresses_.isEmpty();
        empty &= telephones_ == null || telephones_.isEmpty();
        empty &= addresses_ == null || addresses_.isEmpty();
        empty &= addressLabels_ == null || addressLabels_.isEmpty();
        empty &= jids_ == null || jids_.isEmpty();
        empty &= description_.isEmpty();
        empty &= organizations_ == null || organizations_.isEmpty();
        empty &= titles_ == null || titles_.isEmpty();
        empty &= roles_ == null || roles_.isEmpty();
        empty &= urls_ == null || urls_.isEmpty();
        return empty;
    }

    public EMailAddress getPreferredEMailAddress() {
        for (final EMailAddress address : emailAddresses_) {
            if (address.isPreferred) {
                return address;
            }
        }
        if (emailAddresses_ != null && !emailAddresses_.isEmpty()) {
            return emailAddresses_.get(0);
        }
        return new EMailAddress();
    }

    public void addJID(JID jid) {
    	if (jids_ == null) jids_ = new ArrayList<JID>();
    	jids_.add(jid);
    }

    public List<Telephone> getTelephones() {
        return telephones_;
    }

}
