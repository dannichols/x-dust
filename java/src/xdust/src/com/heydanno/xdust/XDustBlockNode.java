package com.heydanno.xdust;


public class XDustBlockNode extends XDustNodeList {

	public XDustBlockNode(String name) {
		super(null);
		this.setName(name);
	}
	
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{+").append(this.getName()).append("}");
		sb.append(super.toString());
		sb.append("{/").append(this.getName()).append("}");
		return sb.toString();
	}
	
	@Override
	public String render(XDust dust, RenderChain chain, Context context, Object model) throws Exception {
		XDustNode block = chain.getBlock(this.getName());
		if (null != block) {
			return block.render(dust, chain, context, model);
		} else {
			return super.render(dust, chain, context, model);
		}
	}

}
