package com.heydanno.xdust;

import java.util.Map;

/**
 * Node for testing a data point and switching between two blocks based on its
 * falseyness
 */
public class XDustNotExistsNode extends XDustExistsNode {

	private static final long serialVersionUID = 6236854786516129400L;

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
	public XDustNotExistsNode(String path, String scope,
			Map<String, XDustNode> parameters) {
		super(path, scope, parameters);
	}

	/**
	 * Selects the name of the body to render
	 * 
	 * @param context
	 *            The current data context
	 * @param model
	 *            The current data model
	 * @return The name of the body to render
	 */
	@Override
	public String chooseBodyName(Context context, Object model) {
		Object resolved = this.getContext().resolve(context, model);
		return this.isTruthy(resolved) ? ELSE : BLOCK;
	}

}
