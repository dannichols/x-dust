package com.heydanno.xdust;

public class XDustEscapedCharacterNode extends XDustNode {

	private static final long serialVersionUID = 762983140385368858L;

	public XDustEscapedCharacterNode(String code) {
		super();
		this.setCode(code);
	}

	private String code;
	private String character;

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

	public String getCharacter() {
		return character;
	}

	private void setCharacter(String value) {
		this.character = value;
	}

	@Override
	public String toString() {
		return String.format("{~%s}", this.getCode());
	}

	@Override
	public String render(XDust dust, RenderChain chain, Context context,
			Object model) {
		return this.character;
	}

}
