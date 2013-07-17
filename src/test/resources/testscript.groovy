/*
 *
 * Scripts are passed the ff. variables:
 *
 * data - the Map instance representing a single 'record' of data.
 * type - the Type name
 * config - the groovy.util.ConfigObject
 * log - a org.apache.log4j.Logger
 * scriptPath - the path of this script
 * environment - the current environment
 *
 * And must set a global 'data' Map instance. The keys must match the field names of the Type. Setting 'data' to null invalidates the record.
 *
 * The script can also set a 'message' global variable.
 *
 */

 for (key in data.keySet()) {
	 data[key] = "test-${data[key]}"
 }
 message = "test script ok."