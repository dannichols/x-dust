package com.heydanno.xdust;

import java.util.Map;

/**
 * Defines a node that can have multiple blocks
 */
public interface IXDustSectionNode {

	/**
	 * Gets the variables defined for this node
	 * 
	 * @return The variables map
	 */
	Map<String, XDustNode> getParameters();

	/**
	 * Creates a body that contains subnodes
	 * 
	 * @param name
	 *            The name of the body
	 * @return The new body
	 */
	XDustNodeList startBody(String name);

	/**
	 * Closes the most recently started body
	 */
	void endBody();

}
