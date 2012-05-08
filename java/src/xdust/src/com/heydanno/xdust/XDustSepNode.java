package com.heydanno.xdust;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class XDustSepNode extends XDustNodeList implements IXDustSectionNode {

	public XDustSepNode(Collection<XDustNode> nodes) {
		super(nodes);
	}
	
	private Map<String, XDustNode> parameters;

	@Override
	public Map<String, XDustNode> getParameters() {
		if (null == this.parameters) {
			this.parameters = new HashMap<String, XDustNode>();
		}
		return this.parameters;
	}
	
	@Override
	public Object prepareModel(RenderChain chain, Context context, Object model) {
		IScriptable scriptable = Scriptable.from(model);
		Object sep;
		try {
			sep = scriptable.get("@sep");
		} catch (IllegalArgumentException e) {
			return null;
		} catch (IllegalAccessException e) {
			return null;
		} catch (InvocationTargetException e) {
			return null;
		}
		if (sep != null && (Boolean)sep) {
			return super.prepareModel(chain, context, model);
		} else {
			return null;
		}
	}
	
	@Override
	public String toString() {
		return String.format("{@sep}%s{/sep}", super.toString());
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
