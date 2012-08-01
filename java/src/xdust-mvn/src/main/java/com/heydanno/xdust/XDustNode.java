package com.heydanno.xdust;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Base type for all x-dust nodes
 */
public abstract class XDustNode implements Serializable {

	private static final long serialVersionUID = -1081092121267113562L;

	/**
	 * Constructor
	 */
	public XDustNode() {

	}

	private Map<String, XDustNode> blocks;

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
	public abstract String render(XDust dust, RenderChain chain,
			Context context, Object model);

	/**
	 * Gets a hash of available subcontent blocks
	 * 
	 * @return A hash of blocks
	 */
	public Map<String, XDustNode> getBlocks() {
		if (null == this.blocks) {
			this.blocks = new HashMap<String, XDustNode>();
		}
		return this.blocks;
	}

	/**
	 * Gets a specific subcontent block, given its name
	 * 
	 * @param name
	 *            The name of the block
	 * @return The block (or null if no such block exists)
	 */
	public XDustNode getBlock(String name) {
		if (this.hasBlock(name)) {
			return this.getBlocks().get(name).asBlock();
		} else {
			return null;
		}
	}

	/**
	 * Assigns a name to a block
	 * 
	 * @param name
	 *            The name of the block
	 * @param value
	 *            The block's root node
	 */
	public void setBlock(String name, XDustNode value) {
		this.getBlocks().put(name, value);
	}

	/**
	 * Indicates whether or not the node has a named block
	 * 
	 * @param name
	 *            The name of the block
	 * @return True if a block exists under that name
	 */
	public boolean hasBlock(String name) {
		return this.getBlocks().containsKey(name);
	}

	/**
	 * Gets this node as a block to be rendered
	 * 
	 * @return This node
	 */
	public XDustNode asBlock() {
		return this;
	}

}
