package com.heydanno.xdust;

import java.util.List;

/**
 * Interface that defines an object that can be worked with similarly to a
 * JavaScript object
 */
public interface IScriptable extends Iterable<String> {

	/**
	 * Gets the raw value of the object
	 * 
	 * @return The raw value of the object
	 */
	Object getValue();

	/**
	 * Indicates whether or not this object is a raw value
	 * 
	 * @return True if the object is a raw value
	 */
	boolean isValue();

	/**
	 * Indicates whether or not this object is a list
	 * 
	 * @return True if the object is a list
	 */
	boolean isList();

	/**
	 * Indicates whether or not this object meets the JavaScript criteria for
	 * truthfulness
	 * 
	 * @return True if the object meets the criteria
	 */
	boolean isTruthy();

	/**
	 * Gets an iterable representation of the object
	 * 
	 * @return This object as a List
	 */
	List<Object> asList();

	/**
	 * Gets a property from the object, given its name
	 * 
	 * @param name
	 *            The name of the property
	 * @return The value of the property
	 */
	Object get(String name);

	/**
	 * Indicates whether or not the object has a property
	 * 
	 * @param name
	 *            The name of the property
	 * @return True if the object has a property with that name
	 */
	boolean hasKey(String name);

}
