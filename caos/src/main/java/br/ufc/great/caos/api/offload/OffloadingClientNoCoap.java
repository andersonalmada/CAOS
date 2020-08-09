/*******************************************************************************
 * Copyright (c) 2015 LG Electronics. All Rights Reserved. This software is the
 * confidential and proprietary information of LG Electronics. You shall not
 * disclose such Confidential Information and shall use it only in accordance
 * with the terms of the license agreement you entered into with LG Electronics.
 *******************************************************************************/
package br.ufc.great.caos.api.offload;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import android.os.StrictMode;
import br.ufc.great.caos.api.Caos;
import br.ufc.great.caos.api.Ports;

/**
 * OffloadingController based in a Restful implementation
 */
public class OffloadingClientNoCoap {

	// 0 position = timeUploadToOffloadingService
	// 1 position = uploadTimeVM
	// 2 position = offloadingExecutTime
	// 3 position = uploadResultOffloading
	// 4 position = finalTimeOffloading
	public static String[] timeOffloading = new String[5];

	private OffloadingClientNoCoap() {

	}

	/**
	 * Sends the invocableMethod to the cloud and waits for the answer
	 */
	public static Object sendObject(InvocableMethod invocableMethod) {

		useSocketInUIThread();
		OffloadingError offloadingError = new OffloadingError();
		try {

			Socket mSocket = new Socket(Caos.sCloudletIp, Ports.offloading);

			long timeTemp;

			ObjectOutputStream streamSender = new ObjectOutputStream(mSocket.getOutputStream());

			timeTemp = System.currentTimeMillis();
			streamSender.writeObject(invocableMethod);
			timeOffloading[0] = String.valueOf((System.currentTimeMillis() - timeTemp) / 1000.0);

			streamSender.flush();
			streamSender.reset();

			timeTemp = System.currentTimeMillis();

			InputStream in = mSocket.getInputStream();
			ObjectInputStream streamReceiver = new ObjectInputStream(in);

			// 0 position return OffloadingResult
			// 1 position return timesExecutions
			Object[] resultObject = null;

			resultObject = (Object[]) streamReceiver.readObject();

			timeOffloading[1] = (String) resultObject[1];
			timeOffloading[2] = (String) resultObject[2];
			timeOffloading[3] = String.valueOf(((System.currentTimeMillis() - timeTemp) / 1000.0)
					- (Double.parseDouble(timeOffloading[1]) + Double.parseDouble(timeOffloading[2])));
			timeOffloading[4] = String
					.valueOf(Double.parseDouble(timeOffloading[0]) + Double.parseDouble(timeOffloading[1])
							+ Double.parseDouble(timeOffloading[2]) + Double.parseDouble(timeOffloading[3]));

			streamReceiver.close();
			in.close();
			streamSender.close();
			mSocket.close();

			return resultObject[0];

		} catch (Exception e) {
			offloadingError.add("Exception, Problem with offloading.");
		} 

		return offloadingError;
	}

	/**
	 * Allows the socket to be executed on the main thread
	 */
	private static void useSocketInUIThread() {
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
	}
}