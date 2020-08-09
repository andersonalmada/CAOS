/*******************************************************************************
 * Copyright (c) 2015 LG Electronics. All Rights Reserved. This software is the
 * confidential and proprietary information of LG Electronics. You shall not
 * disclose such Confidential Information and shall use it only in accordance
 * with the terms of the license agreement you entered into with LG Electronics.
 *******************************************************************************/
package br.ufc.great.caos.api.util;

import android.content.Context;
import android.location.LocationListener;
import android.location.LocationManager;

/**
 * This a class which use the Android location services, like A-GPS and etc.
 */
public final class LocationTracker {
	private LocationManager mLocationManager;
	private LocationListener mLocationListener;

	public LocationTracker(Context context) {
		mLocationManager = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);
	}

	public void setLocationListener(LocationListener locationListener) {
		try {
			mLocationManager.requestLocationUpdates(
					LocationManager.NETWORK_PROVIDER, 500, 0, locationListener);
		} catch (IllegalArgumentException e) {
			mLocationManager.requestLocationUpdates(
					LocationManager.GPS_PROVIDER, 500, 0, locationListener);
		}
		this.mLocationListener = locationListener;
	}

	public void removeLocationListener() {
		mLocationManager.removeUpdates(mLocationListener);
	}
}