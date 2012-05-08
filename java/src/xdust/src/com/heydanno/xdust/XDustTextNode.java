package com.heydanno.xdust;

public class XDustTextNode extends XDustNode {
	
	public XDustTextNode(String value) {
		this.buffer = new StringBuilder();
		if (null != value) {
			this.buffer.append(value);
		}
	}
	
	private StringBuilder buffer;
	private String value;
	
	public void write(Object value) {
		this.buffer.append(value);
	}
	
	public void close() {
		if (null != this.buffer) {
			this.value = this.buffer.toString();
			this.buffer = null;
		}
	}
	
	@Override
	public String render(XDust dust, RenderChain chain, Context context,
			Object model) {
		return this.toString();
	}
	
	@Override
	public String toString() {
		this.close();
		return this.value;
	}

}
