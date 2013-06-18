package au.com.redboxresearchdata.types

import com.google.gson.Gson
import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.FieldNode

/**
 * Represents a Redbox / Mint object
 * 
 * @author Shilo Banihit
 * @since 1.0
 *
 */
abstract class Type {
	
	def static fieldFilter = ['class', '$staticClassInfo','__$stMC', '$callSiteArray', 'fieldFilter', 'closure', 'delegate']
	
	/**
	 * JSON serialization handler
	 * 
	 * @return JSON String representation of this type
	 */
	public String toJsonStr() {
		return new Gson().toJson(this)		
	}
	/**
	 * JSON de-serialization handler.
	 * 
	 * @param json
	 */
	public static Type fromJsonStr(String json, Class clz) {
		return new Gson().fromJson(json, clz)
	}
	
	def withFields(Closure... closures) {
		new ClassNode(getClass()).fields.findAll{!fieldFilter.contains(it.getName())}.each{ FieldNode field ->
			closures.each {
				it(field)
			}
		}	
	}
	
	public boolean equals(Object obj) {		
		if (obj != null && obj instanceof Type) {
			def eqval = new Expando()
			eqval.value = true						
			withFields({aObj, eqVal ->
				return {FieldNode field ->
					def fieldName = field.getName();										
					eqVal.value = eqVal.value && aObj[fieldName] == this[fieldName]					
				}
			}(obj, eqval))
			return eqval.value
		}
		return false
	}	
}
