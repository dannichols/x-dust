package com.heydanno.xdust;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

public class XDustNotExistsNode extends XDustExistsNode {

	public XDustNotExistsNode(String path, String scope,
			Map<String, XDustNode> parameters) {
		super(path, scope, parameters);
	}

	@Override
	public String chooseBodyName(Context context, Object model)
			throws IllegalArgumentException, IllegalAccessException,
			InvocationTargetException {
		Object resolved = this.getContext().resolve(context, model);
		return this.isTruthy(resolved) ? ELSE : BLOCK;
	}

}
