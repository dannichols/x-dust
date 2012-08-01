package com.heydanno.xdust;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Node that outputs the value of a variable
 */
public class XDustVariableNode extends XDustNode {

	private static final long serialVersionUID = 3217038974087935258L;

	/**
	 * Constructor
	 * 
	 * @param path
	 *            The path to the variable
	 * @param filters
	 *            A list of the names of filters to be applied to the variable
	 */
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

	/**
	 * Gets the context resolver used to find the variable
	 * 
	 * @return The context resolver
	 */
	public ContextResolver getContext() {
		return context;
	}

	/**
	 * Serializes the node to string
	 * 
	 * @return The code string
	 */
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

	/**
	 * Renders the node to string
	 * 
	 * @param dust
	 *            The current render engine
	 * @param chain
	 *            The current chain of nodes being rendered
	 * @param context
	 *            The chain's data context
	 * @param model
	 *            The current data
	 * @return The rendered output
	 */
	@Override
	public String render(XDust dust, RenderChain chain, Context context,
			Object model) {
		chain = new RenderChain(chain, this);
		Object originalModel = model;
		model = this.context.resolve(context, model);
		String result = "";
		if (null != model) {
			if (model instanceof XDustNode) {
				result = ((XDustNode) model).render(dust, chain, context,
						originalModel);
			} else if (model instanceof ContextResolver) {
				Object temp = ((ContextResolver) model).resolve(context,
						originalModel);
				if (null != temp) {
					result = temp.toString();
				}
			} else if (model instanceof IScriptable) {
				Object temp = ((IScriptable) model).getValue();
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
