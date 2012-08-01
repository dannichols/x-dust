package com.heydanno.xdust;

import java.io.Serializable;

/**
 * A series of nodes being processed
 */
public class RenderChain implements Serializable {

	private static final long serialVersionUID = 3631059447121501608L;

	/**
	 * Constructs a render chain
	 * @param head The parent chain (if any)
	 * @param tail The newest link in the chain
	 */
	public RenderChain(RenderChain head, XDustNode tail) {
		this.head = head;
		this.tail = tail;
	}

	private RenderChain head;
	private XDustNode tail;

	/**
	 * Gets the parent chain
	 * @return The parent chain (or null)
	 */
	public RenderChain getHead() {
		return this.head;
	}

	/**
	 * Gets the most recent link in the chain
	 * @return The most recent link in the chain
	 */
	public XDustNode getTail() {
		return this.tail;
	}

	/**
	 * Gets a named block from the chain
	 * 
	 * @param name
	 *            The name of the block
	 * @return The block (or null)
	 */
	public XDustNode getBlock(String name) {
		XDustNode block = this.tail.getBlock(name);
		if (null != block) {
			return block;
		} else if (null != this.head) {
			return this.head.getBlock(name);
		} else {
			return null;
		}
	}

}
