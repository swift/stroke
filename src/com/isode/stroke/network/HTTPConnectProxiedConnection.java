/*
 * Copyright (c) 2010-2011 Thilo Cestonaro
 * Licensed under the simplified BSD license.
 * See Documentation/Licenses/BSD-simplified.txt for more information.
 */
/*
 * Copyright (c) 2011-2016 Isode Limited.
 * All rights reserved.
 * See the COPYING file for more information.
 */
/*
 * Copyright (c) 2015 Tarun Gupta.
 * Licensed under the simplified BSD license.
 * See Documentation/Licenses/BSD-simplified.txt for more information.
 */

package com.isode.stroke.network;

import com.isode.stroke.base.SafeByteArray;
import com.isode.stroke.stringcodecs.Base64;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Vector;

public class HTTPConnectProxiedConnection extends ProxiedConnection {

	private SafeByteArray authID_;
	private SafeByteArray authPassword_;
	private HTTPTrafficFilter trafficFilter_;
	private StringBuffer httpResponseBuffer_ = new StringBuffer("");
	private final List<Pair> nextHTTPRequestHeaders_ = new ArrayList<Pair>();

	public static class Pair {
		String a;
		String b;

		public Pair(String a, String b) { this.a = a; this.b = b; }
	}

	public static HTTPConnectProxiedConnection create(DomainNameResolver resolver, ConnectionFactory connectionFactory, TimerFactory timerFactory, final String proxyHost, int proxyPort, final SafeByteArray authID, final SafeByteArray authPassword) {
		return new HTTPConnectProxiedConnection(resolver, connectionFactory, timerFactory, proxyHost, proxyPort, authID, authPassword);
	}

	public void setHTTPTrafficFilter(HTTPTrafficFilter trafficFilter) {
		trafficFilter_ = trafficFilter;
	}

	private HTTPConnectProxiedConnection(DomainNameResolver resolver, ConnectionFactory connectionFactory, TimerFactory timerFactory, final String proxyHost, int proxyPort, final SafeByteArray authID, final SafeByteArray authPassword) {
		super(resolver, connectionFactory, timerFactory, proxyHost, proxyPort);
		this.authID_ = authID;
		this.authPassword_ = authPassword;
	}

	protected void initializeProxy() {
	    httpResponseBuffer_.setLength(0);
	    
		StringBuffer connect = new StringBuffer();
		connect.append("CONNECT ").append(getServer().getAddress().toString()).append(":").append(getServer().getPort()).append(" HTTP/1.1\r\n");
		SafeByteArray data = new SafeByteArray(connect.toString());
		if (!authID_.isEmpty() && !authPassword_.isEmpty()) {
			data.append(new SafeByteArray("Proxy-Authorization: Basic "));
			SafeByteArray credentials = authID_;
			credentials.append(new SafeByteArray(":"));
			credentials.append(authPassword_);
			data.append(Base64.encode(credentials));
			data.append(new SafeByteArray("\r\n"));
		}
		else if (!nextHTTPRequestHeaders_.isEmpty()) {
		    for (Pair headerField : nextHTTPRequestHeaders_) {
		        data.append(headerField.a);
		        data.append(": ");
		        data.append(headerField.b);
		        data.append("\r\n");
		    }
		    nextHTTPRequestHeaders_.clear();
		}
		data.append(new SafeByteArray("\r\n"));
		write(data);
	}

	protected void handleProxyInitializeData(SafeByteArray data) {
		String dataString = data.toString();
		//SWIFT_LOG(debug) << data << std::endl;
		httpResponseBuffer_.append(dataString);
		
		Vector<Pair> headerFields = new Vector<Pair>();

		int headerEnd = httpResponseBuffer_.indexOf("\r\n\r\n", 0);
		if (headerEnd == -1) {
			if ((httpResponseBuffer_.length() > 4) && !(httpResponseBuffer_.substring(0, 4).equals("HTTP"))) {
				setProxyInitializeFinished(false);
			}
			return;
		}

		String statusLine = parseHTTPHeader(httpResponseBuffer_.substring(0, headerEnd), headerFields);

		if (trafficFilter_ != null) {
			Vector<Pair> newHeaderFields = trafficFilter_.filterHTTPResponseHeader(statusLine, headerFields);
			if (!newHeaderFields.isEmpty()) {
	            reconnect();
	            nextHTTPRequestHeaders_.clear();
	            nextHTTPRequestHeaders_.addAll(newHeaderFields);
	            return;
			}
		}

		String[] tmp = statusLine.split(" ");
		if (tmp.length > 1) {
			try {
				int status = Integer.parseInt(tmp[1]);
				//SWIFT_LOG(debug) << "Proxy Status: " << status << std::endl;
				if (status / 100 == 2) { // all 2XX states are OK
					setProxyInitializeFinished(true);
				}
				else {
					//SWIFT_LOG(debug) << "HTTP Proxy returned an error: " << httpResponseBuffer_ << std::endl;
					setProxyInitializeFinished(false);
				}
			}
			catch (NumberFormatException e) {
				//SWIFT_LOG(warning) << "Unexpected response: " << tmp[1] << std::endl;
				setProxyInitializeFinished(false);
			}
		}
		else {
			setProxyInitializeFinished(false);
		}
		httpResponseBuffer_ = new StringBuffer("");
	}

	private void sendHTTPRequest(final String statusLine, final Vector<Pair> headerFields) {
		StringBuffer request = new StringBuffer();

		request.append(statusLine).append("\r\n");
		for (final Pair field : headerFields) {
			request.append(field.a).append(":").append(field.b).append("\r\n");
		}
		request.append("\r\n");
		write(new SafeByteArray(request.toString()));
	}

	private String parseHTTPHeader(final String data, Vector<Pair> headerFields) {
	    Scanner dataScanner = new Scanner(data);

	    String statusLine = "";
		// parse status line
	    if (dataScanner.hasNext()) {
	        statusLine = dataScanner.nextLine();
	    }
		

		// parse fields
		while (dataScanner.hasNext()) {
		    String headerLine = dataScanner.nextLine();
		    if (headerLine.equals("\r")) {
		        break;
		    }
			int splitIndex = headerLine.indexOf(':', 0);
			if (splitIndex != -1) {
				headerFields.add(new Pair(headerLine.substring(0, splitIndex), headerLine.substring(splitIndex + 1)));
			}
		}
		
		return statusLine;
	}

}