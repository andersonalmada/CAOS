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
public class SizeSynchronizerStrategy implements ISynchronizerStrategy {

    private Handler mHhandlerSize;
    private Runnable mR;
    private int mSize = 10;

    public SizeSynchronizerStrategy() {
        this.mHhandlerSize = new Handler();
    }

    public SizeSynchronizerStrategy(int size) {
        this.mSize = size;
        this.mHhandlerSize = new Handler();
    }

    @Override
    public void start() {
        mR = new Runnable() {
            public void run() {
            	if(DataManager.arrayTuples.length() >= mSize) {
            		DataManager.offloading();	
            	}
            	
                mHhandlerSize.postDelayed(this, 1000);
            }
        };

        mHhandlerSize.postDelayed(mR, 1000);
    }

    @Override
    public void stop() {
        mHhandlerSize.removeMessages(0);
    }

    @Override
    public Object getValue() {
        return mSize;
    }

    @Override
    public void setValue(Object object) {
        this.mSize = Integer.parseInt("" + object);
    }
}
