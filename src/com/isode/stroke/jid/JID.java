/*
 * Copyright (c) 2010-2012, Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2010, Remko Tronçon.
 * All rights reserved.
 */
/*
 * Copyright (c) 2015 Tarun Gupta.
 * Licensed under the simplified BSD license.
 * See Documentation/Licenses/BSD-simplified.txt for more information.
 */

package com.isode.stroke.jid;

import com.isode.stroke.idn.ICUConverter;
import com.isode.stroke.idn.IDNConverter;
import com.ibm.icu.text.StringPrepParseException;
import com.isode.stroke.base.NotNull;
import java.util.Arrays;
import java.util.List;

/**
 * JID helper.
 *
 * This represents the JID used in XMPP
 * (RFC6120 - http://tools.ietf.org/html/rfc6120 particularly section 1.4),
 * further defined in XMPP Address Format (http://tools.ietf.org/html/rfc6122 ).
 * For a description of format, see the RFC or page 14 of
 * XMPP: The Definitive Guide (Saint-Andre et al.)
 *
 * Particularly - a Bare JID is a JID without a resource part.
 *
 * Note that invalid JIDs shouldn't have any calls made to them beyond isValid().
 *
 * JIDs may be invalid if received over the wire, and should be checked with {@link JID#isValid}
 * before they're used.
 *
 * <p>
 * This is an immutable class.
 */
public class JID implements Comparable<JID> {
	public enum CompareType {
		WithResource, WithoutResource
	};

	private boolean valid_ = true;
	private boolean hasResource_ = true;
	private String node_ = "";
	private String domain_ = "";
	private String resource_ = "";
	private static IDNConverter idnConverter = new ICUConverter();

	/**
	 * Create an invalid JID.
	 */
	public JID() {
		this("", "", null);
	}

	/**
	 * Create a JID using the JID(String) constructor.
	 * @param jid String formatted JID, not null
	 * @return Jabber ID, not null
	 */
	public static JID fromString(String jid) {
		NotNull.exceptIfNull(jid, "jid");
		return new JID(jid);
	}

	/**
	 * Create a JID from its String representation.
	 *
	 * e.g.
	 * wonderland.lit
	 * wonderland.lit/rabbithole
	 * alice@wonderland.lit
	 * alice@wonderland.lit/TeaParty
	 *
	 * @param jid String representation. Invalid JID if null or invalid.
	 */
	public JID(String jid) {
		NotNull.exceptIfNull(jid, "jid");		
		valid_ = true;
		initializeFromString(jid);
	}

	private void initializeFromString(String jid) {
		if (jid.startsWith("@")) {
			valid_ = false;
			node_ = "";
			domain_ = "";
			resource_ = "";
			return;
		}

		String bare;
		String resource;
		String[] parts = jid.split("/", 2);
		if (parts.length > 1) {
			bare = parts[0];
			resource = parts[1];
		} else {
			hasResource_ = false;
			resource = null;
			bare = jid;
		}
		String[] nodeAndDomain = bare.split("@", 2);
		String node;
		String domain;
		if (nodeAndDomain.length == 1) {
			node = "";
			domain = nodeAndDomain[0];
		} else {
			node = nodeAndDomain[0];
			domain = nodeAndDomain[1];
		}
		nameprepAndSetComponents(node, domain, resource);
	}

	/**
	 * Create a bare JID from the node and domain parts.
	 *
	 * JID("node@domain") == JID("node", "domain")
	 * Use a different constructor instead of passing nulls.
	 *
	 * @param node JID node part.
	 * @param domain JID domain part.
	 */
	public JID(String node, String domain) {
		this(node, domain, null);
	}

	/**
	 * Create a bare JID from the node, domain and resource parts.
	 *
	 * JID("node@domain/resource") == JID("node", "domain", "resource")
	 * Use a different constructor instead of passing nulls.
	 *
	 * @param node JID node part.
	 * @param domain JID domain part.
	 * @param resource JID resource part.
	 */
	public JID(String node, String domain, String resource) {
		NotNull.exceptIfNull(node, "node");
		NotNull.exceptIfNull(domain, "domain");
		valid_ = true;
		hasResource_ = (resource != null);
		nameprepAndSetComponents(node, domain, resource);
	}

	private void nameprepAndSetComponents(String node, String domain, String resource) {
		if (domain.isEmpty() || idnConverter.getIDNAEncoded(domain) == null) {
			valid_ = false;
			return;
		}

		try {
			node_ = idnConverter.getStringPrepared(node, IDNConverter.StringPrepProfile.XMPPNodePrep);
			domain_ = idnConverter.getStringPrepared(domain, IDNConverter.StringPrepProfile.NamePrep);
			resource_ = resource != null ? idnConverter.getStringPrepared(resource, IDNConverter.StringPrepProfile.XMPPResourcePrep) : null;
		} catch (StringPrepParseException e) {
			valid_ = false;
			return;
		}
		if (domain_.isEmpty()) {
			valid_ = false;
			return;
		}
	}

	/**
	 * @return Is a correctly-formatted JID.
	 */
	public boolean isValid() {
		return valid_;
	}

	/**
	 * e.g. JID("node@domain").getNode() == "node"
	 * @return Node, or null for nodeless JIDs.
	 */
	public String getNode() {
		return node_;
	}

	/**
	 * e.g. JID("node@domain").getDomain() == "domain"
	 * @return only null for invalid JIDs.
	 */
	public String getDomain() {
		return domain_;
	}

	/**
	 * e.g. JID("node@domain/resource").getResource() == "resource"
	 * @return "" for bare JIDs.
	 */
	public String getResource() {
		return resource_ != null ? resource_ : "";
	}

	/**
	 * Is a bare JID, i.e. has no resource part.
	 * @return true if the resource part of JID is null, false if not
	 */
	public boolean isBare() {
		return !hasResource_;
	}

	/**
	 * Get the JID without a resource.
	 * @return non-null. Invalid if the original is invalid.
	 */
	public JID toBare() {
		return new JID(getNode(), getDomain());
	}

	static List<Character> escapedChars = Arrays.asList(' ', '"', '&', '\'', '/', '<', '>', '@', ':');
	private static String getEscaped(char c) {
		return "\\" + Integer.toHexString((int)c);
	}

	private static boolean getEscapeSequenceValue(String sequence) {
		try {
			int v = Integer.parseInt(sequence, 16);
			char value = (char)(v);
			return ((value == 0x5C) || escapedChars.contains(value));		
		} catch (NumberFormatException e) {
			return false;
		}
	}

	/**
	 * Returns the given node, escaped according to XEP-0106.
	 * The resulting node is a valid node for a JID, whereas the input value can contain characters
	 * that are not allowed.
	 */
	public static String getEscapedNode(String node) {
		String result = "";
		int startIndex = 0;

		for(char i : node.toCharArray()) {
			if(escapedChars.contains(i)) {
				result += getEscaped(i);
				continue;
			}
			else if (i == '\\') {
				int index = node.indexOf(i, startIndex);
				startIndex = index + 1;
				// Check if we have an escaped dissalowed character sequence
				int innerBegin = index + 1;
				if(innerBegin < node.length() && (innerBegin + 1) < node.length()) {
					int innerEnd = innerBegin + 2;
					String subs = node.substring(innerBegin, innerEnd);
					if(getEscapeSequenceValue(subs)) {
						result += getEscaped(i);
						continue;
					}
				}
			}
			result += i;
		}
		return result;
	}

	/**
	 * Returns the node of the current JID, unescaped according to XEP-0106.
	 */
	public String getUnescapedNode() {
		String result = "";
		int len = node_.length();
		for(int j = 0; j != len;) {
			if (node_.charAt(j) == '\\') {
				int innerEnd = j + 1;
				for (int i = 0; i < 2 && innerEnd != node_.length(); ++i, ++innerEnd) {

				}
				char value;
				String subs = node_.substring(j+1, innerEnd);
				if (getEscapeSequenceValue(subs)) { 
					int x = Integer.parseInt(subs, 16);
					value = (char)(x);
					result += value;
					j = innerEnd;
					continue;
				}
			}
			result += node_.charAt(j);
			++j;
		}
		return result;
	}

	/**
	 * Get the full JID with the supplied resource.
	 */
	public JID withResource(String resource) {
		return new JID (this.getNode(), this.getDomain(), resource);
	}

	public void setIDNConverter(IDNConverter converter) {
		idnConverter = converter;
	}

	@Override
	public String toString() {
        if (!valid_) {
            return "";
        }
		String string = new String();
		if (node_.length()!=0) {
			string += node_ + "@";
		}
		string += domain_;
		if (!isBare()) {
			string += "/" + resource_;
		}
		return string;
	}

	@Override
	public boolean equals(Object otherObject) {
    	if (!(otherObject instanceof JID)) {
    	  	return false;
    	}
    	return (compare((JID)otherObject, CompareType.WithResource) == 0);
 	}

	/**
	 * Compare two Jabber IDs by either including the resource part or
	 * excluding it
	 * @param o other JID to which this JID should be compared, can be null
	 * @param compareType comparison type
	 * @return 0 if equal, 1 if other JID is greater or -1 if this JID is greater
	 */
	public int compare(JID o, CompareType compareType) {
		if(this == o) return 0;
		if(o == null) return 1;
		if (!node_ .equals(o.node_)) {
			return node_.compareTo(o.node_);
		}
		if (!domain_ .equals(o.domain_)) {
			return domain_.compareTo(o.domain_);
		}
		if (compareType == CompareType.WithResource) {
			String res1 = resource_;
			String res2 = o.resource_;
			if(res1 != null && res2 != null) {
				return res1.compareTo(res2);
			}
			if(res1 == null && res2 == null) { return 0; }
            if (res1 == null) { return -1; }
            if (res2 == null) { return 1; }
		}
		return 0;
	}


	@Override
	public int hashCode() {
		int hash = 5;
		hash = 73 * hash + (this.node_ != null ? this.node_.hashCode() : 0);
		hash = 73 * hash + (this.domain_ != null ? this.domain_.hashCode() : 0);
		hash = 73 * hash + (this.resource_ != null ? this.resource_.hashCode() : 0);
		return hash;
	}

	@Override
	public int compareTo(JID o) {
		return compare(o, CompareType.WithResource);
	}

    /**
    * Returns a JID object corresponding to the given
    * String, or null if the String does not represent a valid JID
    * @param s value to be parsed
    * @return a JID, or null if s does not parse as a valid JID
    */
	public JID parse(String s) {
		JID jid = new JID(s);
		return jid.isValid() ? jid : null;
	}
}
