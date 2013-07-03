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

/**
 * Represents a Mint Service 
 * 
 * @author Shilo Banihit
 *
 */
class Service extends Type {
	String ID;
	String Name;
	String Type;
	String ANZSRC_FOR_1;
	String ANZSRC_FOR_2;
	String ANZSRC_FOR_3;
	String Location;
	String Coverage_Temporal_From;
	String Coverage_Temporal_To;
	String Coverage_Spatial_Type;
	String Coverage_Spatial_Value;
	String Existence_Start;
	String Existence_End;
	String Website;
	String Data_Quality_Information;
	String Reuse_Information;
	String Access_Policy;
	String Description;
	String URI;
	
}
