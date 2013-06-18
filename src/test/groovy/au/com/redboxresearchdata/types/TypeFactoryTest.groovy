package au.com.redboxresearchdata.types;

import groovy.util.GroovyTestCase;
import org.codehaus.groovy.ast.FieldNode

class TypeFactoryTest extends GroovyTestCase {
	
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
		def dataMap = [:]
		def service = new Service()
		service.withFields(buildMap(dataMap), populateFields(service))
		def list = [dataMap] 		
		log.info(TypeFactory.buildJsonStr(list, "Service"))
	}

}
