package org.openmrs.module.ugandaemr.api;

import java.io.IOException;
import java.util.LinkedHashMap;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

public class SimpleObject extends LinkedHashMap<String, Object> {
	
	private static final long serialVersionUID = 1L;
	
	public SimpleObject() {
		super();
	}
	
	public SimpleObject(int initialCapacity) {
		super(initialCapacity);
	}
	
	/**
	 * Puts a property in this map, and returns the map itself (for chained method calls)
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public SimpleObject add(String key, Object value) {
		put(key, value);
		return this;
	}
	
	/**
	 * Removes a property from the map, and returns the map itself (for chained method calls)
	 * 
	 * @param key
	 * @return
	 */
	public SimpleObject removeProperty(String key) {
		remove(key);
		return this;
	}
	
	/**
	 * Creates an instance from the given json string.
	 * 
	 * @param json
	 * @return the simpleObject
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	public static SimpleObject parseJson(String json) throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper objectMapper = new ObjectMapper();
		return objectMapper.readValue(json, SimpleObject.class);
	}
	
	/**
	 * Returns the value to which the specified key is mapped, or null if this map contains no
	 * mapping for the key.
	 * 
	 * @param key
	 * @return
	 */
	public <T> T get(String key) {
		return (T) super.get(key);
	}
	
}