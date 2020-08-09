/*******************************************************************************
 * Copyright (c) 2015 LG Electronics. All Rights Reserved. This software is the 
 * confidential and proprietary information of LG Electronics. You shall not
 * disclose such Confidential Information and shall use it only in accordance
 * with the terms of the license agreement you entered into with LG Electronics.
 *******************************************************************************/
package br.ufc.great.syssu.eval;

import br.ufc.great.syssu.eval.var.NumberConstant;
import br.ufc.great.syssu.eval.var.NumberSensorVariable;
import br.ufc.great.syssu.eval.var.Variable;

public abstract class Expression {
	
	public static boolean sIsLocal;

	protected Operator mOperator;

	protected Expression mLeftSide;

	protected Expression mRightSide;
	
	public static Expression createExpression(Operator operator, Expression leftSide) {
		return createExpression(operator, leftSide, null);
	}

	public static Expression createExpression(Operator operator,
			Expression leftSide, Expression rightSide) {
		sIsLocal = false; //pegar de algum canto
		
		if (sIsLocal) {
			ExpressionLocal local = new ExpressionLocal();
			local.mOperator = operator;
			local.mLeftSide = leftSide;
			local.mRightSide = rightSide;
			return local;
		}
		else {
			ExpressionMongo mongo = new ExpressionMongo();
			mongo.mOperator = operator;
			mongo.mLeftSide = leftSide;
			mongo.mRightSide = rightSide;
			return mongo;
		}
	}

	public Expression getLeftSide() {
		return mLeftSide;
	}

	public Expression getRightSide() {
		return mRightSide;
	}

	public Operator getOperator() {
		return mOperator;
	}
	
	public abstract String toString();
	
	public static Expression timestamp(Expression type, Expression value) {
		return createExpression(Operator.TIMESTAMP, type, value);
	}
	
	public static Expression sensor(Expression type) {
		NumberSensorVariable field = new NumberSensorVariable("timestamp");
		Expression exp = gt(field, new NumberConstant(0));
		
		Expression first = createExpression(Operator.MATCH, type, exp);
		return timestamp(first, null);
	}
	
	public static Expression sensor(Expression type, Expression value) {
		Expression first = createExpression(Operator.MATCH, type, value);
		return timestamp(first, null);
	}
	
	public static Expression sensor(Expression type, Expression value, Expression timestamp) {
		Expression first = createExpression(Operator.MATCH, type, value);
		return timestamp(first, timestamp);
	}
	
	public static Expression and(Expression left, Expression right) {
		return createExpression(Operator.AND, left, right);
	}
	
	public static Expression and(Expression... operands) {
		Expression conjunction = null;
		if (operands.length >= 2) {
			conjunction = and(operands[0], operands[1]);

			if (operands.length > 2) {
				for (int i = 2; i < operands.length; i++) {
					conjunction = and(conjunction, operands[i]);
				}
			}
		}
		return conjunction;
	}

	public static Expression nand(Expression left, Expression right) {
		return not(and(left, right));
	}

	public static Expression or(Expression left, Expression right) {
		return createExpression(Operator.OR, left, right);
	}

	public static Expression or(Expression... operands) {
		Expression disjunction = null;
		if (operands.length >= 2) {
			disjunction = or(operands[0], operands[1]);

			if (operands.length > 2) {
				for (int i = 2; i < operands.length; i++) {
					disjunction = or(disjunction, operands[i]);
				}
			}
		}
		return disjunction;
	}

	public static Expression nor(Expression left, Expression right) {
		return not(or(left, right));
	}

	public static Expression not(Expression formula) {
		return createExpression(Operator.NOT, formula);
	}

	public static Expression ifThen(Expression left, Expression right) {
		return or(not(left), right);
	}

	public static Expression iff(Expression left, Expression right) {
		return or(and(not(left), not(right)), and(left, right));
	}

	public static Expression xor(Expression left, Expression right) {
		return or(and(left, not(right)), and(not(left), right));
	}

	public static Expression eq(Variable< ? > left, Variable< ? > rigth) {
		return createExpression(Operator.EQ, left, rigth);
	}

	public static Expression lt(Variable< ? > left, Variable< ? > rigth) {
		return createExpression(Operator.LT, left, rigth);
	}

	public static Expression gt(Variable< ? > left, Variable< ? > rigth) {
		return createExpression(Operator.GT, left, rigth);
	}

	public static Expression lteq(Variable< ? > left, Variable< ? > rigth) {
		return createExpression(Operator.LTEQ, left, rigth);
	}

	public static Expression gteq(Variable< ? > left, Variable< ? > rigth) {
		return createExpression(Operator.GTEQ, left, rigth);
	}
}