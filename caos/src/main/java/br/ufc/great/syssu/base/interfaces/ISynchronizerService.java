/*******************************************************************************
 * Copyright (c) 2015 LG Electronics. All Rights Reserved. This software is the 
 * confidential and proprietary information of LG Electronics. You shall not
 * disclose such Confidential Information and shall use it only in accordance
 * with the terms of the license agreement you entered into with LG Electronics.
 *******************************************************************************/
package br.ufc.great.syssu.base.interfaces;

/**
 * Created by Almada on 02/06/2015.
 *
 * Interface for sending tuples to cloud.
 */
public interface ISynchronizerService {

    /**
     * Add a synchronization strategy.
     *
     * @param strategy the synchronization strategy.
     */
    public void addStrategy(ISynchronizerStrategy strategy);

    /**
     * Remove a synchronization strategy.
     * @param strategy the synchronization strategy.
     */
    public void removeStrategy(ISynchronizerStrategy strategy);
}
