package com.heydanno.xdust;

import java.util.HashMap;
import java.util.Map;

public abstract class XDustNode {
	
	public XDustNode() {
		
	}
	
	private Map<String, XDustNode> blocks;
	
	public abstract String render(XDust dust, RenderChain chain, Context context, Object model) throws Exception;
	
	public Map<String, XDustNode> getBlocks() {
		if (null == this.blocks) {
			this.blocks = new HashMap<String, XDustNode>();
		}
		return this.blocks;
	}
	
	public XDustNode getBlock(String name) {
		if (this.hasBlock(name)) {
			return this.getBlocks().get(name).asBlock();
		} else {
			return null;
		}
	}
	
	public void setBlock(String name, XDustNode value) {
		this.getBlocks().put(name, value);
	}
	
	public boolean hasBlock(String name) {
		return this.getBlocks().containsKey(name);
	}
	
	public XDustNode asBlock() {
		return this;
	}

}
