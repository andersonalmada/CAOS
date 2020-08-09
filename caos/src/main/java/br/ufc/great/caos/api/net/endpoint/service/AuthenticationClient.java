/*******************************************************************************
 * Copyright (c) 2015 LG Electronics. All Rights Reserved. This software is the
 * confidential and proprietary information of LG Electronics. You shall not
 * disclose such Confidential Information and shall use it only in accordance
 * with the terms of the license agreement you entered into with LG Electronics.
 *******************************************************************************/
package br.ufc.great.caos.api.net.endpoint.service;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.ws4d.coap.connection.BasicCoapChannelManager;
import org.ws4d.coap.interfaces.CoapChannelManager;
import org.ws4d.coap.interfaces.CoapClient;
import org.ws4d.coap.interfaces.CoapClientChannel;
import org.ws4d.coap.interfaces.CoapRequest;
import org.ws4d.coap.interfaces.CoapResponse;
import org.ws4d.coap.messages.CoapRequestCode;

import br.ufc.great.caos.api.Caos;
import br.ufc.great.caos.api.Ports;
import br.ufc.great.caos.api.util.Util;
import br.ufc.great.caos.api.util.device.Device;
import br.ufc.great.caos.transition.AuthenticationData;

/**
 * The authentication basically sends device information to the Server
 */
public class AuthenticationClient {

	BasicCoapClient client;

	private static AuthenticationClient sAuthenticationClient = null;

	private AuthenticationClient() {
		client = new BasicCoapClient();
	}

	public static AuthenticationClient getInstance() {
		if (sAuthenticationClient == null) {
			sAuthenticationClient = new AuthenticationClient();
		}

		return sAuthenticationClient;
	}

	private synchronized void renewSync() {
		if (Util.hasConnection(Caos.getContext())) {
			client.sendAuthenticationData();
		}
	}

	public void renew() {

		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				renewSync();
			}
		};
		new Thread(runnable).start();
	}

	private class BasicCoapClient implements CoapClient {
		CoapChannelManager channelManager = null;
		CoapClientChannel clientChannel = null;

		public void sendAuthenticationData() {
			try {
				client.channelManager = BasicCoapChannelManager.getInstance();
				clientChannel = channelManager.connect(this, InetAddress.getByName(Caos.sCloudletIp),
						Ports.authentication);
				CoapRequest coapRequest = clientChannel.createRequest(true, CoapRequestCode.POST);

				Device device = new Device(Caos.getContext());
				AuthenticationData authenticationData = device.getBean();
				authenticationData.setDeviceIp(Util.getIp());

				coapRequest.setPayload(Util.wrap(authenticationData));

				clientChannel.sendMessage(coapRequest);
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void onConnectionFailed(CoapClientChannel channel, boolean notReachable, boolean resetByServer) {
			System.out.println("Connection Failed");
		}

		@Override
		public void onResponse(CoapClientChannel channel, CoapResponse response) {
			System.out.println("Received response");
		}
	}
}
