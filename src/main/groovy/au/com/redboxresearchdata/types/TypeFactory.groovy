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

import java.util.List
import java.util.Map
import javax.script.ScriptEngine
import javax.script.ScriptEngineManager
import org.apache.log4j.Logger
import org.apache.commons.io.FilenameUtils
/**
 * Builds JSON harvest messages and instantiates Types from non-JSON data structures.
 * 
 * This class provides allows 2 ways to plug-in scripts that affect how Types are assembled. 
 * 
 * A "pre" assembling hook, where each script specified at "harvest.scripts.preAssemble" is executed.  
 * 
 * Pre assembly scripts are passed the ff. variables:
 *
 * data - the Map instance representing a single 'record' of data. The keys may not match the field names of the Type.
 * type - the Type name
 * config - the groovy.util.ConfigObject
 * log - a org.apache.log4j.Logger
 * scriptPath - the path of this script
 * configPath - the script's configuration (optional)
 *
 * And must set a global 'data' Map instance. The keys must match the field names of the Type. 
 * 
 * Pre-assembly scripts should be just be concerned at fulfilling basic requirements (e.g. injecting / validating required fields, filtering out records based on a certain criteria). 
 * Complex field manipulation is recommended to be performed post-assembly where scripts are dealing with Type-specific instances instead of a Map. Scripts setting 'data' to null invalidates the record.
 * 
 * @TODO: Add a post-assembling hook specified at "harvest.scripts.postAssemble" 
 * 
 * @author Shilo Banihit
 * @since 1.0
 */
class TypeFactory {

	static final Logger log = Logger.getLogger(TypeFactory.class)
	static String targetMethod = "buildType"
	
	/**
	 * Returns a JSON harvest request message of the list using the type specified.
	 * 
	 * This method pre-processes the entries before instatiating each type, and resolves any field maps.
	 * 
	 * @param list
	 * @param type
	 * @return JSON String of harvest request message.
	 */
	public static String buildJsonStr(List<Map> list, String type, ConfigObject config) {
		if (TypeFactory.metaClass.respondsTo(TypeFactory.class, targetMethod) != null) {
			def strBuilder = new StringBuilder()
			strBuilder.append(getJsonHeaderStr(type))
			list.each {map ->
				def preAssembleResults = preAssemble(map, type, config)
				if (preAssembleResults.data != null) {
					strBuilder.append(TypeFactory."${targetMethod}"(resolveFields(preAssembleResults.data, type, config), type).toJsonStr())
				} else {
					// @TODO: send an event.
					log.error("Detected a failed record while pre-processing. Sending error event..")
				}
			}
			strBuilder.append(getJsonFooterStr(type))
			return strBuilder.toString()
		}
		throw new Exception("Type building method does not exist, check if TypeFactory has the method:'${targetMethod}' with Map and String argument")
	}
	
	/**
	 * Returns a JSON harvest request message of the data using the type specified.
	 * 
	 * @param data - Map<String, Object> where each key maps to the type's property
	 * @return type - target data type of this map
	 * @return JSON String of harvest request message
	 */
	public static String buildJsonStr(Map data, String type, ConfigObject config) {
		if (TypeFactory.metaClass.respondsTo(TypeFactory.class, targetMethod) != null) {
			def strBuilder = new StringBuilder()
			strBuilder.append(getJsonHeaderStr(type))
			strBuilder.append(TypeFactory."${targetMethod}"(resolveFields(data, type, config), type).toJsonStr())
			strBuilder.append(getJsonFooterStr(type))
			return strBuilder.toString()
		}
		throw new Exception("Type building method does not exist, check if TypeFactory has the method:'${targetMethod}' with Map and String argument")
	}
	
	/**
	 * Pre-assembly method.
	 * 
	 * @param data
	 * @param type
	 * @param config
	 * @return Expando instance containing 'data' (Map) and 'message' (String from script[s]).
	 */
	private static Expando preAssemble(Map data, String type, ConfigObject config) {
		def retval = new Expando()		
		def preprocessing = config.harvest.scripts?.preAssemble		
		log.debug(preprocessing)
		if (preprocessing != null && preprocessing.size() > 0) {
			ScriptEngineManager manager = new ScriptEngineManager()				
			preprocessing.keySet().each {script->
				def configPath = preprocessing[script]
				if (data != null) {		
					retval.script = script
					def engine = manager.getEngineByExtension(FilenameUtils.getExtension(script)) 
					if (engine != null) {
						if (log.isDebugEnabled()) {
							log.debug("Preparing to execute pre-processing script:'${script}'...")
						}
						engine.put("type", type)
						engine.put("data", data)
						engine.put("config", config)
						engine.put("log", log)
						engine.put("scriptPath", script)
						engine.put("configPath", configPath)
						engine.eval(new FileReader(new File(script)))
						data = engine.get("data")					
						retval.data = engine.get("data")							
						retval.message = engine.get("message")				
						if (data == null) {
							log.error("Execution of '${script}' invalidated the record. The script returned the ff. message: '${retval.message}")
						} else {
							if (log.isDebugEnabled()) {
								log.debug("Execution of '${script}' successful, message is: '${retval.message}'")
							}
						}	
					} else {
						log.error("Script type not supported: '${script}'.")
					}
				} else {
					log.error("Previous processing had failed. Halting further pre-processing of this record.")
					return
				}
			}
		} else {
		 	retval.script = "None"
		 	retval.data = data
			retval.message = "No script executed, pass through." 
		}
		return retval		
	}
	
	/**
	 * Resolves field names and potentially values using the mapping configuration.
	 * 
     * Fields mapping configuration as:
     * 
     * ["sourceField" : "destinationField"]
     * 
     * or
     * 
     * ["sourceField" : ["destinationField1","destinationField2","destinationField3"], "delim":";"]
     * 
     * sourceField - the key in the source map
     * destinationField(s) - the property or properties in the type's class
     * 
     * Optional config:
     * 
     * delim - the delimiter used to split the data in this field unto the destination fields
     * 
     * 
	 * @param map
	 * @param type
	 * @param config
	 * @return the same Map instance but modified to resolve the mapping, removing the source fields when necessary. 
	 */
	private static Map resolveFields(Map map, String type, ConfigObject config) {
		def size = config.types[type].fields.size()
		HashMap<String, Object> resolvedMap = new HashMap<String, Object>()
		if (size == 0) {
			if (log.isDebugEnabled()) {
				log.debug("No field map resoution performed, configuration seems to be missing.")
			} 			
		} else {
			for (i in 0..config.types[type].fields.size()-1) {
				def fieldConfig = config.types[type].fields[i]
				def keySet = config.types[type].fields[i].keySet()
				def srcField = null
				def delim = null
				def destField = null
				for (def key : keySet) {
					if ("delim".equals(key)) {
						delim = config.types[type].fields[i].delim
					} else {
						srcField = key
						destField = config.types[type].fields[i][srcField]
					}
				}
				if (delim == null) {
					if (srcField != destField) 
						map[destField] = map[srcField]
				} else {
					// parse the source field
					if (log.isDebugEnabled()) {
						log.debug("Parsing the source field: '${srcField}' using delimiter: '${delim}'")
					}				
					def parsedFieldList = map[srcField].tokenize(delim)
					for (j in 0..parsedFieldList.size()-1) {
						map[destField[j]] = parsedFieldList[j]
					}
				}
				if (srcField != destField)
					map.remove(srcField)
			}
		}
		return map
	}
	
	/**
	 * Build a specific type from a Map. The keys on the Map must be equivalent to the Type's field names. 
	 * 
	 * Method was added as an effort to ensure static typing.
	 * 
	 * @param data - source map
	 * @param type - type name
	 * @return Type subclass instance initialised with the map's data
	 */
	public static Type buildType(Map data, String type) {
		switch(type) {
			case "Service":
				return new Service(data)
			case "People":
				return new People(data)			
		}
		throw new Exception("Type does not seem to be supported: '${type}'.")
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
