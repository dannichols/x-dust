package com.heydanno.xdust;

import java.util.Map;

public interface IXDustSectionNode {

	Map<String, XDustNode> getParameters();

	XDustNodeList startBody(String name);

	void endBody();

}
