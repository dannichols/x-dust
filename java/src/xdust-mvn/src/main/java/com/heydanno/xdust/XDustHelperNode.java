package com.heydanno.xdust;

import java.util.Map;

/**
 * Node invoking an x-dust extension (helper)
 */
public class XDustHelperNode extends XDustLogicNode {

	private static final long serialVersionUID = -3046616917519170745L;

	/**
	 * Constructs a helper node
	 * 
	 * @param name
	 *            The name of the helper
	 * @param scope
	 *            The path defining the context for the helper
	 * @param parameters
	 *            A map of variables to define for the helper
	 */
	public XDustHelperNode(String name, String scope,
			Map<String, XDustNode> parameters) {
		super(null, scope, parameters);
		this.setName(name);
	}

	private String name;

	/**
	 * Gets the name of the helper
	 * 
	 * @return The name of the helper
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name of the helper
	 * 
	 * @param name
	 *            The name of the helper
	 */
	public void setName(String name) {
		this.name = name;
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
	 * Renders the node to string
	 * 
	 * @param dust
	 *            The current render engine
	 * @param chain
	 *            The current chain of nodes being rendered
	 * @param context
	 *            The chain's data context
	 * @param model
	 *            The current data
	 * @return The rendered output
	 */
	@Override
	public String render(XDust dust, RenderChain chain, Context context,
			Object model) {
		chain = new RenderChain(chain, this);
		context = new Context(context, null, this.getParameters());
		IXDustHelper helper = dust.getHelpers().get(this.getName());
		return helper.render(dust, chain, context, model);
	}

}
