package au.com.redboxresearchdata.types

import java.util.List;
import java.util.Map;

/**
 * Builds JSON harvest messages and instantiates types from non-JSON data structures.
 * 
 * This class currently does not validate the integrity of the source structure.
 * 
 * @author Shilo Banihit
 * @since 1.0
 *
 */
class TypeFactory {

	/**
	 * Returns a JSON representation of the list using the type specified.
	 * 
	 * Note: this method does not validate the list data. Ensure data sanity before calling this method.
	 * 
	 * @param list
	 * @param type
	 * @return
	 */
	public static String buildJsonStr(List<Map> list, String type) {
		def targetMethod = "build${type}"
		if (TypeFactory.metaClass.respondsTo(TypeFactory.class, targetMethod) != null) {
			def strBuilder = new StringBuilder()
			strBuilder.append(getJsonHeaderStr(type))
			list.each {map ->
				strBuilder.append(TypeFactory."${targetMethod}"(map).toJsonStr())
			}
			strBuilder.append(getJsonFooterStr(type))
			return strBuilder.toString()
		} 
		throw new Exception("Type does not seem to be supported: '${type}', check if TypeFactory has the method:'${targetMethod}' with Map argument")
	}
	
	/**
	 * Build a Service type from a Map. The keys on the Map must be equivalent to the Service field names. Method was added as an effort to ensure static typing.
	 * 
	 * Possibly add validation here or on the Type's constructor. 
	 *  
	 * @param data
	 * @return Service object initialised with the map's data
	 */
	public static Type buildService(Map data) {
		return new Service(data)		
	}
	
	public static Type buildPeople(Map data) {
		return new People(data)
	}
	
	/**
	 * Wraps the JSON type message.
	 * 
	 * @param type
	 * @return header string for the JSON message
	 */
	static getJsonHeaderStr(String type) {
		return "{\"type\":\"${type}Json\",\"data\":{\"data\":["
	}
	
	/**
	 * Wraps the JSON type message.
	 * 
	 * @param type
	 * @return footer string for the JSON message.
	 */
	static getJsonFooterStr(String type) {
		return "]}}"
	}
}
