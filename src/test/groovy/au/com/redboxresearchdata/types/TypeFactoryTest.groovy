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
package au.com.redboxresearchdata.types;

import groovy.util.GroovyTestCase;
import org.codehaus.groovy.ast.FieldNode

import au.com.redboxresearchdata.util.config.Config;

class TypeFactoryTest extends GroovyTestCase {
	
	def environment = "test"
	
	def getDummyVal = {fieldName ->
		return "${fieldName}_val"
	}

	def populateFields = {serviceObj ->
		return {FieldNode field ->
			def fieldName = field.getName()
			serviceObj[fieldName] = getDummyVal(fieldName)
		}
	}
	
	def buildMap = {dataMap ->
		return { FieldNode field ->
			def fieldName = field.getName()
			def fieldVal = getDummyVal(fieldName)
			dataMap[fieldName] = fieldVal
		}
	}
	
	public void testBuildJsonStr() {
		def config = Config.getConfig(environment, "src/test/resources/config/config-unit-testing.groovy")
		def dataMap = [:]
		def service = new Service()
		service.withFields(buildMap(dataMap), populateFields(service))
		def list = [dataMap]
		def jsonHarvestMsg = TypeFactory.buildJsonStr(list, "Service", config) 		
		log.info(jsonHarvestMsg)
		dataMap.each {key, field->
			assertEquals("test-${key}_val", field)
		}
		assert(jsonHarvestMsg.startsWith('''{"type":"ServiceJson","data":{"data":[{'''))
	}
}
