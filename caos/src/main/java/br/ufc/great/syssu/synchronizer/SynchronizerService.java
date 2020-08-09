/*******************************************************************************
 * Copyright (c) 2015 LG Electronics. All Rights Reserved. This software is the 
 * confidential and proprietary information of LG Electronics. You shall not
 * disclose such Confidential Information and shall use it only in accordance
 * with the terms of the license agreement you entered into with LG Electronics.
 *******************************************************************************/
package br.ufc.great.syssu.synchronizer;

import java.util.ArrayList;

import br.ufc.great.syssu.base.interfaces.ISynchronizerService;
import br.ufc.great.syssu.base.interfaces.ISynchronizerStrategy;

/**
 * Created by Almada on 02/06/2015.
 */
public class SynchronizerService implements ISynchronizerService {

	private ArrayList<ISynchronizerStrategy> mListStrategy;
	
	public SynchronizerService() {
		mListStrategy = new ArrayList<ISynchronizerStrategy>();
	}
	
	@Override
	public void addStrategy(ISynchronizerStrategy strategy) {
		if (!mListStrategy.contains(strategy)) {
			strategy.start();
			mListStrategy.add(strategy);
		}
	}

	@Override
	public void removeStrategy(ISynchronizerStrategy strategy) {
		if (mListStrategy.contains(strategy)) {
			strategy.stop();
			mListStrategy.remove(strategy);
		}
	}
}
