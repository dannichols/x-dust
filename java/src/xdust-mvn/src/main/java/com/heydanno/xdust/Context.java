package com.heydanno.xdust;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Context implements IScriptable, Serializable {

	private static final long serialVersionUID = -5834248967445485253L;

	public Context(Object head, Object tail, Map<String, ?> parameters) {
		if (null != parameters) {
			for (String key : parameters.keySet()) {
				this.getParameters().put(key, parameters.get(key));
			}
		}
		this.tail = Scriptable.from(tail);
	}

	private Context head;
	private IScriptable tail;
	private Map<String, Object> parameters;

	public Context getHead() {
		return this.head;
	}

	public void setHead(Context value) {
		this.head = value;
	}

	public IScriptable getTail() {
		return this.tail;
	}

	public void setTail(IScriptable value) {
		this.tail = value;
	}

	public Map<String, Object> getParameters() {
		if (null == this.parameters) {
			this.parameters = new HashMap<String, Object>();
		}
		return this.parameters;
	}

	public Object getValue() {
		if (null != this.tail) {
			return this.tail.getValue();
		} else if (null != this.head) {
			return this.head.getValue();
		} else {
			return null;
		}
	}

	public boolean isValue() {
		if (null != this.tail) {
			return this.tail.isValue();
		} else if (null != this.head) {
			return this.head.isValue();
		} else {
			return false;
		}
	}

	public Object get(String name) {
		if (".".equals(name)) {
			return this.getValue();
		} else if (this.tail.hasKey(name)) {
			return this.tail.get(name);
		} else if (this.getParameters().containsKey(name)) {
			return this.getParameters().get(name);
		} else if (null != this.head) {
			return this.head.get(name);
		} else {
			return null;
		}
	}

	public boolean hasKey(String name) {
		if (null != this.parameters && this.parameters.containsKey(name)) {
			return true;
		} else if (null != this.tail && this.tail.hasKey(name)) {
			return true;
		} else if (null != this.head && this.head.hasKey(name)) {
			return true;
		} else {
			return false;
		}
	}

	public Iterator<String> iterator() {
		if (this.tail.isValue()) {
			return null;
		} else {
			return this.tail.iterator();
		}
	}

	public boolean isList() {
		return null != this.tail && this.tail.isList();
	}

	public boolean isTruthy() {
		return null != this.tail && this.tail.isTruthy();
	}

	public List<Object> asList() {
		return this.isList() ? this.tail.asList() : null;
	}

}
