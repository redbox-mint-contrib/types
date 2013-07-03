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
import org.codehaus.groovy.ast.ClassNode

class ServiceTest extends GroovyTestCase {

	def getDummyVal = {fieldName ->
		return "${fieldName}_val"
	}
	
	def buildJsonStr = {jsonStrBuilder -> 
		return { FieldNode field ->
			def fieldName = field.getName()
			def fieldVal = getDummyVal(fieldName)
			jsonStrBuilder.append(jsonStrBuilder.size() > 1 ? "," : "")
			jsonStrBuilder.append("\"${fieldName}\":\"${fieldVal}\"")
		}
	}
	
	def buildMap = {dataMap -> 
		return { FieldNode field ->
			def fieldName = field.getName()
			def fieldVal = getDummyVal(fieldName)
			dataMap[fieldName] = fieldVal
		}
	}
	
	def populateFields = {serviceObj ->
		return { FieldNode field ->
			def fieldName = field.getName()
			serviceObj[fieldName] = getDummyVal(fieldName)
		}
	}
	
	public void testToJsonStr() {
		def dataMap = [:]
		def jsonStrBuilder = new StringBuilder()
		jsonStrBuilder.append("{")
		def service = new Service()
		service.withFields(buildJsonStr(jsonStrBuilder), buildMap(dataMap), populateFields(service))
		jsonStrBuilder.append("}")		
		log.info("Expected JSON: ${jsonStrBuilder.toString()}")
		log.info("Actual JSON: ${service.toJsonStr()}")
		assertEquals(jsonStrBuilder.toString(), service.toJsonStr())
	}		
	
	public void testFromJsonStr() {
		def dataMap = [:]
		def jsonStrBuilder = new StringBuilder()
		jsonStrBuilder.append("{")
		def service = new Service()
		service.withFields(buildJsonStr(jsonStrBuilder), buildMap(dataMap), populateFields(service))
		jsonStrBuilder.append("}")		
		def service2 = Service.fromJsonStr(jsonStrBuilder.toString(), Service.class)				
		assertEquals(service, service2)
	}
	
	public void testFromJsonStrFail() {
		def dataMap = [:]
		def jsonStrBuilder = new StringBuilder()
		jsonStrBuilder.append("{")
		def service = new Service()
		service.withFields(buildJsonStr(jsonStrBuilder), buildMap(dataMap), populateFields(service))
		jsonStrBuilder.append("}")
		def service2 = Service.fromJsonStr(jsonStrBuilder.toString(), Service.class)
		service2.ID = "Test ID"			
		assertFalse(service.equals(service2))		
	}

}
