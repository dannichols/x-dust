package com.heydanno.xdust;

import java.util.Map;

public class XDustExistsNode extends XDustLogicNode {

	private static final long serialVersionUID = 4129001443943748282L;

	public XDustExistsNode(String path, String scope,
			Map<String, XDustNode> parameters) {
		super(path, scope, parameters);
	}

	@Override
	public String getOperator() {
		return "?";
	}

	@Override
	public boolean getAllowIteration() {
		return false;
	}

	@Override
	public Context prepareModel(XDust dust, RenderChain chain, Context context,
			Object model) {
		return new Context(context, context, this.getParameters());
	}

}
