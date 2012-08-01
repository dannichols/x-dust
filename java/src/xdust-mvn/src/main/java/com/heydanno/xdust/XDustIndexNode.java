package com.heydanno.xdust;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Node for exposing the index of a node within a loop
 */
public class XDustIndexNode extends XDustNodeList implements IXDustSectionNode {

	private static final long serialVersionUID = -7070011746376720226L;

	/**
	 * Constructor
	 * 
	 * @param nodes
	 *            The collection of nodes that make up this node list
	 */
	public XDustIndexNode(Collection<XDustNode> nodes) {
		super(nodes);
	}

	private Map<String, XDustNode> parameters;

	/**
	 * Gets the variables defined for this node
	 * 
	 * @return The variables map
	 */
	public Map<String, XDustNode> getParameters() {
		if (null == this.parameters) {
			this.parameters = new HashMap<String, XDustNode>();
		}
		return this.parameters;
	}

	/**
	 * Readies a model to be rendered
	 * 
	 * @param dust
	 *            The current dust render engine instance
	 * @param chain
	 *            The current chain of nodes being rendered
	 * @param context
	 *            The current data context
	 * @param model
	 *            The current data model
	 * @return The prepared context object
	 */
	@Override
	public Object prepareModel(RenderChain chain, Context context, Object model) {
		IScriptable scriptable = Scriptable.from(model);
		try {
			if (scriptable.get("@idx") != null) {
				return scriptable.get("@idx").toString();
			} else {
				return null;
			}
		} catch (IllegalArgumentException e) {
			return null;
		}
	}

	/**
	 * Serializes the node to string
	 * 
	 * @return The code string
	 */
	@Override
	public String toString() {
		return String.format("{@idx}%s{/idx}", super.toString());
	}

	/**
	 * Creates a body that contains subnodes
	 * 
	 * @param name
	 *            The name of the body
	 * @return The new body
	 */
	public XDustNodeList startBody(String name) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Closes the most recently started body
	 */
	public void endBody() {
		throw new UnsupportedOperationException();
	}

}
