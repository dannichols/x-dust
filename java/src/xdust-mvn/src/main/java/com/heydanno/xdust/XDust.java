package com.heydanno.xdust;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * The x-dust rendering engine
 */
public class XDust {

	private class NoOpFilter implements IXDustFilter {

		public String call(XDust dust, Object obj) {
			return obj == null ? "" : obj.toString();
		}

	}

	private class EscapeHTMLFilter implements IXDustFilter {

		public String call(XDust dust, Object obj) {
			String str = obj == null ? "" : obj.toString();
			Pattern pattern = Pattern.compile("[&<>\\\"]");
			if (pattern.matcher(str).matches()) {
				return str;
			} else {
				return str.replace("&", "&amp;").replace("<", "&lt;")
						.replace(">", "&gt;").replace("\"", "&quot;")
						.replace("'", "&squot;");
			}
		}

	}

	private class EscapeJSFilter implements IXDustFilter {

		public String call(XDust dust, Object obj) {
			String str = obj == null ? "" : obj.toString();
			return str.replace("\\", "\\\\").replace("\"", "\\\"")
					.replace("'", "\\'").replace("\r", "\\r")
					.replace("\u2028", "\\u2028").replace("\u2029", "\\u2029")
					.replace("\n", "\\n").replace("\f", "\\f")
					.replace("\t", "\\t");
		}

	}

	private class EscapeURIFilter implements IXDustFilter {

		public String call(XDust dust, Object obj) {
			String str = obj == null ? "" : obj.toString();
			try {
				return URLEncoder.encode(str, "utf-8");
			} catch (UnsupportedEncodingException e) {
				return "";
			}
		}

	}

	private class EscapeURIComponentFilter extends EscapeURIFilter {
	}

	/**
	 * Constructs an instance of the x-dust rendering engine
	 */
	public XDust() {
		this.parser = new XDustNodeListParser();
		this.getFilters().put("s", new NoOpFilter());
		this.getFilters().put("h", new EscapeHTMLFilter());
		this.getFilters().put("j", new EscapeJSFilter());
		this.getFilters().put("u", new EscapeURIFilter());
		this.getFilters().put("uc", new EscapeURIComponentFilter());
		this.setContextPath(System.getProperty("user.dir"));
	}

	/**
	 * The object used to parse strings into x-dust node trees
	 */
	protected XDustNodeListParser parser;
	private Map<String, XDustTemplate> templates;
	private Map<String, IXDustHelper> helpers;
	private Map<String, IXDustFilter> filters;
	private String contextPath;

	/**
	 * Gets the hash of parsed x-dust templates
	 * 
	 * @return The hash of templates
	 */
	public Map<String, XDustTemplate> getTemplates() {
		if (null == this.templates) {
			this.templates = new HashMap<String, XDustTemplate>();
		}
		return this.templates;
	}

	/**
	 * Gets the helpers available to the engine
	 * 
	 * @return The hash of helpers
	 */
	public Map<String, IXDustHelper> getHelpers() {
		if (null == this.helpers) {
			this.helpers = new HashMap<String, IXDustHelper>();
		}
		return this.helpers;
	}

	/**
	 * Gets the filters available to the engine
	 * 
	 * @return The hash of filters
	 */
	public Map<String, IXDustFilter> getFilters() {
		if (null == this.filters) {
			this.filters = new HashMap<String, IXDustFilter>();
		}
		return this.filters;
	}

	/**
	 * Encodes an object for inclusion in HTML code
	 * 
	 * @param obj
	 *            The object
	 * @return The escaped HTML string
	 */
	public String escapeHTML(Object obj) {
		return new EscapeHTMLFilter().call(this, obj);
	}

	/**
	 * Encodes an object for inclusion in JavaScript string code
	 * 
	 * @param obj
	 *            The object
	 * @return The escaped JS string
	 */
	public String escapeJS(Object obj) {
		return new EscapeJSFilter().call(this, obj);
	}

	/**
	 * Encodes an object for inclusion in a URI string
	 * 
	 * @param obj
	 *            The object
	 * @return The escaped URI string
	 */
	public String escapeURI(Object obj) {
		return new EscapeURIFilter().call(this, obj);
	}

	/**
	 * Encodes an object for inclusion as a URI component
	 * 
	 * @param obj
	 *            The object
	 * @return The escaped URI component string
	 */
	public String escapeURIComponent(Object obj) {
		return new EscapeURIComponentFilter().call(this, obj);
	}

	/**
	 * Readies template code for use and stores it in the templates map
	 * 
	 * @param str
	 *            The template source code
	 * @param name
	 *            The name by which to reference the template
	 * @param sourceFile
	 *            The source file that held the template source (if any)
	 * @return The root node of the parsed template
	 */
	public XDustNode compile(String str, String name, String sourceFile) {
		XDustNode rootNode = this.parser.parse(this, str);
		this.getTemplates().put(name,
				new XDustTemplate(name, rootNode, sourceFile));
		return rootNode;
	}

	/**
	 * Compiles a template from file
	 * 
	 * @param sourceFile
	 *            The path to the template file
	 * @param name
	 *            The name under which to store the compiled template
	 * @return The root node of the parsed template
	 * @throws IOException
	 */
	public XDustNode load(String sourceFile, String name) throws IOException {
		if (null == name || "".equals(name)) {
			name = sourceFile;
		} else if (null == sourceFile || "".equals(sourceFile)) {
			sourceFile = name;
		}
		if (this.getTemplates().containsKey(name)) {
			return this.getTemplates().get(name).getRootNode();
		} else {
			StringBuilder sb = new StringBuilder();
			if (sourceFile.startsWith("~")) {
				sourceFile = sourceFile.replace(
						"~",
						this.getContextPath() == null ? "" : this
								.getContextPath()).replace("//", "/");
			}
			BufferedReader reader = new BufferedReader(new FileReader(
					sourceFile));
			char[] buffer = new char[1024];
			int count = 0;
			while ((count = reader.read(buffer)) != -1) {
				sb.append(buffer, 0, count);
				buffer = new char[1024];
			}
			reader.close();
			String code = sb.toString();
			return this.compile(code, name, sourceFile);
		}
	}

	/**
	 * Renders a template
	 * 
	 * @param name
	 *            The name of the template to render
	 * @param model
	 *            The data to pass to the template
	 * @param action
	 *            The object to which to pass the results of the operation
	 */
	public void render(String name, Object model, IXDustResult action) {
		XDustTemplate template = this.getTemplates().get(name);
		String output = null;
		Exception error = null;
		try {
			output = template.render(this, null, null, model);
		} catch (Exception e) {
			error = e;
		}
		action.call(error, output);
	}

	/**
	 * Gets the current context root path
	 * 
	 * @return The context path
	 */
	public String getContextPath() {
		return this.contextPath;
	}

	/**
	 * Sets the current context root path
	 * 
	 * @param path
	 *            The context path
	 */
	public void setContextPath(String path) {
		this.contextPath = path.replace("\\", "/").replace("//", "/");
	}

}
