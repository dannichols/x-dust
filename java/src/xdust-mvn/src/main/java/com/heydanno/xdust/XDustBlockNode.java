package com.heydanno.xdust;

/**
 * Node representing a named block
 */
public class XDustBlockNode extends XDustNodeList {

	private static final long serialVersionUID = 3058596907129626444L;

	/**
	 * Constructs a new block node
	 * 
	 * @param name
	 *            The name of the block
	 */
	public XDustBlockNode(String name) {
		super(null);
		this.setName(name);
	}

	private String name;

	/**
	 * Gets the name of the block
	 * 
	 * @return The name of the block
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name of the block
	 * 
	 * @param name
	 *            The name of the block
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Serializes the node to string
	 * 
	 * @return The code string
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{+").append(this.getName()).append("}");
		sb.append(super.toString());
		sb.append("{/").append(this.getName()).append("}");
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
		XDustNode block = chain.getBlock(this.getName());
		if (null != block) {
			return block.render(dust, chain, context, model);
		} else {
			return super.render(dust, chain, context, model);
		}
	}

}
