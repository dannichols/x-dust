package com.heydanno.xdust;

/**
 * Defines an object that can receive the results of a dust rendering operation
 */
public interface IXDustResult {

	/**
	 * Supplies results of the rendering operation
	 * 
	 * @param error
	 *            An error that occurred during the operation, or null if no
	 *            error occurred
	 * @param output
	 *            The rendered output, or null if an error occurred
	 */
	void call(Exception error, String output);

}
