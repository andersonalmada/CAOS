/*******************************************************************************
 * Copyright (c) 2015 LG Electronics. All Rights Reserved. This software is the 
 * confidential and proprietary information of LG Electronics. You shall not
 * disclose such Confidential Information and shall use it only in accordance
 * with the terms of the license agreement you entered into with LG Electronics.
 *******************************************************************************/
package br.ufc.great.syssu.eval.var;

import br.ufc.great.syssu.base.Tuple;
import br.ufc.great.syssu.eval.EvaluationException;
import br.ufc.great.syssu.eval.Expression;

public abstract class Variable<Type> extends Expression {

	private String mName;

	public Variable() {
	}

	public Variable(String name) {
		this.mName = name;
	}

	public String getName() {
		return mName;
	}

	public void setName(String name) {
		this.mName = name;
	}

	public String toString() {
		return this.mName;
	}

	public abstract Type getValuation(Tuple tuple) throws EvaluationException;
}