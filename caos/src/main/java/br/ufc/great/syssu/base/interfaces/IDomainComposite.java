/*******************************************************************************
 * Copyright (c) 2015 LG Electronics. All Rights Reserved. This software is the 
 * confidential and proprietary information of LG Electronics. You shall not
 * disclose such Confidential Information and shall use it only in accordance
 * with the terms of the license agreement you entered into with LG Electronics.
 *******************************************************************************/
package br.ufc.great.syssu.base.interfaces;

import br.ufc.great.syssu.base.TupleSpaceException;

/**
 * Interface used to create multiple domains.
 */
public interface IDomainComposite {

	/**
	 * Get a specific domain.
     *
	 * @param name the name of the domain.
	 * @return The interface to access the domain.
     * @throws TupleSpaceException If some general exception occurs on the tuple space.
	 */
    IDomain getDomain(String name) throws TupleSpaceException;
}
