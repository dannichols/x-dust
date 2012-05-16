package com.heydanno.xdust;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XDustLogicNode extends XDustNode implements IXDustSectionNode {

	private static final long serialVersionUID = 2169863162076222163L;

	public static final String BLOCK = "block";
	public static final String ELSE = "else";

	public XDustLogicNode(String path, String scope,
			Map<String, XDustNode> parameters) {
		super();
		this.startBody(BLOCK);
		if (null != parameters) {
			for (String key : parameters.keySet()) {
				this.getParameters().put(key, parameters.get(key));
			}
		}
		if (null != path && !"".equals(path)) {
			this.context = new ContextResolver(path);
		}
		if (null != scope && !"".equals(scope)) {
			this.scope = new ContextResolver(scope);
		}
	}

	private Map<String, XDustNodeList> bodies;
	private Map<String, XDustNode> parameters;
	private ContextResolver context;
	private ContextResolver scope;
	private XDustNodeList currentBody;

	public String getOperator() {
		return "#";
	}

	public boolean getAllowIteration() {
		return true;
	}

	public Map<String, XDustNodeList> getBodies() {
		if (null == this.bodies) {
			this.bodies = new HashMap<String, XDustNodeList>();
		}
		return this.bodies;
	}

	public ContextResolver getContext() {
		return this.context;
	}

	public ContextResolver getScope() {
		return this.scope;
	}

	public XDustNodeList getCurrentBody() {
		return this.currentBody;
	}

	public Map<String, XDustNode> getParameters() {
		if (null == this.parameters) {
			this.parameters = new HashMap<String, XDustNode>();
		}
		return this.parameters;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("{");
		sb.append(this.getOperator()).append(this.getContext().toString());
		if (null != this.getScope()) {
			sb.append(":").append(this.getScope().toString());
		}
		for (String key : this.getParameters().keySet()) {
			sb.append(" ").append(key).append("=");
			Object value = this.getParameters().get(key);
			if (value instanceof XDustNodeList) {
				sb.append("\"").append(value.toString()).append("\"");
			} else if (value instanceof XDustVariableNode) {
				sb.append(((XDustVariableNode) value).getContext());
			} else if (value instanceof XDustTextNode) {
				sb.append("\"").append(value).append("\"");
			} else {
				sb.append(value);
			}
		}
		sb.append("}").append(this.getBodies().get(BLOCK));
		for (String key : this.getBodies().keySet()) {
			if (!BLOCK.equals(key)) {
				Object value = this.getBodies().get(key);
				sb.append("{:").append(key).append("}").append(value);
			}
		}
		sb.append("{/").append(this.getContext()).append("}");
		return sb.toString();
	}

	public XDustNodeList startBody(String name) {
		XDustNodeList body = new XDustNodeList(null);
		this.currentBody = body;
		this.getBodies().put(name, body);
		return body;
	}

	public void endBody() {
		this.currentBody = null;
	}

	public Context prepareModel(XDust dust, RenderChain chain, Context context,
			Object model) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		for (String key : this.getParameters().keySet()) {
			parameters.put(
					key,
					this.getParameters().get(key)
							.render(dust, chain, null, context));
		}
		Context result = new Context(context, model, parameters);
		return result;
	}

	public String renderBody(XDust dust, String name, RenderChain chain,
			Context context, Object model) {
		StringBuilder sb = new StringBuilder();
		XDustNodeList body = this.getBodies().containsKey(name) ? this
				.getBodies().get(name) : null;
		context = new Context(context, model, this.getParameters());
		if (null != this.getScope()) {
			model = this.getScope().resolve(context, model);
		} else if (null != this.getContext()) {
			model = this.getContext().resolve(context, model);
		}
		if (null != body) {
			chain = new RenderChain(chain, body);
			if (this.getAllowIteration()
					&& (model instanceof List<?> || (model instanceof IScriptable && ((IScriptable) model)
							.isList()))) {
				List<?> list = model instanceof IScriptable ? ((IScriptable) model)
						.asList() : (List<?>) model;
				int length = list.size();
				if (ELSE.equals(name) && length < 1) {
					sb.append(body.render(dust, chain, context, null));
				} else {
					for (int i = 0; i < length; i++) {
						Context iterModel = this.prepareModel(dust, chain,
								context, list.get(i));
						iterModel.getParameters().put("@idx", i);
						iterModel.getParameters().put("@sep", i != length - 1);
						sb.append(body.render(dust, chain, context, iterModel));
					}
				}
			} else {
				Context iterModel = this.prepareModel(dust, chain, context,
						model);
				sb.append(body.render(dust, chain, context, iterModel));
			}
		}
		return sb.toString();
	}

	protected boolean isTruthy(Object obj) {
		if (null == obj) {
			return false;
		} else if (obj instanceof List<?> && ((List<?>) obj).size() < 1) {
			return false;
		} else if (obj instanceof Boolean && !(Boolean) obj) {
			return false;
		} else if (obj instanceof String && !"".equals(obj)) {
			return false;
		} else if (obj instanceof IScriptable) {
			return ((IScriptable) obj).isTruthy();
		} else {
			return true;
		}
	}

	public String chooseBodyName(Context context, Object model) {
		Object resolved = this.getContext().resolve(context, model);
		return this.isTruthy(resolved) ? BLOCK : ELSE;
	}

	@Override
	public String render(XDust dust, RenderChain chain, Context context,
			Object model) {
		chain = new RenderChain(chain, this);
		context = new Context(context, null, this.getParameters());
		String bodyName = this.chooseBodyName(context, model);
		return this.renderBody(dust, bodyName, chain, context, model);
	}

}
