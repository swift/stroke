/*
 * Copyright (c) 2010-2012, Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2010, Remko Tronçon.
 * All rights reserved.
 */
package com.isode.stroke.jid;

import com.ibm.icu.text.StringPrep;
import com.ibm.icu.text.StringPrepParseException;
import java.util.logging.Level;
import java.util.logging.Logger;

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
public class JID {
    public enum CompareType {
        WithResource, WithoutResource
    };

    private final String node_;
    private final String domain_;
    private final String resource_;

    /**
     * Create an invalid JID.
     */
    public JID() {
        this("", "", null);
    }

    /**
     * Create a JID using the JID(String) constructor.
     * @param jid String formatted JID.
     */
    public static JID fromString(final String jid) {
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
    public JID(final String jid) {
        //FIXME: This doesn't nameprep!
        if (jid == null || jid.startsWith("@")) {
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
        StringPrep nodePrep = StringPrep.getInstance(StringPrep.RFC3491_NAMEPREP);
        StringPrep domainPrep = StringPrep.getInstance(StringPrep.RFC3920_NODEPREP);
        StringPrep resourcePrep = StringPrep.getInstance(StringPrep.RFC3920_RESOURCEPREP);
        try {
            node = nodePrep.prepare(node, StringPrep.DEFAULT);
            domain = domainPrep.prepare(domain, StringPrep.DEFAULT);
            resource = resource != null ? resourcePrep.prepare(resource, StringPrep.DEFAULT) : null;
        } catch (StringPrepParseException ex) {
            node = "";
            domain = "";
            resource = "";
        }
        node_ = node;
        domain_ = domain;
        resource_ = resource;
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
    public JID(final String node, final String domain) {
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
    public JID(final String node, final String domain, final String resource) {
        StringPrep nodePrep = StringPrep.getInstance(StringPrep.RFC3491_NAMEPREP);
        StringPrep domainPrep = StringPrep.getInstance(StringPrep.RFC3920_NODEPREP);
        StringPrep resourcePrep = StringPrep.getInstance(StringPrep.RFC3920_RESOURCEPREP);
        String preppedNode;
        String preppedDomain;
        String preppedResource;
        try {
            preppedNode = nodePrep.prepare(node, StringPrep.DEFAULT);
            preppedDomain = domainPrep.prepare(domain, StringPrep.DEFAULT);
            preppedResource = resource != null ? resourcePrep.prepare(resource, StringPrep.DEFAULT) : null;
        } catch (StringPrepParseException ex) {
            preppedNode = "";
            preppedDomain = "";
            preppedResource = "";
        }
        node_ = preppedNode;
        domain_ = preppedDomain;
        resource_ = preppedResource;

    }

    /**
     * @return Is a correctly-formatted JID.
     */
    public boolean isValid() {
        return (domain_.length()!=0);
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
     * @return null for bare JIDs.
     */
    public String getResource() {
        return resource_ != null ? resource_ : "";
    }

    /**
     * Is a bare JID, i.e. has no resource part.
     */
    public boolean isBare() {
        return resource_ == null;
    }

    /**
     * Get the JID without a resource.
     * @return non-null. Invalid if the original is invalid.
     */
    public JID toBare() {
        return new JID(getNode(), getDomain());
    }

    @Override
    public String toString() {
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
    public boolean equals(final Object otherObject) {
        if (otherObject == null || getClass() != otherObject.getClass()) {
            return false;
        }
        if (otherObject == this) {
            return true;
        }
        JID other = (JID)otherObject;
        String node1 = getNode();
        String node2 = other.getNode();
        String domain1 = getDomain();
        String domain2 = other.getDomain();
        String resource1 = getResource();
        String resource2 = other.getResource();
        boolean nodeMatch = (node1 == null && node2 == null) || (node1 != null && node1.equals(node2));
        boolean domainMatch = (domain1 == null && domain2 == null) || (domain1 != null && domain1.equals(domain2));
        boolean resourceMatch = (resource1 == null && resource2 == null) || (resource1 != null && resource1.equals(resource2));
        return nodeMatch && domainMatch && resourceMatch;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 73 * hash + (this.node_ != null ? this.node_.hashCode() : 0);
        hash = 73 * hash + (this.domain_ != null ? this.domain_.hashCode() : 0);
        hash = 73 * hash + (this.resource_ != null ? this.resource_.hashCode() : 0);
        return hash;
    }
}
