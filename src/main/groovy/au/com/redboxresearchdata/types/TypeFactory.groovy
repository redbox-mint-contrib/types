/*******************************************************************************
*Copyright (C) 2013 Queensland Cyber Infrastructure Foundation (http://www.qcif.edu.au/)
*
*This program is free software: you can redistribute it and/or modify
*it under the terms of the GNU General Public License as published by
*the Free Software Foundation; either version 2 of the License, or
*(at your option) any later version.
*
*This program is distributed in the hope that it will be useful,
*but WITHOUT ANY WARRANTY; without even the implied warranty of
*MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*GNU General Public License for more details.
*
*You should have received a copy of the GNU General Public License along
*with this program; if not, write to the Free Software Foundation, Inc.,
*51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
******************************************************************************/
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
