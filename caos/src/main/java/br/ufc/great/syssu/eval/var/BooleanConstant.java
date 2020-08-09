/*******************************************************************************
 * Copyright (c) 2015 LG Electronics. All Rights Reserved. This software is the 
 * confidential and proprietary information of LG Electronics. You shall not
 * disclose such Confidential Information and shall use it only in accordance
 * with the terms of the license agreement you entered into with LG Electronics.
 *******************************************************************************/
package br.ufc.great.syssu.eval.var;

import br.ufc.great.syssu.base.Tuple;
import br.ufc.great.syssu.eval.EvaluationException;

public class BooleanConstant extends Variable<Boolean> {

	private Boolean mValue;

	public BooleanConstant() {
	}

	public BooleanConstant(Boolean value) {
		super(value.toString());
		this.mValue = value;
	}

	public void setValue(Boolean value) {
		this.mValue = value;
	}

	@Override
	public Boolean getValuation(Tuple tuple) throws EvaluationException {
		return this.mValue;
	}

}
