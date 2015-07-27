/*
 * Copyright (c) 2010-2011 Thilo Cestonaro
 * Licensed under the simplified BSD license.
 * See Documentation/Licenses/BSD-simplified.txt for more information.
 */
/*
 * Copyright (c) 2014-2015 Isode Limited.
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
import java.net.InetAddress;

public class SOCKS5ProxiedConnection extends ProxiedConnection {

	private enum ProxyState {
		Initial,
		ProxyAuthenticating,
		ProxyConnecting
	}

	private ProxyState proxyState_;

	public static SOCKS5ProxiedConnection create(DomainNameResolver resolver, ConnectionFactory connectionFactory, TimerFactory timerFactory, final String proxyHost, int proxyPort) {
		return new SOCKS5ProxiedConnection(resolver, connectionFactory, timerFactory, proxyHost, proxyPort);
	}

	private SOCKS5ProxiedConnection(DomainNameResolver resolver, ConnectionFactory connectionFactory, TimerFactory timerFactory, final String proxyHost, int proxyPort) {
		super(resolver, connectionFactory, timerFactory, proxyHost, proxyPort);
		this.proxyState_ = ProxyState.Initial;
	}

	protected void initializeProxy() {
		proxyState_ = ProxyState.ProxyAuthenticating;
		SafeByteArray socksConnect = new SafeByteArray();
		socksConnect.append((byte)0x05); // VER = SOCKS5 = 0x05
		socksConnect.append((byte)0x01); // Number of authentication methods after this byte.
		socksConnect.append((byte)0x00); // 0x00 == no authentication
		// buffer.append(0x01); // 0x01 == GSSAPI 
		// buffer.append(0x02); // 0x02 ==  Username/Password
		// rest see RFC 1928 (http://tools.ietf.org/html/rfc1928)
		write(socksConnect);
	}

	public static boolean is_v4(InetAddress ia) {
		byte[] address = ia.getAddress();
		if (address.length == 4) return true;
		else return false;
  	}
	public static boolean is_v6(InetAddress ia) {
		byte[] address = ia.getAddress();
		if (address.length == 16) return true;
		else return false;
  	}

	protected void handleProxyInitializeData(SafeByteArray data) {
		SafeByteArray socksConnect = new SafeByteArray();
		InetAddress rawAddress = getServer().getAddress().getInetAddress();
		assert(is_v4(rawAddress) || is_v6(rawAddress));

		if (ProxyState.ProxyAuthenticating.equals(proxyState_)) {
			//SWIFT_LOG(debug) << "ProxyAuthenticating response received, reply with the connect BYTEs" << std::endl;
			byte choosenMethod = ((byte)data.getData()[1]);
			if (data.getData()[0] == (byte)0x05 && choosenMethod != (byte)0xFF) {
				switch(choosenMethod) { // use the correct Method
					case (byte)0x00:
						try {
							proxyState_ = ProxyState.ProxyConnecting;
							socksConnect.append((byte)0x05); // VER = SOCKS5 = 0x05
							socksConnect.append((byte)0x01); // Construct a TCP connection. (CMD)
							socksConnect.append((byte)0x00); // reserved.
							socksConnect.append(is_v4(rawAddress) ? (byte)0x01 : (byte)0x04); // IPv4 == 0x01, Hostname == 0x02, IPv6 == 0x04. (ATYP)
							int size = rawAddress.getAddress().length;
							for (int s = 0; s < size; s++) {
								byte uc;
								uc = rawAddress.getAddress()[s];
								socksConnect.append(uc);
						
							}
							socksConnect.append((byte)((getServer().getPort() >> 8) & 0xFF)); // highbyte of the port.
							socksConnect.append((byte)(getServer().getPort() & 0xFF)); // lowbyte of the port.
							write(socksConnect);
							return;
						}
						catch(Exception e) {
							System.err.println("exception caught");
						}
						write(socksConnect);
						break;
					default:
						setProxyInitializeFinished(true);
						break;
				}
				return;
			}
			setProxyInitializeFinished(false);
		}
		else if (ProxyState.ProxyConnecting.equals(proxyState_)) {
			//SWIFT_LOG(debug) << "Connect response received, check if successfully." << std::endl;
			//SWIFT_LOG(debug) << "Errorbyte: 0x" << std::hex << static_cast<int> ((*data)[1]) << std::dec << std::endl;
			/*

			data.at(1) can be one of the following:
			0x00 	succeeded
			0x01 	general SOCKS server failure
			0x02 	connection not allowed by ruleset
			0x03 	Network unreachable
			0x04 	Host unreachable
			0x05 	Connection refused
			0x06 	TTL expired
			0x07 	Command not supported (CMD)
			0x08 	Address type not supported (ATYP)
			0x09 bis 0xFF 	unassigned
			*/
			if (data.getData()[0] == 0x05 && data.getData()[1] == 0x0) {
				//SWIFT_LOG(debug) << "Successfully connected the server via the proxy." << std::endl;
				setProxyInitializeFinished(true);
			}
			else {
				//std::cerr << "SOCKS Proxy returned an error: " << std::hex << (*data)[1] << std::endl;
				setProxyInitializeFinished(false);
			}
		}
	}
}