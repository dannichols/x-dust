package com.heydanno.xdust;

public class RenderChain {
	
	public RenderChain(RenderChain head, XDustNode tail) {
		this.head = head;
		this.tail = tail;
	}
	
	private RenderChain head;
	private XDustNode tail;
	
	public RenderChain getHead() {
		return this.head;
	}

	public XDustNode getTail() {
		return this.tail;
	}

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
