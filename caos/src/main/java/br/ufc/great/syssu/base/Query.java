/*******************************************************************************
 * Copyright (c) 2015 LG Electronics. All Rights Reserved. This software is the 
 * confidential and proprietary information of LG Electronics. You shall not
 * disclose such Confidential Information and shall use it only in accordance
 * with the terms of the license agreement you entered into with LG Electronics.
 *******************************************************************************/
package br.ufc.great.syssu.base;

import br.ufc.great.syssu.base.interfaces.IFilter;

/**
 * Represents a query in the tuple space.
 */
public class Query {
	
	private Pattern mPattern;
	private IFilter mJavaFilter;
	private String mJsFilter;
	
	/**
	 * Constructor using the javascript filter.
     *
	 * @param pattern the pattern used to select tuples.
	 * @param filter the filter used to refine the selection.
	 */
	public Query(Pattern pattern, String filter) {
		this.mPattern = pattern;
		this.mJsFilter = filter;
	}

    /**
     * Constructor using the java filter.
     *
     * @param pattern the pattern used to select tuples.
     * @param filter the filter used to refine the selection.
     */
	public Query(Pattern pattern, IFilter filter) {
		this.mPattern = pattern;
		this.mJavaFilter = filter;
	}

	/**
	 * Get the pattern used for this query.
     *
	 * @return the pattern used for this query.
	 */
	public Pattern getPattern() {
		return mPattern;
	}

	/**
	 * Get the javascript filter used for this query.
     *
	 * @return the javascript filter used for this query.
	 */
	public String getJavaScriptFilter() {
		return mJsFilter;
	}
	
	/**
	 * Get the java filter used for this query.
     *
	 * @return the java filter used for this query.
	 */
	public IFilter getJavaFilter() {
		return mJavaFilter;
	}
}
