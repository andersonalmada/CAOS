/*******************************************************************************
 * Copyright (c) 2015 LG Electronics. All Rights Reserved. This software is the
 * confidential and proprietary information of LG Electronics. You shall not
 * disclose such Confidential Information and shall use it only in accordance
 * with the terms of the license agreement you entered into with LG Electronics.
 *******************************************************************************/
package br.ufc.great.caos.api.profile;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Random;

import org.ws4d.coap.connection.BasicCoapChannelManager;
import org.ws4d.coap.interfaces.CoapChannelManager;
import org.ws4d.coap.interfaces.CoapClient;
import org.ws4d.coap.interfaces.CoapClientChannel;
import org.ws4d.coap.interfaces.CoapRequest;
import org.ws4d.coap.interfaces.CoapResponse;
import org.ws4d.coap.messages.CoapRequestCode;

import android.net.TrafficStats;
import android.util.Log;
import br.ufc.great.caos.api.Caos;
import br.ufc.great.caos.api.Ports;
import br.ufc.great.caos.api.util.Util;
import br.ufc.great.caos.transition.ThroughputResultData;

/**
 * This class calculates the download and upload rate and sends this piece of
 * information to the cloudlet
 */
public class ThroughputClient implements Runnable, CaosService {

	BasicCoapClient client;

	public String downloadRate;
	public String uploadRate;

	private static boolean isRunning = false;

	public static boolean isRunning() {
		return isRunning;
	}

	private static ThroughputClient sThroughputClient = null;

	private ThroughputClient() {
		client = new BasicCoapClient();
	}

	public static ThroughputClient getInstance() {
		if (sThroughputClient == null) {
			sThroughputClient = new ThroughputClient();
		}
		return sThroughputClient;
	}

	public void run() {
		try {
			Socket serverSocket = new Socket(Caos.sCloudletIp, Ports.throughput);
			serverSocket.setSendBufferSize(65536);
			serverSocket.setReceiveBufferSize(65536);

			OutputStream toServer = serverSocket.getOutputStream();
			InputStream fromServer = serverSocket.getInputStream();

			byte randomData[] = new byte[1024 * 32];
			new Random().nextBytes(randomData);
			byte[] buffer = new byte[1024 * 4];

			// Send bytes
			long totalTxBeforeTest = TrafficStats.getTotalTxBytes();
			long beforeTime = System.currentTimeMillis();

			while (System.currentTimeMillis() < (beforeTime + 7000)) {
				toServer.write(randomData);
			}

			long totalTxAfterTest = TrafficStats.getTotalTxBytes();
			long totalTime = System.currentTimeMillis() - beforeTime;
			double txDiff = totalTxAfterTest - totalTxBeforeTest;
			double uploadRate = (txDiff / (totalTime / 1000));

			toServer.write("[END_UP]".getBytes());

			// Receive bytes
			long totalRxBeforeTest = TrafficStats.getTotalRxBytes();
			beforeTime = System.currentTimeMillis();

			while ((fromServer.read(buffer)) != -1) {
				String buf = new String(buffer);
				if (buf.indexOf("[END_DW]") != -1) {
					break;
				}
				Arrays.fill(buffer, (byte) 0);
			}
			long totalRxAfterTest = TrafficStats.getTotalRxBytes();
			totalTime = System.currentTimeMillis() - beforeTime;
			double rxDiff = totalRxAfterTest - totalRxBeforeTest;
			double downloadRate = (rxDiff / (totalTime / 1000));
			ProfileMonitor.setDownloadRate(downloadRate);
			ProfileMonitor.setUploadRate(uploadRate);

			toServer.close();
			fromServer.close();
			serverSocket.close();

			client.channelManager = BasicCoapChannelManager.getInstance();
			if (Util.hasConnection(Caos.getContext())) {
				client.sendResult(new ThroughputResultData(downloadRate, uploadRate, Util.getIp()));
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private class BasicCoapClient implements CoapClient {
		CoapChannelManager channelManager = null;
		CoapClientChannel clientChannel = null;

		public void sendResult(ThroughputResultData result) {
			try {
				clientChannel = channelManager.connect(this, InetAddress.getByName(Caos.sCloudletIp),
						Ports.throughput + 1);
				CoapRequest coapRequest = clientChannel.createRequest(true, CoapRequestCode.POST);

				coapRequest.setPayload(Util.wrap(result));
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

	@Override
	public void shutDown() {
		Log.i("ThroughputProfile", "Stoping profiling...");
		Thread.currentThread().interrupt();
	}

}
