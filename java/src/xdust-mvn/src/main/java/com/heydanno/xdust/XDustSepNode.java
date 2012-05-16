package com.heydanno.xdust;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class XDustSepNode extends XDustNodeList implements IXDustSectionNode {

	private static final long serialVersionUID = 2174671463957632924L;

	public XDustSepNode(Collection<XDustNode> nodes) {
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
		Object sep = scriptable.get("@sep");
		if (sep != null && (Boolean) sep) {
			return super.prepareModel(chain, context, model);
		} else {
			return null;
		}
	}

	@Override
	public String toString() {
		return String.format("{@sep}%s{/sep}", super.toString());
	}

	public XDustNodeList startBody(String name) {
		throw new UnsupportedOperationException();
	}

	public void endBody() {
		throw new UnsupportedOperationException();
	}

}
