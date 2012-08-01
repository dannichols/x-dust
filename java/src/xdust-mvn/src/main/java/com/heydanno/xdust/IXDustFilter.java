package com.heydanno.xdust;

/**
 * Defines an object that can be used by the dust engine to filter output
 */
public interface IXDustFilter {

	/**
	 * Executes the filter on a value
	 * 
	 * @param dust
	 *            The current dust engine instance
	 * @param obj
	 *            The object to filter
	 * @return The filtered output
	 */
	String call(XDust dust, Object obj);

}
