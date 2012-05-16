package com.heydanno.xdust;

import java.util.Map;

public class XDustNotExistsNode extends XDustExistsNode {

	private static final long serialVersionUID = 6236854786516129400L;

	public XDustNotExistsNode(String path, String scope,
			Map<String, XDustNode> parameters) {
		super(path, scope, parameters);
	}

	@Override
	public String chooseBodyName(Context context, Object model) {
		Object resolved = this.getContext().resolve(context, model);
		return this.isTruthy(resolved) ? ELSE : BLOCK;
	}

}
