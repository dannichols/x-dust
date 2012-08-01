package com.heydanno.xdust;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Node containing multiple other nodes that render in sequence
 */
public class XDustNodeList extends XDustNode implements Iterable<XDustNode> {

	private static final long serialVersionUID = -1575322991051873215L;

	/**
	 * Constructor
	 * 
	 * @param nodes
	 *            The collection of nodes that make up this node list
	 */
	public XDustNodeList(Collection<XDustNode> nodes) {
		super();
		if (null != nodes) {
			this.getNodes().addAll(nodes);
		}
	}

	private List<XDustNode> nodes;

	/**
	 * Gets the child nodes of this list
	 * 
	 * @return The child nodes
	 */
	public List<XDustNode> getNodes() {
		if (null == this.nodes) {
			this.nodes = new ArrayList<XDustNode>();
		}
		return this.nodes;
	}

	/**
	 * Appends a node to this list
	 * 
	 * @param node
	 *            The node to append
	 * @return The appeneded node
	 */
	public XDustNode add(XDustNode node) {
		this.getNodes().add(node);
		return node;
	}

	/**
	 * Gets the last child node in this list
	 * 
	 * @return The last child (or null if the list is empty)
	 */
	public XDustNode last() {
		int len = this.getNodes().size();
		if (len > 0) {
			return this.getNodes().get(len - 1);
		} else {
			return null;
		}
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
	public Object prepareModel(RenderChain chain, Context context, Object model) {
		return new Context(context, model, null);
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
		model = this.prepareModel(chain, context, model);
		StringBuilder sb = new StringBuilder();
		if (null != model) {
			for (XDustNode node : this.getNodes()) {
				sb.append(node.render(dust, chain, context, model));
			}
		}
		return sb.toString();
	}

	/**
	 * Serializes the node to string
	 * 
	 * @return The code string
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (XDustNode node : this.getNodes()) {
			sb.append(node.toString());
		}
		return sb.toString();
	}

	/**
	 * Gets an iterator to loop through this list
	 * 
	 * @return The iterator
	 */
	public Iterator<XDustNode> iterator() {
		return this.getNodes().iterator();
	}

}
