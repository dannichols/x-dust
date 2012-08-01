package com.heydanno.xdust;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Defines a data context used by a template
 */
public class Context implements IScriptable, Serializable {

	private static final long serialVersionUID = -5834248967445485253L;

	/**
	 * Constructor
	 * 
	 * @param head
	 *            The parent context (if any)
	 * @param tail
	 *            The current data object (if any)
	 * @param parameters
	 *            Variables to set for the current context
	 */
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

	/**
	 * Gets the parent context
	 * 
	 * @return The parent context
	 */
	public Context getHead() {
		return this.head;
	}

	/**
	 * Sets the parent context
	 * 
	 * @param value
	 *            The parent context
	 */
	public void setHead(Context value) {
		this.head = value;
	}

	/**
	 * Gets the current context's model
	 * 
	 * @return The current context's model
	 */
	public IScriptable getTail() {
		return this.tail;
	}

	/**
	 * Sets the current context's model
	 * 
	 * @param value
	 *            The current context's model
	 */
	public void setTail(IScriptable value) {
		this.tail = value;
	}

	/**
	 * Gets the variables associated with the current context
	 * 
	 * @return The variables associated with the current context
	 */
	public Map<String, Object> getParameters() {
		if (null == this.parameters) {
			this.parameters = new HashMap<String, Object>();
		}
		return this.parameters;
	}

	/**
	 * Gets a raw value represented by the current context (if any)
	 * 
	 * @return The value
	 */
	public Object getValue() {
		if (null != this.tail) {
			return this.tail.getValue();
		} else if (null != this.head) {
			return this.head.getValue();
		} else {
			return null;
		}
	}

	/**
	 * Determines whether or not the current context represents a raw value
	 * 
	 * @return True if the current context is a raw value
	 */
	public boolean isValue() {
		if (null != this.tail) {
			return this.tail.isValue();
		} else if (null != this.head) {
			return this.head.isValue();
		} else {
			return false;
		}
	}

	/**
	 * Gets a value stored under a key from the current context, coming from
	 * either its parameters, its model, or its parent
	 * 
	 * @param name
	 *            The key under which the value is stored
	 * @return The value of that key
	 */
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

	/**
	 * Determines whether or not there's a value associated with a key in this
	 * context chain
	 * 
	 * @param name
	 *            The key
	 * @return True if there's a value for that key in this context chain
	 */
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

	/**
	 * Allows looping through this context's model
	 * 
	 * @return An iterator
	 */
	public Iterator<String> iterator() {
		if (this.tail.isValue()) {
			return null;
		} else {
			return this.tail.iterator();
		}
	}

	/**
	 * Indicates whether or not this context's model is a list
	 * 
	 * @return True if the context's model is a list
	 */
	public boolean isList() {
		return null != this.tail && this.tail.isList();
	}

	/**
	 * Indicates whether or not this context's model follows the JavaScript
	 * rules for truthy/falsey values
	 * 
	 * @return True if the context's model meets the JavaScript criteria for
	 *         true
	 */
	public boolean isTruthy() {
		return null != this.tail && this.tail.isTruthy();
	}

	/**
	 * Gets an iterable representation of this context's model
	 * 
	 * @return This context's model as a List
	 */
	public List<Object> asList() {
		return this.isList() ? this.tail.asList() : null;
	}

}
