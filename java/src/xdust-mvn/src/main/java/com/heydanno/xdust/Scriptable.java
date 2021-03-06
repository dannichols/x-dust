package com.heydanno.xdust;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Wrapper that allows an object to be worked with similarly to a JavaScript
 * object
 */
public class Scriptable implements IScriptable, Serializable {

	private static final long serialVersionUID = -6344366299280534777L;

	/**
	 * Wraps an object, if it isn't already
	 * 
	 * @param obj
	 *            The object to wrap
	 * @return The scriptable version of that object
	 */
	public static IScriptable from(Object obj) {
		if (null != obj && obj instanceof IScriptable) {
			return (IScriptable) obj;
		} else {
			return new Scriptable(obj);
		}
	}

	private Object target;
	private boolean targetIsValue;
	private boolean targetIsMap;
	private boolean targetIsNull;
	private boolean targetIsContext;
	private boolean targetIsList;

	private Scriptable(Object target) {
		if (null == target) {
			this.target = null;
			this.targetIsMap = false;
			this.targetIsContext = false;
			this.targetIsValue = false;
			this.targetIsNull = true;
			this.targetIsList = false;
		} else {
			this.target = target;
			this.targetIsContext = target instanceof Context;
			this.targetIsMap = target instanceof Map<?, ?>;
			this.targetIsList = !this.targetIsMap
					&& (target instanceof List<?> || target.getClass()
							.isArray());
			this.targetIsValue = this.targetIsList
					|| target.getClass().isPrimitive()
					|| target instanceof String || target instanceof Integer
					|| target instanceof Double || target instanceof Boolean
					|| target instanceof Long || target instanceof Float
					|| target instanceof Character || target instanceof Byte
					|| target instanceof Short || target instanceof Enum;
		}
	}

	/**
	 * Gets the raw value of the object
	 * 
	 * @return The raw value of the object
	 */
	public Object getValue() {
		if (this.targetIsValue) {
			return this.target;
		} else if (this.targetIsContext) {
			Context ctx = (Context) this.target;
			return ctx.getTail() == null ? null : ctx.getTail().getValue();
		} else {
			return null;
		}
	}

	/**
	 * Indicates whether or not this object is a raw value
	 * 
	 * @return True if the object is a raw value
	 */
	public boolean isValue() {
		if (this.targetIsContext && ((Context) this.target).getTail() != null) {
			return ((Context) this.target).getTail().isValue();
		} else {
			return this.targetIsValue;
		}
	}

	/**
	 * Indicates whether or not this object is a list
	 * 
	 * @return True if the object is a list
	 */
	public boolean isList() {
		return this.targetIsList;
	}

	/**
	 * Gets an iterable representation of the object
	 * 
	 * @return This object as a List
	 */
	@SuppressWarnings("unchecked")
	public List<Object> asList() {
		if (this.targetIsList) {
			Class<?> c = this.target.getClass();
			if (c.isArray()) {
				List<Object> list = new ArrayList<Object>();
				int len = java.lang.reflect.Array.getLength(this.target);
				for (int i = 0; i < len; i++) {
					list.add(java.lang.reflect.Array.get(this.target, i));
				}
				return list;
			} else {
				return (List<Object>) this.target;
			}
		} else {
			return null;
		}
	}

	/**
	 * Indicates whether or not this object meets the JavaScript criteria for
	 * truthfulness
	 * 
	 * @return True if the object meets the criteria
	 */
	@SuppressWarnings("unchecked")
	public boolean isTruthy() {
		if (this.targetIsNull) {
			return false;
		} else if (this.targetIsList) {
			return this.asList().size() > 0;
		} else if (this.targetIsMap) {
			return ((Map<String, Object>) this.target).size() > 0;
		} else if (this.targetIsValue) {
			if (this.target instanceof String) {
				return null != this.target
						&& ((String) this.target).length() > 0;
			} else if (this.target instanceof Integer
					|| this.target instanceof Double
					|| this.target instanceof Float
					|| this.target instanceof Short) {
				return (Double) this.target != 0;
			} else if (this.target instanceof Long) {
				return (Long) this.target != 0;
			} else {
				return true;
			}
		} else {
			return true;
		}
	}

	private Method getGetter(Object obj, String name) {
		String getterName;
		String firstChar = name.substring(0, 1).toUpperCase();
		if (name.length() > 1) {
			getterName = firstChar + name.substring(1);
		} else {
			getterName = firstChar;
		}
		Method[] methods = obj.getClass().getMethods();
		for (Method m : methods) {
			if ((m.getName().equals("get" + getterName) || m.getName().equals(
					"is" + getterName))
					&& m.getParameterTypes().length == 0) {
				return m;
			}
		}
		return null;
	}

	private Field getField(Object obj, String name) {
		for (Field f : obj.getClass().getFields()) {
			if (name.equals(f.getName())) {
				return f;
			}
		}
		return null;
	}

	/**
	 * Gets a property from the object, given its name
	 * 
	 * @param name
	 *            The name of the property
	 * @return The value of the property
	 */
	@SuppressWarnings("unchecked")
	public Object get(String name) {
		if (this.targetIsContext) {
			return ((Context) this.target).get(name);
		} else if (this.targetIsNull || this.targetIsValue) {
			return null;
		} else if (this.targetIsMap) {
			return ((Map<String, Object>) this.target).get(name);
		} else {
			Method m = this.getGetter(this.target, name);
			if (null != m) {
				try {
					return m.invoke(this.target);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
			Field f = this.getField(this.target, name);
			if (null != f) {
				try {
					return f.get(this.target);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		}
		return null;
	}

	/**
	 * Indicates whether or not the object has a property
	 * 
	 * @param name
	 *            The name of the property
	 * @return True if the object has a property with that name
	 */
	@SuppressWarnings("unchecked")
	public boolean hasKey(String name) {
		if (this.targetIsNull || this.targetIsValue) {
			return false;
		} else if (this.targetIsMap) {
			return ((Map<String, Object>) this.target).containsKey(name);
		} else if (this.targetIsContext) {
			return ((Context) this.target).get(name) != null;
		} else {
			return null != this.getGetter(this.target, name)
					|| null != this.getField(this.target, name);
		}
	}

	/**
	 * Produces an iterator for all of the keys in an object
	 * 
	 * @return The iterator
	 */
	@SuppressWarnings("unchecked")
	public Iterator<String> iterator() {
		if (this.targetIsNull || this.targetIsValue) {
			return null;
		} else if (this.targetIsMap) {
			return ((Map<String, Object>) this.target).keySet().iterator();
		} else {
			List<String> names = new ArrayList<String>();
			Class<?> c = this.target.getClass();
			for (Field f : c.getFields()) {
				names.add(f.getName());
			}
			for (Method m : c.getMethods()) {
				String name = m.getName();
				if (name.startsWith("get") && name.length() > 3
						&& m.getParameterTypes().length == 0) {
					String firstChar = Character.toString(name.charAt(3))
							.toUpperCase();
					names.add(firstChar + name.substring(4));
				}
			}
			return names.iterator();
		}
	}

	/**
	 * Serializes the object to string
	 * 
	 * @return The string representation of the object
	 */
	@Override
	public String toString() {
		if (this.targetIsNull) {
			return "";
		} else {
			return this.target.toString();
		}
	}

}
