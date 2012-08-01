package com.heydanno.xdust;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Node for looping and value testing
 */
public class XDustLogicNode extends XDustNode implements IXDustSectionNode {

	private static final long serialVersionUID = 2169863162076222163L;

	/**
	 * The name of the truthy block
	 */
	public static final String BLOCK = "block";
	/**
	 * The name of the falsy block
	 */
	public static final String ELSE = "else";

	/**
	 * Constructor
	 * 
	 * @param path
	 *            The path to resolve against the data context
	 * @param scope
	 *            An alternate path to select the block's data context
	 * @param parameters
	 *            Variables to define for the block
	 */
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

	/**
	 * Gets the dust operator character associated with this node type
	 * 
	 * @return The operator character
	 */
	public String getOperator() {
		return "#";
	}

	/**
	 * Whether or not this type of logic node can function as a loop
	 * 
	 * @return True if the node allows iteration
	 */
	public boolean getAllowIteration() {
		return true;
	}

	/**
	 * Gets the named blocks that the logic node switches between
	 * 
	 * @return A map of the blocks
	 */
	public Map<String, XDustNodeList> getBodies() {
		if (null == this.bodies) {
			this.bodies = new HashMap<String, XDustNodeList>();
		}
		return this.bodies;
	}

	/**
	 * Gets the context resolver used to determine what data point is tested
	 * 
	 * @return The context resolver
	 */
	public ContextResolver getContext() {
		return this.context;
	}

	/**
	 * Gets the context resolver used to determine the data context of the body
	 * that's rendered
	 * 
	 * @return The context resolver
	 */
	public ContextResolver getScope() {
		return this.scope;
	}

	/**
	 * Gets the currently open body
	 * 
	 * @return The body
	 */
	public XDustNodeList getCurrentBody() {
		return this.currentBody;
	}

	/**
	 * Gets a map of variables that's passed to the body
	 * 
	 * @return The map of parameters
	 */
	public Map<String, XDustNode> getParameters() {
		if (null == this.parameters) {
			this.parameters = new HashMap<String, XDustNode>();
		}
		return this.parameters;
	}

	/**
	 * Serializes the node to string
	 * 
	 * @return The code string
	 */
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

	/**
	 * Begins a new body
	 * 
	 * @param The
	 *            name of the body
	 * @return The root node of the body
	 */
	public XDustNodeList startBody(String name) {
		XDustNodeList body = new XDustNodeList(null);
		this.currentBody = body;
		this.getBodies().put(name, body);
		return body;
	}

	/**
	 * Seals the current body
	 */
	public void endBody() {
		this.currentBody = null;
	}

	/**
	 * Readies a model to be rendered
	 * 
	 * @param dust
	 *            The current dust render engine instance
	 * @param chain
	 *            The current chain of nodes being rendered
	 * @param context
	 *            The current data context
	 * @param model
	 *            The current data model
	 * @return The prepared context object
	 */
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

	/**
	 * Renders a body to string
	 * 
	 * @param dust
	 *            The current dust render engine instance
	 * @param name
	 *            The name of the body to render
	 * @param chain
	 *            The current chain of nodes being rendered
	 * @param context
	 *            The current data context
	 * @param model
	 *            The current data model
	 * @return The rendered body
	 */
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

	/**
	 * Determines whether or not an object fits the standard of truthfulness
	 * 
	 * @param obj
	 *            The object to test
	 * @return True when the object is deemed truthy
	 */
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

	/**
	 * Selects the name of the body to render
	 * 
	 * @param context
	 *            The current data context
	 * @param model
	 *            The current data model
	 * @return The name of the body to render
	 */
	public String chooseBodyName(Context context, Object model) {
		Object resolved = this.getContext().resolve(context, model);
		return this.isTruthy(resolved) ? BLOCK : ELSE;
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
		context = new Context(context, null, this.getParameters());
		String bodyName = this.chooseBodyName(context, model);
		return this.renderBody(dust, bodyName, chain, context, model);
	}

}
