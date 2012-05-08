package com.heydanno.xdust;

public class XDustTemplate {
	
	public XDustTemplate(String name, XDustNode rootNode, String sourceFile) {
		this.name = name;
		this.rootNode = rootNode;
		this.sourceFile = sourceFile;
	}
	
	private String name;
	private XDustNode rootNode;
	private String sourceFile;
	
	public String getName() {
		return name;
	}
	public XDustNode getRootNode() {
		return rootNode;
	}
	public String getSourceFile() {
		return sourceFile;
	}
	
	public String render(XDust dust, RenderChain chain, Context context, Object model) throws Exception {
		return this.rootNode.render(dust, chain, context, model);
	}

}
