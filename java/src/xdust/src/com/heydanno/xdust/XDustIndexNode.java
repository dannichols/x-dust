package com.heydanno.xdust;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class XDustIndexNode extends XDustNodeList implements IXDustSectionNode {

	private static final long serialVersionUID = -7070011746376720226L;

	public XDustIndexNode(Collection<XDustNode> nodes) {
		super(nodes);
	}

	private Map<String, XDustNode> parameters;

	public Map<String, XDustNode> getParameters() {
		if (null == this.parameters) {
			this.parameters = new HashMap<String, XDustNode>();
		}
		return this.parameters;
	}

	@Override
	public Object prepareModel(RenderChain chain, Context context, Object model) {
		IScriptable scriptable = Scriptable.from(model);
		try {
			if (scriptable.get("@idx") != null) {
				return scriptable.get("@idx").toString();
			} else {
				return null;
			}
		} catch (IllegalArgumentException e) {
			return null;
		}
	}

	@Override
	public String toString() {
		return String.format("{@idx}%s{/idx}", super.toString());
	}

	public XDustNodeList startBody(String name) {
		throw new UnsupportedOperationException();
	}

	public void endBody() {
		throw new UnsupportedOperationException();
	}

}
