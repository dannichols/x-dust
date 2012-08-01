package com.heydanno.xdust;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Utility for resolving a path against a context
 */
public class ContextResolver implements Serializable {

	private static final long serialVersionUID = -5143410657368012472L;

	/**
	 * Constructs a new context resolver for a path
	 * 
	 * @param path
	 *            The path to a value within a context chain
	 */
	public ContextResolver(String path) {
		if (path.startsWith(".")) {
			this.getPath().add(".");
			String[] splitPath = path.split("\\.");
			for (int i = 1; i < splitPath.length; i++) {
				this.getPath().add(splitPath[i]);
			}
		} else {
			for (String s : path.split("\\.")) {
				this.getPath().add(s);
			}
		}
	}

	private List<String> path;

	/**
	 * Gets the path used by this resolver
	 * 
	 * @return The path
	 */
	public List<String> getPath() {
		if (null == this.path) {
			this.path = new ArrayList<String>();
		}
		return this.path;
	}

	/**
	 * Serializes the context resolver to a string
	 * 
	 * @return The string
	 */
	@Override
	public String toString() {
		List<String> path = new ArrayList<String>(this.getPath());
		StringBuilder sb = new StringBuilder();
		Iterator<String> iter = path.iterator();
		while (iter.hasNext()) {
			String current = iter.next();
			sb.append(current);
			if (iter.hasNext() && !".".equals(current)) {
				sb.append(".");
			}
		}
		return sb.toString();
	}

	/**
	 * Tests the path against a model and then against a context chain,
	 * producing the result
	 * 
	 * @param context
	 *            The context chain to test
	 * @param model
	 *            The data model (the last item in the context chain)
	 * @return The found value or null if no value exists on the path
	 */
	public Object resolve(Context context, Object model) {
		Object copy = new Context(context, model, null);
		if (null != model) {
			if (model instanceof Collection<?>) {
				copy = new ArrayList<Object>((Collection<?>) model);
			} else if (model.getClass().isArray()) {
				copy = new ArrayList<Object>(Arrays.asList((Object[]) model));
			} else if (model instanceof Context && ((Context) model).isList()) {
				copy = ((Context) model).asList();
			} else {
				for (String segment : this.getPath()) {
					Context ctx = (Context) copy;
					if (null != segment && !"".equals(segment)) {
						if (".".equals(segment)) {
							if (ctx.getTail().isValue()) {
								copy = ctx.getTail().getValue();
								break;
							} else {
								ctx.setHead(null);
							}
						} else {
							Object value = ctx.get(segment);
							if (null != value) {
								ctx = new Context(copy, value, null);
								if (ctx.getTail().isValue()) {
									copy = ctx.getTail();
								} else {
									copy = ctx;
								}
							} else {
								copy = null;
								break;
							}
						}
					} else {
						copy = ctx.getTail();
						break;
					}
				}
			}
		} else {
			copy = null;
		}
		return copy;
	}

}
