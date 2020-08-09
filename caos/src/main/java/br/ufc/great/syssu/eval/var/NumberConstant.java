/*******************************************************************************
 * Copyright (c) 2015 LG Electronics. All Rights Reserved. This software is the 
 * confidential and proprietary information of LG Electronics. You shall not
 * disclose such Confidential Information and shall use it only in accordance
 * with the terms of the license agreement you entered into with LG Electronics.
 *******************************************************************************/
package br.ufc.great.syssu.eval.var;

import br.ufc.great.syssu.base.Tuple;
import br.ufc.great.syssu.eval.EvaluationException;

public class NumberConstant extends Variable<Number> {

	private Number value;

	public NumberConstant() {
	}

	public NumberConstant(Number value) {
		super(value.toString());
		this.value = value.doubleValue();
	}

	public void setValue(Number value) {
		this.value = value;
	}

	@Override
	public Number getValuation(Tuple tuple) throws EvaluationException {
		return this.value;
	}

}
