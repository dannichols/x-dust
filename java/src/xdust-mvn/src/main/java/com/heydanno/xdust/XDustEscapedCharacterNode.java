package com.heydanno.xdust;

/**
 * Node representing a single escaped character
 */
public class XDustEscapedCharacterNode extends XDustNode {

	private static final long serialVersionUID = 762983140385368858L;

	/**
	 * Constructor
	 * 
	 * @param code
	 *            The character code
	 */
	public XDustEscapedCharacterNode(String code) {
		super();
		this.setCode(code);
	}

	private String code;
	private String character;

	/**
	 * Gets the character code
	 * 
	 * @return The character code
	 */
	public String getCode() {
		return code;
	}

	private void setCode(String code) {
		this.code = code;
		if ("n".equals(code)) {
			this.setCharacter("\n");
		} else if ("r".equals(code)) {
			this.setCharacter("\r");
		} else if ("s".equals(code)) {
			this.setCharacter(" ");
		} else if ("lb".equals(code)) {
			this.setCharacter("{");
		} else if ("rb".equals(code)) {
			this.setCharacter("}");
		} else {
			throw new UnsupportedOperationException();
		}
	}

	/**
	 * Gets the escaped sequence
	 * 
	 * @return The escaped sequence
	 */
	public String getCharacter() {
		return character;
	}

	private void setCharacter(String value) {
		this.character = value;
	}

	/**
	 * Serializes the node to string
	 * 
	 * @return The code string
	 */
	@Override
	public String toString() {
		return String.format("{~%s}", this.getCode());
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
		return this.character;
	}

}
