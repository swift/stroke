/*
 * Copyright (c) 2010-2011 Isode Limited.
 * All rights reserved.
 * See the COPYING file for more information.
 */
/*
 * Copyright (c) 2015 Tarun Gupta.
 * Licensed under the simplified BSD license.
 * See Documentation/Licenses/BSD-simplified.txt for more information.
 */

package com.isode.stroke.base;

import com.isode.stroke.base.NotNull;
import java.net.URLDecoder;
import java.io.UnsupportedEncodingException;

public class URL {

	private String scheme;
	private String user;
	private String password;
	private String host;
	private Integer port = null;
	private String path;
	private boolean empty;

	/**
	* Default constructor.
	*/
	public URL() {
		this("", "", "", "", null, "", true);
	}

	/**
	* Parameterized Constructor
	* @param scheme notnull
	* @param host notnull
	* @param path notnull
	*/
	public URL(String scheme, String host, String path) {
		this(scheme, "", "", host, null, path, false);
	}

	/**
	* Parameterized Constructor
	* @param scheme notnull
	* @param host notnull
	* @param port
	* @param path notnul
	*/
	public URL(String scheme, String host, int port, String path) {
		this(scheme, "", "", host, port, path, false);
	}

	/**
	* Parameterized Constructor
	* @param scheme notnull
	* @param user notnull
	* @param password notnull
	* @param host notnull
	* @param port
	* @param path notnul
	* @param empty notnull
	*/
	public URL(String scheme, String user, String password, String host, Integer port, String path, boolean empty) {
		NotNull.exceptIfNull(scheme, "scheme");
		NotNull.exceptIfNull(user, "user");
		NotNull.exceptIfNull(password, "password");
		NotNull.exceptIfNull(host, "host");
		NotNull.exceptIfNull(path, "path");
		NotNull.exceptIfNull(empty, "empty");
		this.scheme = scheme;
		this.user = user;
		this.password = password;
		this.host = host;
		this.port = port;
		this.path = path;
		this.empty = empty;
	}

	/**
	* Whether the URL is empty.
	*/
	public boolean isEmpty() {
		return empty;
	}

	/**
	* Scheme used for the URL (http, https etc.)
	*/
	public String getScheme() {
		return scheme;
	}

	/**
	* Hostname
	*/
	public String getHost() {
		return host;
	}

	/**
	* Port number
	*/
	public Integer getPort() {
		return port;
	}

	/**
	* Path
	*/
	public String getPath() {
		return path;
	}

	/**
	* @return Complete URL with all its parameters and format.
	*/
	public String toString() {
		if (empty) {
			return "";
		}
		String result = scheme + "://";
		if (!user.isEmpty()) {
			result += user;
			if (!password.isEmpty()) {
				result += ":" + password;
			}
			result += "@";
		}
		result += host;
		if (port != null) {
			result += ":";
			result += Integer.toString(port);
		}
		result += path;
		return result;
	}

	/**
	* @return port number based on scheme.
	*/
	public static int getPortOrDefaultPort(URL url) {
		NotNull.exceptIfNull(url, "url");
		if (url.getPort() != null) {
			return url.getPort();
		}
		else if (url.getScheme() == "http") {
			return 80;
		}
		else if (url.getScheme() == "https") {
			return 443;
		}
		else {
			System.err.println("Unknown scheme: " + url.getScheme());
			return 80;
		}
	}

	/**
	* @param URL String which has to be processed for extraction of parameters.
	* @return URL object after extracting all parameters from urlString.
	*/
	public static URL fromString(String urlString) {
		NotNull.exceptIfNull(urlString, "urlString");
		int colonIndex = urlString.indexOf(':');
		if (colonIndex == -1) {
			return new URL();
		}
		String scheme = urlString.substring(0, colonIndex);

		// Authority
		if (urlString.length() > colonIndex + 2 && urlString.charAt(colonIndex+1) == '/' && urlString.charAt(colonIndex+2) == '/') {
			int authorityIndex = colonIndex + 3;
			int slashIndex = urlString.indexOf('/', authorityIndex);
			String authority = "";
			String path = "";
			if (slashIndex == -1) {
				authority = urlString.substring(authorityIndex);
				path = "";
			}
			else {
				authority = urlString.substring(authorityIndex, slashIndex);
				path = unescape(urlString.substring(slashIndex));
			}

			int atIndex = authority.indexOf('@');
			String userInfo = "";
			String hostAndPort = "";
			if (atIndex != -1) {
				userInfo = authority.substring(0, atIndex);
				hostAndPort = authority.substring(atIndex + 1);
			}
			else {
				userInfo = "";
				hostAndPort = authority;
			}

			String host = "";
			Integer port = null;
			colonIndex = hostAndPort.indexOf(':');
			if (colonIndex != -1) {
				host = unescape(hostAndPort.substring(0, colonIndex));
				try {
					port = Integer.valueOf(hostAndPort.substring(colonIndex + 1));
				}
				catch (NumberFormatException e) {
					return new URL();
				}
			}
			else {
				host = unescape(hostAndPort);
			}

			if (port != null) {
				return new URL(scheme, host, port, path);
			}
			else {
				return new URL(scheme, host, path);
			}
		}
		else {
			// We don't support URLs without authorities yet
			return new URL();
		}
	}

	/**
	* @return URL String after decoding hexadecimal bits.
	*/
	public static String unescape(String str) {
		try {
			return URLDecoder.decode(str, "UTF-8");
		}
		catch(IllegalArgumentException e) {
			System.err.println(e.getMessage());
			return "";
		}
		catch(UnsupportedEncodingException e) {
			System.err.println(e.getMessage());
			return "";
		}
	}
}