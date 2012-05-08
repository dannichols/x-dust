package com.heydanno.xdust;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class XDustVariableNode extends XDustNode {
	
	public XDustVariableNode(String path, Collection<String> filters) {
		super();
		this.context = new ContextResolver(path);
		this.filters = new ArrayList<String>();
		if (null != filters) {
			this.filters.addAll(filters);
		}
	}
	
	private ContextResolver context;
	private List<String> filters;
	
	public ContextResolver getContext() {
		return context;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("{");
		sb.append(this.context.toString());
		Iterator<String> iter = this.filters.iterator();
		while (iter.hasNext()) {
			sb.append("|");
			sb.append(iter.next());
		}
		sb.append("}");
		return sb.toString();
	}

	@Override
	public String render(XDust dust, RenderChain chain, Context context,
			Object model) throws Exception {
		chain = new RenderChain(chain, this);
		Object originalModel = model;
		model = this.context.resolve(context, model);
		String result = "";
		if (null != model) {
			if (model instanceof XDustNode) {
				result = ((XDustNode)model).render(dust, chain, context, originalModel);
			} else if (model instanceof ContextResolver) {
				Object temp = ((ContextResolver)model).resolve(context, originalModel);
				if (null != temp) {
					result = temp.toString();
				}
			} else if (model instanceof IScriptable) {
				Object temp = ((IScriptable)model).getValue();
				if (null != temp) {
					result = temp.toString();
				}
			} else {
				result = model.toString();
			}
			if (!this.filters.isEmpty()) {
				for (String flag : this.filters) {
					result = dust.getFilters().get(flag).call(dust, result);
				}
			} else {
				dust.escapeHTML(result);
			}
		}
		return result;
	}

}
