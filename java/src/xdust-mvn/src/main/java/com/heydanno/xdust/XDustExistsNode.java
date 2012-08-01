package com.heydanno.xdust;

import java.util.Map;

/**
 * Node for testing a data point and switching between two blocks based on its
 * truthiness
 */
public class XDustExistsNode extends XDustLogicNode {

	private static final long serialVersionUID = 4129001443943748282L;

	/**
	 * Constructor
	 * 
	 * @param path
	 *            The path to resolve against the data context
	 * @param scope
	 *            An alternate path to select the block's data context
	 * @param parameters
	 *            Variables to define for the block
	 */
	public XDustExistsNode(String path, String scope,
			Map<String, XDustNode> parameters) {
		super(path, scope, parameters);
	}

	/**
	 * Gets the dust operator character associated with this node type
	 * 
	 * @return The operator character
	 */
	@Override
	public String getOperator() {
		return "?";
	}

	/**
	 * Whether or not this type of logic node can function as a loop
	 * 
	 * @return True if the node allows iteration
	 */
	@Override
	public boolean getAllowIteration() {
		return false;
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
	public Context prepareModel(XDust dust, RenderChain chain, Context context,
			Object model) {
		return new Context(context, context, this.getParameters());
	}

}
