/*******************************************************************************
 * Copyright (c) 2015 LG Electronics. All Rights Reserved. This software is the 
 * confidential and proprietary information of LG Electronics. You shall not
 * disclose such Confidential Information and shall use it only in accordance
 * with the terms of the license agreement you entered into with LG Electronics.
 *******************************************************************************/
package br.ufc.great.syssu.eval.var;

import br.ufc.great.syssu.base.Tuple;
import br.ufc.great.syssu.eval.EvaluationException;

public class StringConstant extends Variable<String> {

	private String value;

	public StringConstant() {
	}

	public StringConstant(String value) {
		super("\"" + value + "\"");
		this.value = value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String getValuation(Tuple tuple) throws EvaluationException {
		return this.value;
	}

}
