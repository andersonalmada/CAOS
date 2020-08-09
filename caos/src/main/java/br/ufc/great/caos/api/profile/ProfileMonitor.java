/*******************************************************************************
 * Copyright (c) 2015 LG Electronics. All Rights Reserved. This software is the
 * confidential and proprietary information of LG Electronics. You shall not
 * disclose such Confidential Information and shall use it only in accordance
 * with the terms of the license agreement you entered into with LG Electronics.
 *******************************************************************************/
package br.ufc.great.caos.api.profile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.DetailedState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.CellInfoGsm;
import android.telephony.CellSignalStrengthGsm;
import android.telephony.TelephonyManager;
import android.util.Log;
import br.ufc.great.caos.api.Caos;
import br.ufc.great.caos.api.offload.InvocableMethod;
import br.ufc.great.caos.api.util.LocationListenerAdapter;
import br.ufc.great.caos.api.util.LocationTracker;
import br.ufc.great.caos.api.util.Util;
import br.ufc.great.caos.transition.ProfileSyncData;

/**
 * Main class responsible for gathering all the monitoring information and after
 * sent to the Cloud.
 */
public final class ProfileMonitor {

	private static Map<String, ProfileSyncData> sLastLocalExectime = new HashMap<String, ProfileSyncData>();
	private static ExecutorService sMonitoringExecutor = Executors.newFixedThreadPool(1);
	private static Map<Short, Object> sMonitorContext = new HashMap<Short, Object>();

	private static boolean sIsMonitoringRunning = false;
	private static ThroughputClient sThroughputProfile;
	private static PacketLossClient sPacketLossProfile;
	private static RttClient sRttProfile;
	private static ProfileSyncClient sProfileSync;

	private static Context sContext = Caos.getContext();
	public static String sFreeMemory;
	public static String sTotalMemory;
	public static String sMaxHeap;
	public static String sCurrentHeap;
	public static String sFreeHeap;
	public static String sLatitude;
	public static String sLongitude;

	private ProfileMonitor() {
		// Exists only to defeat instantiation.
	}

	public static void initProfiling() {
		if (!sIsMonitoringRunning) {
			updateMemoryInfo();
			updateHeapInfo();
			sThroughputProfile = ThroughputClient.getInstance();
			sPacketLossProfile = PacketLossClient.getInstance();
			sRttProfile = RttClient.getInstance();
			sProfileSync = ProfileSyncClient.getInstance();
			startMonitoring();
		}
	}

	private static void updateMonitor() {
		Log.i("ProfileMonitor", "Updating Monitor values...");
		updateMemoryInfo();
		updateHeapInfo();
	}

	private static void startMonitoring() {
		sMonitoringExecutor.execute(new Runnable() {
			@Override
			public void run() {
				sIsMonitoringRunning = true;
				try {
					while (isMonitoringRunning()) {
						if (Thread.interrupted()) {
							return;
						}
						updateMonitor();
						if (!ThroughputClient.isRunning()) {
							sThroughputProfile.run();
						}
						if (!PacketLossClient.isRunning()) {
							sPacketLossProfile.run();
						}
						if (!RttClient.isRunning()) {
							sRttProfile.run();
						}
						if (!ProfileSyncClient.isRunning()) {
							sProfileSync.run();
						}

						Thread.sleep(60 * 1000);
					}
				} catch (InterruptedException e) {
					System.out.println(e.getMessage());
				}
			}
		});
	}

	public static synchronized void cleanLastLocalExectime() {
		sLastLocalExectime.clear();
	}

	public static synchronized boolean isMonitoringRunning() {
		return sIsMonitoringRunning;
	}

	public static synchronized void stopMonitoring() {
		Thread.currentThread().interrupt();
		sIsMonitoringRunning = false;
		closeSockets();
	}

	private static void closeSockets() {
		sThroughputProfile.shutDown();
		sPacketLossProfile.shutDown();
		sRttProfile.shutDown();
		sProfileSync.shutDown();
	}

	public static String getWiFiRSSI() {
		WifiManager wifiManager = (WifiManager) sContext.getSystemService(Context.WIFI_SERVICE);
		return String.valueOf(wifiManager.getConnectionInfo().getRssi());
	}

	@SuppressLint("NewApi")
	public static String getGsmSignalStrength() {
		TelephonyManager telephonyManager = (TelephonyManager) sContext.getSystemService(Context.TELEPHONY_SERVICE);
		if (!telephonyManager.getAllCellInfo().isEmpty()) {
			CellInfoGsm cellinfogsm = (CellInfoGsm) telephonyManager.getAllCellInfo().get(0);
			CellSignalStrengthGsm cellSignalStrengthGsm = cellinfogsm.getCellSignalStrength();
			return String.valueOf(cellSignalStrengthGsm.getDbm());
		}
		return "None";
	}

	@SuppressWarnings("unused")
	private static void updateLocation() {
		final LocationTracker TRACKER = new LocationTracker(sContext);
		TRACKER.setLocationListener(new LocationListenerAdapter() {
			@Override
			public void onLocationChanged(Location location) {
				// 200m
				if (location.getAccuracy() < 200.0f) {
					sLatitude = Double.toString(location.getLatitude());
					sLongitude = Double.toString(location.getLongitude());
				}

				// collect completed
				TRACKER.removeLocationListener();
			}
		});
	}

	private static void updateHeapInfo() {
		sFreeHeap = String.valueOf(Runtime.getRuntime().freeMemory());
		sMaxHeap = String.valueOf(Runtime.getRuntime().maxMemory());
		sCurrentHeap = String.valueOf(Runtime.getRuntime().totalMemory());
	}

	/**
	 * Get memory information from the phone and update it in our system. These
	 * information will be sent to Cloud.
	 *
	 */
	private static void updateMemoryInfo() {
		try {
			Process proc = Runtime.getRuntime().exec("cat /proc/meminfo");
			InputStream is = proc.getInputStream();
			String[] cpuInfoArray = Util.getStringFromInputStream(is).split(System.getProperty("line.separator"));
			for (int i = 0; i < cpuInfoArray.length; i++) {
				if (cpuInfoArray[i].contains("MemTotal")) {
					sTotalMemory = cpuInfoArray[i].split(":")[1].trim();
				}
				if (cpuInfoArray[i].contains("MemFree")) {
					sFreeMemory = cpuInfoArray[i].split(":")[1].trim();
					break;
				}
			}
		} catch (IOException e) {
			Log.e("ERROR", e.getMessage());
		}
	}

	@SuppressLint("UseSparseArrays")
	public static void registerLocalExecution(InvocableMethod invocableMethod, long execTime) throws IOException {
		synchronized (sLastLocalExectime) {
			String methodID = invocableMethod.getMethodId();
			int argSize = getParametersSize(invocableMethod);
			if (sLastLocalExectime.containsKey(methodID)) {
				Integer aS = sLastLocalExectime.get(methodID).getArgSize();
				Long eT = sLastLocalExectime.get(methodID).getExecTime();
				HashMap<Integer, Long> argsizeExectimePair = new HashMap<Integer, Long>();
				argsizeExectimePair.put(aS, eT);
				if (argsizeExectimePair.containsKey(argSize)) {
					long pastExectime = argsizeExectimePair.get(argSize);
					argsizeExectimePair.put(argSize, (long) ((0.5 * pastExectime) + (0.5 * execTime)));

				} else {
					argsizeExectimePair.put(argSize, execTime);
				}
				sLastLocalExectime.get(methodID).setExecTime(argsizeExectimePair.get(argSize));
			} else {
				HashMap<Integer, Long> argsizeExectimePair = new HashMap<Integer, Long>();
				argsizeExectimePair.put(argSize, execTime);
				ProfileSyncData psd = new ProfileSyncData(methodID, invocableMethod.getMethodName(),
						invocableMethod.getPackageName(), invocableMethod.getAppVersion(), argSize, execTime, Util.getIp());
				sLastLocalExectime.put(methodID, psd);
			}
		}
	}

	public static Map<String, ProfileSyncData> getLastLocalExectime() {
		synchronized (sLastLocalExectime) {
			return sLastLocalExectime;
		}
	}

	/**
	 * A method has parameters. This method gets the size of the method's
	 * params.
	 *
	 * @param offloadableMethod
	 * @return - method parameter size
	 */
	public static int getParametersSize(InvocableMethod offloadableMethod) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutput out = null;
		try {
			out = new ObjectOutputStream(bos);
			out.writeObject(offloadableMethod);
			byte[] offloadableMethodBytes = bos.toByteArray();
			return offloadableMethodBytes.length;
		} catch (IOException e) {

		} finally {
			try {
				if (out != null) {
					out.close();
				}
				bos.close();
			} catch (IOException ex) {
			}
		}
		return -1;
	}

	/**
	 * It gets the connection info
	 * 
	 * @return
	 */
	public static int getConnectionInfo() {
		WifiManager wifiManager = (WifiManager) sContext.getSystemService(Context.WIFI_SERVICE);

		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		if (wifiInfo != null) {
			int linkSpeed = wifiInfo.getLinkSpeed();
			return linkSpeed;
		}
		return 0;
	}

	/**
	 * It checks if Wi-FI or Mobile Data connection is allowed.
	 * 
	 * @return
	 */
	public static boolean isOnline() {
		return connectionStatus(ConnectivityManager.TYPE_WIFI) || connectionStatus(ConnectivityManager.TYPE_MOBILE);
	}

	// Fine-grained state
	private static boolean connectionStatus(int type) {
		ConnectivityManager connectivityManager = (ConnectivityManager) sContext
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		return connectivityManager.getNetworkInfo(type).getDetailedState() == DetailedState.CONNECTED;
	}

	/**
	 * It returns the network type
	 * 
	 * @return
	 */
	public static String getNetworkConnectedType() {
		NetworkInfo info = ((ConnectivityManager) sContext.getSystemService(Context.CONNECTIVITY_SERVICE))
				.getActiveNetworkInfo();

		if (info != null) {
			int type = info.getType();
			int subtype = info.getSubtype();

			if (type == ConnectivityManager.TYPE_WIFI) {
				return "WiFi";
			} else if (type == ConnectivityManager.TYPE_MOBILE) {
				switch (subtype) {
				case TelephonyManager.NETWORK_TYPE_CDMA:
					return "CDMA";
				case TelephonyManager.NETWORK_TYPE_IDEN:
					return "iDen";
				case TelephonyManager.NETWORK_TYPE_EHRPD:
					return "eHRPD";
				case TelephonyManager.NETWORK_TYPE_EDGE:
					return "EDGE";
				case TelephonyManager.NETWORK_TYPE_1xRTT:
					return "1xRTT";
				case TelephonyManager.NETWORK_TYPE_GPRS:
					return "GPRS";
				case TelephonyManager.NETWORK_TYPE_UMTS:
					return "UMTS";
				case TelephonyManager.NETWORK_TYPE_EVDO_0:
				case TelephonyManager.NETWORK_TYPE_EVDO_A:
				case TelephonyManager.NETWORK_TYPE_EVDO_B:
					return "EVDO";
				case TelephonyManager.NETWORK_TYPE_HSPA:
					return "HSPA";
				case TelephonyManager.NETWORK_TYPE_HSDPA:
					return "HSDPA";
				case TelephonyManager.NETWORK_TYPE_HSPAP:
					return "HSPA+";
				case TelephonyManager.NETWORK_TYPE_HSUPA:
					return "HSUPA";
				case TelephonyManager.NETWORK_TYPE_LTE:
					return "LTE";
				default:
					return "Unknown";
				}
			}
		}
		return "Offline";
	}

	public static void setDownloadRate(double download) {
		sMonitorContext.put((short) 1, download);
	}

	public static void setUploadRate(double upload) {
		sMonitorContext.put((short) 2, upload);
	}

	public static void setRTT(double rtt) {
		sMonitorContext.put((short) 3, rtt);
	}

	public static Map<Short, Object> getMonitorContext() {
		return sMonitorContext;
	}

}