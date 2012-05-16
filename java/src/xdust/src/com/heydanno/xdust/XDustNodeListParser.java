package com.heydanno.xdust;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class XDustNodeListParser implements Serializable {

	private static final long serialVersionUID = 2933388538928074080L;
	private static final char[] OPERATORS = new char[] { '~', '#', '?', '@',
			':', '<', '>', '+', '/', '^' };
	private static final char[] SECTION_OPERATORS = new char[] { '#', '?', '@',
			':', '+', '<' };

	public XDustNodeListParser() {
		this.setLastEnd(0);
	}

	private int lastEnd;

	public int getLastEnd() {
		return lastEnd;
	}

	public void setLastEnd(int lastEnd) {
		this.lastEnd = lastEnd;
	}

	private boolean contains(char[] arr, char c) {
		for (int i = 0; i < arr.length; i++) {
			if (arr[i] == c) {
				return true;
			}
		}
		return false;
	}

	public XDustNodeList parse(XDust dust, String str) {
		Pattern comments = Pattern.compile("\\{!.+?!\\}", Pattern.DOTALL);
		str = comments.matcher(str).replaceAll("").trim();
		List<XDustNodeList> nodes = new ArrayList<XDustNodeList>();
		nodes.add(new XDustNodeList(null));
		int depth = 0;
		Pattern exp = Pattern
				.compile(
						"(\\{[\\~\\#\\?\\@\\:\\<\\>\\+\\/\\^]?([a-zA-Z0-9_\\$\\.]+|\"[^\"]+\")(\\:[a-zA-Z0-9\\$\\.]+)?(\\|[a-z]+)*?( \\w+\\=((\"[^\"]*?\")|([\\w\\.]+)))*?\\/?\\})",
						Pattern.DOTALL);
		int lastEnd = 0;
		Integer start = null;
		Integer end = null;
		Matcher matcher = exp.matcher(str);
		while (matcher.find()) {
			boolean depthChange = false;
			start = matcher.start();
			end = matcher.end();
			if (lastEnd != start) {
				String head;
				int headDiff = start - lastEnd;
				if (headDiff <= 0) {
					head = str.substring(lastEnd);
				} else {
					head = str.substring(lastEnd, start);
				}
				if (null != head && !"".equals(head)) {
					nodes.get(depth).add(new XDustTextNode(head));
				}
			}
			lastEnd = end;
			XDustNode node = null;
			String tag = str.substring(start + 1, end - 1);
			char op = tag.charAt(0);
			String[] tagParts = tag.split(" ");
			if (contains(OPERATORS, op)) {
				String[] tagNameParts = tagParts[0].substring(1).split(":");
				boolean selfClosed = tagNameParts[tagNameParts.length - 1]
						.endsWith("/");
				if (selfClosed) {
					int index = tagNameParts.length - 1;
					String last = tagNameParts[index];
					tagNameParts[index] = last.substring(0, last.length() - 1);
				}
				String scope = tagNameParts.length > 1 ? tagNameParts[1] : null;
				String tagName = tagNameParts[0];
				Map<String, XDustNode> parameters = null;
				if (op == '~') {
					node = new XDustEscapedCharacterNode(tagName);
					selfClosed = true;
				} else if (op == '#') {
					if (dust.getHelpers().containsKey(tagName)) {
						node = new XDustHelperNode(tagName, scope, parameters);
					} else {
						node = new XDustLogicNode(tagName, scope, parameters);
					}
				} else if (op == '?') {
					node = new XDustExistsNode(tagName, scope, parameters);
				} else if (op == '@') {
					String name = tagParts[0].substring(1);
					if ("idx".equals(name)) {
						node = new XDustIndexNode(null);
					} else if ("sep".equals(name)) {
						node = new XDustSepNode(null);
					} else {
						throw new UnsupportedOperationException();
					}
				} else if (op == '>') {
					boolean isExternal = tagName.startsWith("\"");
					Object tagArgs = tagName;
					if (isExternal) {
						tagArgs = this.parse(dust,
								tagName.replaceAll("(^\\\")|(\\\"$)", ""));
					}
					node = new XDustPartialNode(tagArgs, scope);
				} else if (op == '+') {
					node = new XDustBlockNode(tagName);
				} else if (op == '<') {
					node = new XDustInlinePartialNode(tagName);
				} else if (op == '/' || op == ':') {
					node = null;
				} else {
					throw new UnsupportedOperationException();
				}
				if (!selfClosed) {
					if (contains(SECTION_OPERATORS, op)) {
						depthChange = true;
						for (int i = 1; i < tagParts.length; i++) {
							String param = tagParts[i];
							String[] paramParts = param.split("=");
							String name = paramParts[0];
							StringBuilder valueSB = new StringBuilder();
							for (int j = 1; j < paramParts.length; j++) {
								if (j > 1) {
									valueSB.append("=");
								}
								valueSB.append(paramParts[j]);
							}
							String valueStr = valueSB.toString();
							XDustNode value;
							if (!valueStr.startsWith("\"")) {
								value = new XDustVariableNode(valueStr, null);
							} else {
								value = this.parse(dust, valueStr.replaceAll(
										"(^\\\")|(\\\"$)", ""));
							}
							((IXDustSectionNode) node).getParameters().put(
									name, value);
						}
						if (node instanceof XDustNodeList) {
							if (node instanceof XDustInlinePartialNode) {
								nodes.get(depth)
										.setBlock(
												((XDustInlinePartialNode) node)
														.getName(),
												node);
							}
							nodes.get(depth).add(node);
							depth += 1;
							while (nodes.size() < depth + 1) {
								nodes.add(null);
							}
							nodes.set(depth, (XDustNodeList) node);
						} else if (node instanceof XDustLogicNode) {
							nodes.get(depth).add(node);
							depth += 1;
							while (nodes.size() < depth + 1) {
								nodes.add(null);
							}
							nodes.set(depth,
									((XDustLogicNode) node).getCurrentBody());
						} else {
							IXDustSectionNode root = (IXDustSectionNode) nodes
									.get(depth - 1).last();
							root.endBody();
							while (nodes.size() < depth + 1) {
								nodes.add(null);
							}
							nodes.set(depth, root.startBody(tagName));
						}
					} else if (op == '/') {
						depthChange = true;
						if (depth > 0) {
							nodes.remove(depth);
							depth -= 1;
						}
					}
				}
				if (!selfClosed && null != node && !depthChange) {
					nodes.get(depth).add(node);
				}
			} else {
				tagParts = tagParts[0].split("\\|");
				List<String> filters = new ArrayList<String>();
				for (int i = 1; i < tagParts.length; i++) {
					filters.add(tagParts[i]);
				}
				tag = tagParts[0];
				node = new XDustVariableNode(tag, filters);
			}
			if (null != node && !depthChange) {
				nodes.get(depth).add(node);
			}
		}
		String tail = str.substring(lastEnd);
		if (null != tail && !"".equals(tail)) {
			nodes.get(depth).add(new XDustTextNode(tail));
		}
		return nodes.get(0);
	}

}
