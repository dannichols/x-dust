package com.heydanno.xdust;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Node that makes a reference to an external template
 */
public class XDustPartialNode extends XDustNode implements IXDustSectionNode {

	private static final long serialVersionUID = 2564920555706288058L;

	/**
	 * Constructor
	 * 
	 * @param include
	 *            The name of the template to include or a node list that
	 *            renders into the name of the template to include
	 * @param scope
	 *            The context path to use for the included template
	 */
	public XDustPartialNode(Object include, String scope) {
		super();
		this.setInclude(include);
		if (null != scope) {
			this.setScope(new ContextResolver(scope));
		}
	}

	private Object include;
	private ContextResolver scope;
	private Map<String, XDustNode> parameters;

	/**
	 * Gets the name of the template to include
	 * 
	 * @return The name of the template
	 */
	public Object getInclude() {
		return include;
	}

	/**
	 * Sets the name of the template to include
	 * 
	 * @param include
	 *            The name of the template
	 */
	public void setInclude(Object include) {
		this.include = include;
	}

	/**
	 * Gets the path of the included template's data context
	 * 
	 * @return The scope
	 */
	public ContextResolver getScope() {
		return scope;
	}

	/**
	 * Sets the path of the included template's data context
	 * 
	 * @param scope
	 *            The scope
	 */
	public void setScope(ContextResolver scope) {
		this.scope = scope;
	}

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
	 * Serializes the node to string
	 * 
	 * @return The code string
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("{>");
		if (this.getInclude() instanceof XDustNodeList) {
			sb.append("\"");
		}
		sb.append(this.getInclude());
		if (this.getInclude() instanceof XDustNodeList) {
			sb.append("\"");
		}
		if (null != this.getScope()) {
			sb.append(":").append(this.getScope());
		}
		sb.append("/}");
		return sb.toString();
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
		if (null != this.getScope()) {
			model = this.getScope().resolve(context, model);
		}
		chain = new RenderChain(chain, this);
		String name;
		if (this.getInclude() instanceof XDustNodeList) {
			name = ((XDustNodeList) this.getInclude()).render(dust, chain,
					context, model);
		} else {
			name = this.getInclude().toString();
		}
		XDustNode template;
		try {
			template = dust.load(null, name);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return template.render(dust, chain, context, model);
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
