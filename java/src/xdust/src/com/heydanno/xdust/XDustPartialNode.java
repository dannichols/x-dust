package com.heydanno.xdust;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class XDustPartialNode extends XDustNode implements IXDustSectionNode {

	private static final long serialVersionUID = 2564920555706288058L;

	public XDustPartialNode(Object include, String scope) {
		super();
		this.setInclude(include);
		if (null != scope) {
			this.setScope(new ContextResolver(scope));
		}
	}

	private Object include;
	private ContextResolver scope;
	private Map<String, XDustNode> parameters;

	public Object getInclude() {
		return include;
	}

	public void setInclude(Object include) {
		this.include = include;
	}

	public ContextResolver getScope() {
		return scope;
	}

	public void setScope(ContextResolver scope) {
		this.scope = scope;
	}

	public Map<String, XDustNode> getParameters() {
		if (null == this.parameters) {
			this.parameters = new HashMap<String, XDustNode>();
		}
		return this.parameters;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("{>");
		if (this.getInclude() instanceof XDustNodeList) {
			sb.append("\"");
		}
		sb.append(this.getInclude());
		if (this.getInclude() instanceof XDustNodeList) {
			sb.append("\"");
		}
		if (null != this.getScope()) {
			sb.append(":").append(this.getScope());
		}
		sb.append("/}");
		return sb.toString();
	}

	@Override
	public String render(XDust dust, RenderChain chain, Context context,
			Object model) {
		if (null != this.getScope()) {
			model = this.getScope().resolve(context, model);
		}
		chain = new RenderChain(chain, this);
		String name;
		if (this.getInclude() instanceof XDustNodeList) {
			name = ((XDustNodeList) this.getInclude()).render(dust, chain,
					context, model);
		} else {
			name = this.getInclude().toString();
		}
		XDustNode template;
		try {
			template = dust.load(null, name);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return template.render(dust, chain, context, model);
	}

	public XDustNodeList startBody(String name) {
		throw new UnsupportedOperationException();
	}

	public void endBody() {
		throw new UnsupportedOperationException();
	}

}
