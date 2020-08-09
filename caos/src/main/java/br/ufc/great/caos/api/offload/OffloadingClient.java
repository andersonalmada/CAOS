/*******************************************************************************
 * Copyright (c) 2015 LG Electronics. All Rights Reserved. This software is the
 * confidential and proprietary information of LG Electronics. You shall not
 * disclose such Confidential Information and shall use it only in accordance
 * with the terms of the license agreement you entered into with LG Electronics.
 *******************************************************************************/
package br.ufc.great.caos.api.offload;

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
import br.ufc.great.caos.transition.AckData;

/**
 * After sending the offloading request, it's needed to observe the
 * waitingForResponse variable to make sure that the answer has arrived
 */
public class OffloadingClient {

	private static Object resultObject;
	private static Boolean waitingForResponse = false;
	

	private BasicCoapClient client;

	private static OffloadingClient sOffloadingClient = null;

	private OffloadingClient() {

		OffloadingClient.resultObject = null;

		client = new BasicCoapClient();
		client.channelManager = BasicCoapChannelManager.getInstance();


	}



	public static OffloadingClient getInstance() {
		if (sOffloadingClient == null) {
			sOffloadingClient = new OffloadingClient();
		}
		return sOffloadingClient;
	}

	public Object sendOffloadingRequest(InvocableMethod invocableMethod) {
		OffloadingClient.waitingForResponse = true;
		if (Util.hasConnection(Caos.getContext())) {
			client.sendInvocableMethod(invocableMethod);
		}
		try {
			while (waitingForResponse) {
				Thread.sleep(5);
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return resultObject;
	}

	private class BasicCoapClient implements CoapClient {
		CoapChannelManager channelManager = null;
		CoapClientChannel clientChannel = null;

		public void sendInvocableMethod(InvocableMethod invocableMethod) {
			try {
				clientChannel = channelManager.connect(this, InetAddress.getByName(Caos.sCloudletIp), Ports.offloading);
				CoapRequest coapRequest = clientChannel.createRequest(true, CoapRequestCode.POST);
				coapRequest.setPayload(Util.wrap(invocableMethod));
				clientChannel.sendMessage(coapRequest);
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void onConnectionFailed(CoapClientChannel channel, boolean notReachable, boolean resetByServer) {
			OffloadingError offloadingError = new OffloadingError();
			offloadingError.add("Could not connect to the server");
			OffloadingClient.resultObject = offloadingError;
			OffloadingClient.waitingForResponse = false;
		}

		@Override
		public void onResponse(CoapClientChannel channel, CoapResponse response) {
			System.out.println("Received response");

			OffloadingError offloadingError = new OffloadingError();
			Object resultObject = null;
			try {
				resultObject = Util.unwrap(response.getPayload());
				if (resultObject instanceof AckData) {
					return;
				}

				OffloadingClient.resultObject = resultObject;
				OffloadingClient.waitingForResponse = false;

			} catch (Exception e) {
				offloadingError.add("Exception, to unwrap object from server.");
				resultObject = offloadingError;
			}
		}
	}
}