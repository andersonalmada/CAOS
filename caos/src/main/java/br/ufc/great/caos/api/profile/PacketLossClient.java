/*******************************************************************************
 * Copyright (c) 2015 LG Electronics. All Rights Reserved. This software is the
 * confidential and proprietary information of LG Electronics. You shall not
 * disclose such Confidential Information and shall use it only in accordance
 * with the terms of the license agreement you entered into with LG Electronics.
 *******************************************************************************/
package br.ufc.great.caos.api.profile;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.ws4d.coap.connection.BasicCoapChannelManager;
import org.ws4d.coap.interfaces.CoapChannelManager;
import org.ws4d.coap.interfaces.CoapClient;
import org.ws4d.coap.interfaces.CoapClientChannel;
import org.ws4d.coap.interfaces.CoapRequest;
import org.ws4d.coap.interfaces.CoapResponse;
import org.ws4d.coap.messages.CoapRequestCode;

import android.util.Log;
import br.ufc.great.caos.api.Caos;
import br.ufc.great.caos.api.Ports;
import br.ufc.great.caos.api.util.Util;
import br.ufc.great.caos.transition.PacketLossData;

/**
 * Checks the reliability of the network related to coap messages exchanges
 * (UDP)
 */
public class PacketLossClient extends Thread implements Runnable, CaosService {

	private static PacketLossClient sPacketLossClient = null;
	private static boolean isRunning = false;

	BasicCoapClient client;

	private PacketLossClient() {
		client = new BasicCoapClient();
	}

	public static PacketLossClient getInstance() {
		if (sPacketLossClient == null) {
			sPacketLossClient = new PacketLossClient();
		}
		return sPacketLossClient;
	}

	public static boolean isRunning() {
		return isRunning;
	}

	@Override
	public void run() {
		isRunning = true;
		client.channelManager = BasicCoapChannelManager.getInstance();
		if (Util.hasConnection(Caos.getContext())) {
			client.sendPacket();
		}
	}

	private class BasicCoapClient implements CoapClient {
		CoapChannelManager channelManager = null;
		CoapClientChannel clientChannel = null;

		public void sendPacket() {
			Log.i("PacketLossProfile", "Starting sending...");
			for (int i = 0; i < 30; i++) {
				try {
					clientChannel = channelManager.connect(this, InetAddress.getByName(Caos.sCloudletIp),
							Ports.packetLoss);
					CoapRequest coapRequest = clientChannel.createRequest(true, CoapRequestCode.POST);

					PacketLossData clientPacket = new PacketLossData(i, Util.wifiIpAddress(Caos.getContext()));
					coapRequest.setPayload(Util.wrap(clientPacket));
					clientChannel.sendMessage(coapRequest);
				} catch (UnknownHostException e) {
					e.printStackTrace();
				}
			}
			isRunning = false;
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

	@Override
	public void shutDown() {
		Log.i("PacketLossProfile", "Stoping profiling...");
		isRunning = false;
		Thread.currentThread().interrupt();
	}
}