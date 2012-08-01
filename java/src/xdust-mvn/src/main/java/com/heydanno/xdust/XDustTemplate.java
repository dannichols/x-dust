package com.heydanno.xdust;

import java.io.Serializable;

/**
 * A template that can be passed a data object and then rendered to string
 */
public class XDustTemplate implements Serializable {

	private static final long serialVersionUID = 8666395035723748127L;

	/**
	 * Constructor
	 * 
	 * @param name
	 *            The name of the template
	 * @param rootNode
	 *            The root node of the template
	 * @param sourceFile
	 *            The path to the file containing the source code for the
	 *            template
	 */
	public XDustTemplate(String name, XDustNode rootNode, String sourceFile) {
		this.name = name;
		this.rootNode = rootNode;
		this.sourceFile = sourceFile;
	}

	private String name;
	private XDustNode rootNode;
	private String sourceFile;

	/**
	 * Gets the name of the template
	 * 
	 * @return The name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Gets the root node of the template
	 * 
	 * @return The root node
	 */
	public XDustNode getRootNode() {
		return rootNode;
	}

	/**
	 * Gets the source file of the template
	 * 
	 * @return The source file path
	 */
	public String getSourceFile() {
		return sourceFile;
	}

	/**
	 * Renders the template to string
	 * 
	 * @param dust
	 *            The current dust render engine instance
	 * @param chain
	 *            The chain of nodes currently being rendered
	 * @param context
	 *            The current data context
	 * @param model
	 *            The current data object
	 * @return The rendered string
	 * @throws Exception
	 */
	public String render(XDust dust, RenderChain chain, Context context,
			Object model) throws Exception {
		return this.rootNode.render(dust, chain, context, model);
	}

}
