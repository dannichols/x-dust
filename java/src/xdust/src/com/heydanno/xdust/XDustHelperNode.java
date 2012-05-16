package com.heydanno.xdust;

import java.util.Map;

public class XDustHelperNode extends XDustLogicNode {

	private static final long serialVersionUID = -3046616917519170745L;

	public XDustHelperNode(String name, String scope,
			Map<String, XDustNode> parameters) {
		super(null, scope, parameters);
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
	public boolean getAllowIteration() {
		return false;
	}

	@Override
	public String render(XDust dust, RenderChain chain, Context context,
			Object model) {
		chain = new RenderChain(chain, this);
		context = new Context(context, null, this.getParameters());
		IXDustHelper helper = dust.getHelpers().get(this.getName());
		return helper.render(dust, chain, context, model);
	}

}
