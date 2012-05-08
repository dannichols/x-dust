package com.heydanno.xdust;

import java.util.HashMap;
import java.util.Map;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class XDustInlinePartialNode extends XDustNodeList implements
		IXDustSectionNode {

	public XDustInlinePartialNode(String name) {
		super(null);
		this.setName(name);
	}

	private String name;
	private Map<String, XDustNode> parameters;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public Map<String, XDustNode> getParameters() {
		if (null == this.parameters) {
			this.parameters = new HashMap<String, XDustNode>();
		}
		return this.parameters;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{<").append(this.getName()).append("}");
		sb.append(super.toString());
		sb.append("{/").append(this.getName()).append("}");
		return sb.toString();
	}

	@Override
	public XDustNode asBlock() {
		return new XDustNodeList(this.getNodes());
	}

	@Override
	public String render(XDust dust, RenderChain chain, Context context,
			Object model) throws Exception {
		return "";
	}

	@Override
	public XDustNodeList startBody(String name) {
		throw new NotImplementedException();
	}

	@Override
	public void endBody() {
		throw new NotImplementedException();
	}

}