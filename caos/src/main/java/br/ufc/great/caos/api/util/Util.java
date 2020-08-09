/*******************************************************************************
 * Copyright (c) 2015 LG Electronics. All Rights Reserved. This software is the
 * confidential and proprietary information of LG Electronics. You shall not
 * disclose such Confidential Information and shall use it only in accordance
 * with the terms of the license agreement you entered into with LG Electronics.
 *******************************************************************************/
package br.ufc.great.caos.api.util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteOrder;
import java.util.Enumeration;
import java.util.regex.Pattern;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.util.Log;

public final class Util {

	private static final String CAOS_KEY = "caos_key";

	private static Pattern sPatternIpAddress;
	private static final String IP_ADDRESS_PATTERN = "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
			+ "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
			+ "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";

	static {
		sPatternIpAddress = Pattern.compile(IP_ADDRESS_PATTERN);
	}

	private Util() {

	}

	/**
	 * Tells if Wi-Fi connectivity is available
	 */
	public static boolean hasConnection(Context context) {
		ConnectionVerify connectionVerify = new ConnectionVerify(context);
		return connectionVerify.hasConnection();
	}

	/**
	 * Validate ip address with regular expression
	 * 
	 * @param ip
	 * @return true valid ip address, false invalid ip address
	 */
	public static boolean validateIpAddress(final String ip) {
		return sPatternIpAddress.matcher(ip).matches();
	}

	public static String getStringFromInputStream(InputStream is) {
		StringBuilder sb = new StringBuilder();
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		String line = null;

		try {
			while ((line = br.readLine()) != null) {
				sb.append(line);
				sb.append("\n");
			}
		} catch (IOException e) {
			Log.e("ERROR", e.getMessage());
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					Log.e("ERROR", e.getMessage());
				}
			}
		}
		return sb.toString();
	}

	/**
	 * Converts an Object into a byte[] and encrypts it using CAOS_KEY as the
	 * encrypting key.
	 * 
	 * @param obj the Object to be wrapped
	 * @return encrypted byte[] which represents obj
	 */
	public static byte[] wrap(Object obj) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ObjectOutputStream os;
		try {
			os = new ObjectOutputStream(out);
			os.writeObject(obj);
			BCEncryptor encryptor = new BCEncryptor(CAOS_KEY);
			return encryptor.encrypt(out.toByteArray());
		} catch (IOException | org.bouncycastle.crypto.CryptoException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Decrypts the byte[] data by using CAOS_KEY as the encryption key and converts
	 * it into an Object.
	 * 
	 * @param encrypted byte[] which represents the Object
	 * @return deserialized and unencrypted byte[] as an Object
	 */
	public static Object unwrap(byte[] data) {
		try {
			BCEncryptor encryptor = new BCEncryptor(CAOS_KEY);
			ByteArrayInputStream in = new ByteArrayInputStream(encryptor.decrypt(data));
			ObjectInputStream is;
			is = new ObjectInputStream(in);
			return is.readObject();
		} catch (IOException | ClassNotFoundException | org.bouncycastle.crypto.CryptoException e) {
			//e.printStackTrace();
		}

		return null;
	}

	/**
	 * Collects the wlan0 ip
	 */
	public static String wifiIpAddress(Context context) {
		WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		int ipAddress = wifiManager.getConnectionInfo().getIpAddress();

		// Convert little-endian to big-endianif needed
		if (ByteOrder.nativeOrder().equals(ByteOrder.LITTLE_ENDIAN)) {
			ipAddress = Integer.reverseBytes(ipAddress);
		}

		byte[] ipByteArray = BigInteger.valueOf(ipAddress).toByteArray();

		String ipAddressString;
		try {
			ipAddressString = InetAddress.getByAddress(ipByteArray).getHostAddress();
		} catch (UnknownHostException ex) {
			Log.e("WIFIIP", "Unable to get host address.");
			ipAddressString = null;
		}

		return ipAddressString;
	}	
	
	public static String getIp() {
		String ipAddress = null;
		Enumeration<NetworkInterface> net = null;
		try {
			net = NetworkInterface.getNetworkInterfaces();
		} catch (SocketException e) {
			throw new RuntimeException(e);
		}

		while (net.hasMoreElements()) {
			NetworkInterface element = net.nextElement();
			Enumeration<InetAddress> addresses = element.getInetAddresses();
			while (addresses.hasMoreElements()) {
				InetAddress ip = addresses.nextElement();

				if (ip.isSiteLocalAddress()) {
					ipAddress = ip.getHostAddress();
				}
			}
		}
		return ipAddress;
	}
	
	public static File getAppOffloading(Context context) {
		PackageManager pm = context.getPackageManager();
		String packageName = context.getPackageName();

        for (ApplicationInfo app : pm.getInstalledApplications(0)) {
            if (app.sourceDir.contains(packageName)) {
                return new File(app.sourceDir);                
            }
        }
        
        return null;
	}
}