package com.heydanno.xdust;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

public interface IScriptable extends Iterable<String> {
	
	Object getValue();
	boolean isValue();
	boolean isList();
	boolean isTruthy();
	List<Object> asList();
	Object get(String name) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException;
	boolean hasKey(String name) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException;

}
