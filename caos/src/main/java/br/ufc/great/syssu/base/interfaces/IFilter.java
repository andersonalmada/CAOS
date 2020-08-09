package br.ufc.great.syssu.base.interfaces;

import br.ufc.great.syssu.eval.Expression;

public interface IFilter {

	public Expression localFilter();

	public Expression remoteFilter();
}
