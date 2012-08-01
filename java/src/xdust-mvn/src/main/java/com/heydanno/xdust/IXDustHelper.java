package com.heydanno.xdust;

/**
 * Defines a custom tag that can be invoked in a dust template
 */
public interface IXDustHelper {

	/**
	 * Renders the tag
	 * 
	 * @param dust
	 *            The current instance of the dust engine
	 * @param chain
	 *            The node chain leading to this tag
	 * @param context
	 *            The current data context
	 * @param model
	 *            The current data object
	 * @return The rendered output
	 */
	String render(XDust dust, RenderChain chain, Context context, Object model);

}
