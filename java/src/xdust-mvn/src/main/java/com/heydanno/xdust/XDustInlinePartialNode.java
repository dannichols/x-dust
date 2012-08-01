package com.heydanno.xdust;

import java.util.HashMap;
import java.util.Map;

/**
 * Node that allows a block to be overwritten
 */
public class XDustInlinePartialNode extends XDustNodeList implements
		IXDustSectionNode {

	private static final long serialVersionUID = 8417337831119373869L;

	/**
	 * Constructor
	 * 
	 * @param name
	 *            The name of the section to override
	 */
	public XDustInlinePartialNode(String name) {
		super(null);
		this.setName(name);
	}

	private String name;
	private Map<String, XDustNode> parameters;

	/**
	 * Gets the name of the section to override
	 * 
	 * @return The name of the section
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name of the section to override
	 * 
	 * @param name
	 *            The name of the section
	 */
	public void setName(String name) {
		this.name = name;
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
		StringBuilder sb = new StringBuilder();
		sb.append("{<").append(this.getName()).append("}");
		sb.append(super.toString());
		sb.append("{/").append(this.getName()).append("}");
		return sb.toString();
	}

	/**
	 * Gets this node as a block to be rendered
	 * 
	 * @return This node
	 */
	@Override
	public XDustNode asBlock() {
		return new XDustNodeList(this.getNodes());
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
		return "";
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
