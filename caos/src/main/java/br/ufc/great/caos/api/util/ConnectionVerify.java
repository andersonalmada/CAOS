/*******************************************************************************
 * Copyright (c) 2015 LG Electronics. All Rights Reserved. This software is the
 * confidential and proprietary information of LG Electronics. You shall not
 * disclose such Confidential Information and shall use it only in accordance
 * with the terms of the license agreement you entered into with LG Electronics.
 *******************************************************************************/
package br.ufc.great.caos.api.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Implements Wi-Fi state verification
 */
public class ConnectionVerify {
	private Context mContext;

	public ConnectionVerify(Context context) {
		this.mContext = context;
	}

	/**
	 * It verifies if there is Wi-Fi connection
	 */
	public boolean hasConnection() {

		ConnectivityManager connectivityManager = (ConnectivityManager) mContext
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivityManager != null) {
			NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();

			if (netInfo == null) {
				return false;
			}

			return netInfo.isConnected();
			
			//int netType = netInfo.getType();

			//if (netType == ConnectivityManager.TYPE_WIFI) {
			
		//	} else {
			//	return false;
			//}
		} else {
			return false;
		}
	}
}
