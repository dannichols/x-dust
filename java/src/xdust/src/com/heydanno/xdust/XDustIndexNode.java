package com.heydanno.xdust;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class XDustIndexNode extends XDustNodeList implements IXDustSectionNode {

	public XDustIndexNode(Collection<XDustNode> nodes) {
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
		try {
			if (scriptable.get("@idx") != null) {
				return scriptable.get("@idx").toString();
			} else {
				return null;
			}
		} catch (IllegalArgumentException e) {
			return null;
		} catch (IllegalAccessException e) {
			return null;
		} catch (InvocationTargetException e) {
			return null;
		}
	}
	
	@Override
	public String toString() {
		return String.format("{@idx}%s{/idx}", super.toString());
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
