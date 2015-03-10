package com.isode.stroke.elements;

import com.isode.stroke.base.NotNull;
import java.lang.Comparable;

public class CapsInfo extends Payload implements Comparable<CapsInfo> {

	private String node_;
	private String version_;
	private String hash_;

	/**
	* Initialize private variables
	*/
	public void init(String node, String version, String hash) {
		NotNull.exceptIfNull(node, "node");
		NotNull.exceptIfNull(version, "version");
		NotNull.exceptIfNull(hash, "hash");
		this.node_ = node;
		this.version_ = version;
		this.hash_ = hash;
	}

	/**
	* CapsInfo();
	*/
	public CapsInfo() {
		init("", "", "sha-1");
	}

	/**
	* CapsInfo(node, "", "sha-1");
	*/
	public CapsInfo(String node) {
		init(node, "", "sha-1");
	}

	/**
	* CapsInfo(node, version, "sha-1");
	*/
	public  CapsInfo(String node, String version) {
		init(node, version, "sha-1");
	}

	/**
	 *
	 * @param node CapsInfo node, notnull.
	 * @param node CapsInfo version, notnull.
	 * @param node CapsInfo hash, notnull.
	 */
	public CapsInfo(String node, String version, String hash) {
		init(node, version, hash);
	}

	@Override
	public boolean equals(Object other) {
		
		if ((!(other instanceof CapsInfo)) || other == null) {
			return false;
		}
			
		CapsInfo guest = (CapsInfo) other;
		return 	(node_.equals(guest.node_)) && (version_.equals(guest.version_)) && (hash_.equals(guest.hash_));
	}

	@Override	 
	public int compareTo(CapsInfo other) {
		if(other == null) {
			return -1;
		}
		if (node_.equals(other.node_)) {
			if (version_.equals(other.version_)) {
				return hash_.compareTo(other.hash_);
			}
			else {
				return version_.compareTo(other.version_);
			}
		}
		else {
				return node_.compareTo(other.node_);
			}
	}

	/**
	*
	* @return Node, notnull.
	*/
	public String getNode() {
		return node_;
	}

	/**
	*
	* @param node, notnull.
	*/
	public void setNode(String node) {
		NotNull.exceptIfNull(node, "node");
		this.node_ = node;
	}

	/**
	*
	* @return version, notnull.
	*/
	public String getVersion() {
		return version_;
	}

	/**
	*
	* @param version, notnull.
	*/
	public void setVersion(String version) {
		NotNull.exceptIfNull(version, "version");
		this.version_ = version;
	}

	/**
	*
	* @return hash, notnull.
	*/
	public String getHash() {
		return hash_;
	}

	/**
	*
	* @param hash, notnull.
	*/
	public void setHash(String hash) {
		NotNull.exceptIfNull(hash, "hash");		
		this.hash_ = hash;
	}
}