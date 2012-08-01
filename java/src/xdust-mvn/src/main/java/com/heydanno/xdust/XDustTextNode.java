package com.heydanno.xdust;

/**
 * Node that renders plain text
 */
public class XDustTextNode extends XDustNode {

	private static final long serialVersionUID = 1386807051140750553L;

	/**
	 * Constructor
	 * 
	 * @param value
	 *            The text value
	 */
	public XDustTextNode(String value) {
		this.buffer = new StringBuilder();
		if (null != value) {
			this.buffer.append(value);
		}
	}

	private StringBuilder buffer;
	private String value;

	/**
	 * Adds text to this node
	 * 
	 * @param value
	 *            The text to add
	 */
	public void write(Object value) {
		this.buffer.append(value);
	}

	/**
	 * Prevents further text from being added to this node
	 */
	public void close() {
		if (null != this.buffer) {
			this.value = this.buffer.toString();
			this.buffer = null;
		}
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
		return this.toString();
	}

	/**
	 * Serializes the node to string
	 * 
	 * @return The code string
	 */
	@Override
	public String toString() {
		this.close();
		return this.value;
	}

}
