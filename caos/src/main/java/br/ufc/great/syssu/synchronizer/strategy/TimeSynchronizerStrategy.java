/*******************************************************************************
 * Copyright (c) 2015 LG Electronics. All Rights Reserved. This software is the 
 * confidential and proprietary information of LG Electronics. You shall not
 * disclose such Confidential Information and shall use it only in accordance
 * with the terms of the license agreement you entered into with LG Electronics.
 *******************************************************************************/
package br.ufc.great.syssu.synchronizer.strategy;

import android.os.Handler;
import br.ufc.great.caos.data.DataManager;
import br.ufc.great.syssu.base.interfaces.ISynchronizerStrategy;

/**
 * Created by Almada on 01/06/2015.
 */
public class TimeSynchronizerStrategy implements ISynchronizerStrategy {

    private Handler mHandlerTime;
    private Runnable mR;
    private int mTime = 10000;

    public TimeSynchronizerStrategy() {
        this.mHandlerTime = new Handler();
    }

    public TimeSynchronizerStrategy(int time) {
        this.mTime = time;
        this.mHandlerTime = new Handler();
    }

    @Override
    public void start() {
        mR = new Runnable() {
            public void run() {
            	if(DataManager.arrayTuples.length() > 0) {
            		DataManager.offloading();	
            	}
                                    
                mHandlerTime.postDelayed(this, mTime);
            }
        };

        mHandlerTime.postDelayed(mR, 1000);
    }

    @Override
    public void stop() {
        mHandlerTime.removeMessages(0);
    }

    public Object getValue() {
        return mTime;
    }

    @Override
    public void setValue(Object object) {
        this.mTime = Integer.parseInt("" + object);
    }
}
