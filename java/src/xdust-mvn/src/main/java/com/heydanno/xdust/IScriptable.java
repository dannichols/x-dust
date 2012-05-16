package com.heydanno.xdust;

import java.util.List;

public interface IScriptable extends Iterable<String> {

	Object getValue();

	boolean isValue();

	boolean isList();

	boolean isTruthy();

	List<Object> asList();

	Object get(String name);

	boolean hasKey(String name);

}
