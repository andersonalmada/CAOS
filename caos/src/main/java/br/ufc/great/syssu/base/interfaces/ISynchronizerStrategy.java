/*******************************************************************************
 * Copyright (c) 2015 LG Electronics. All Rights Reserved. This software is the 
 * confidential and proprietary information of LG Electronics. You shall not
 * disclose such Confidential Information and shall use it only in accordance
 * with the terms of the license agreement you entered into with LG Electronics.
 *******************************************************************************/
package br.ufc.great.syssu.base.interfaces;

/**
 * Created by Almada on 01/06/2015.
 *
 * Synchronization strategy interface.
 */
public interface ISynchronizerStrategy {

    /**
     * Start a synchronization strategy.
     */
    public void start();

    /**
     * Stop a synchronization strategy.
     */
    public void stop();

    /**
     * Get the threshold value of this strategy.
     *
     * @return the threshold value.
     */
    public Object getValue();

    /**
     * Set the threshold value for this strategy.
     *
     * @param value the threshold value.
     */
    public void setValue(Object value);
}
