package com.heydanno.xdust;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class XDustNodeList extends XDustNode implements Iterable<XDustNode> {

	private static final long serialVersionUID = -1575322991051873215L;

	public XDustNodeList(Collection<XDustNode> nodes) {
		super();
		if (null != nodes) {
			this.getNodes().addAll(nodes);
		}
	}

	private List<XDustNode> nodes;

	public List<XDustNode> getNodes() {
		if (null == this.nodes) {
			this.nodes = new ArrayList<XDustNode>();
		}
		return this.nodes;
	}

	public XDustNode add(XDustNode node) {
		this.getNodes().add(node);
		return node;
	}

	public XDustNode last() {
		int len = this.getNodes().size();
		if (len > 0) {
			return this.getNodes().get(len - 1);
		} else {
			return null;
		}
	}

	public Object prepareModel(RenderChain chain, Context context, Object model) {
		return new Context(context, model, null);
	}

	@Override
	public String render(XDust dust, RenderChain chain, Context context,
			Object model) {
		chain = new RenderChain(chain, this);
		model = this.prepareModel(chain, context, model);
		StringBuilder sb = new StringBuilder();
		if (null != model) {
			for (XDustNode node : this.getNodes()) {
				sb.append(node.render(dust, chain, context, model));
			}
		}
		return sb.toString();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (XDustNode node : this.getNodes()) {
			sb.append(node.toString());
		}
		return sb.toString();
	}

	public Iterator<XDustNode> iterator() {
		return this.getNodes().iterator();
	}

}
