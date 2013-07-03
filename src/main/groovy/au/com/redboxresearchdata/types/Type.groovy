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
