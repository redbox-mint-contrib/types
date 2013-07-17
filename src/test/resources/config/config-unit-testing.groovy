/**
 * Types Testing Configuration File
 * ----------------------------------------------------------------------
 * PROJECT: Types
 * ----------------------------------------------------------------------
 * @author Shilo Banihit
 *
 */
// General configuration below...
environment = environment
types {
	Service {
		fields = [
			["ID" : "ID"],
			["Name" : "Name"],
			["Type" : "Type"],
			["ANZSRC_FOR_1" : "ANZSRC_FOR_1"],
			["ANZSRC_FOR_2" : "ANZSRC_FOR_2"],
			["ANZSRC_FOR_3" : "ANZSRC_FOR_3"],
			["Location" : "Location"],
			["Coverage_Temporal_From" : "Coverage_Temporal_From"],
			["Coverage_Temporal_To" : "Coverage_Temporal_To"],
			["Coverage_Spatial_Type" : "Coverage_Spatial_Type"],
			["Coverage_Spatial_Value" : "Coverage_Spatial_Value"],
			["Existence_Start" : "Existence_Start"],
			["Existence_End" : "Existence_End"],
			["Website" : "Website"],
			["Data_Quality_Information" : "Data_Quality_Information"],
			["Reuse_Information" : "Reuse_Information"],
			["Access_Policy" : "Access_Policy"],
			["Description" : "Description"],
			["URI" : "URI"]
		]
		required = ["ID"]
	}
	
	People {
		fields = [
			["ID" : "ID"],
			["Given_Name" : "Given_Name"],
			["Other_Names" : "Other_Names"],
			["Family_Name" : "Family_Name"],
			["Pref_Name" : "Pref_Name"],
			["Honorific" : "Honorific"],
			["Email" : "Email"],
			["Job_Title" : "Job_Title"],
			["GroupIDs" : ["GroupID_1", "GroupID_2", "GroupID_3"], "delim" : ";"],
			["ANZSRC_FOR_1" : "ANZSRC_FOR_1"],
			["ANZSRC_FOR_2" : "ANZSRC_FOR_2"],
			["ANZSRC_FOR_3" : "ANZSRC_FOR_3"],
			["URI" : "URI"],
			["NLA_Party_Identifier" : "NLA_Party_Identifier"],
			["ResearcherID" : "ResearcherID"],
			["openID" : "openID"],
			["Personal_URI" : "Personal_URI"],
			["Personal_Homepage" : "Personal_Homepage"],
			["Staff_Profile_Homepage" : "Staff_Profile_Homepage"],
			["Description" : "Description"]
		]
		required = ["ID"]
	}
}
// Environment specific config below...
environments {
	test {
		file {
			runtimePath = "src/test/resources/config/generated/config-unit-testing.groovy"
			customPath = ""
		}
		harvest {
			/* Please see http://csvjdbc.sourceforge.net/ for details on your options below. */
			csvjdbc {
				className = "org.relique.jdbc.csv.CsvDriver"
				url = "jdbc:relique:csv:class:au.com.redboxresearchdata.harvester.csvjdbc.CsvFileReader"
			}
			directory = "input"
			queueCapacity = 10
			pollRate = 5000
			output {
				directory = "output/"
				dateFormat = "yyyy-MM-dd_HHmmssSSS"
			}
			scripts {								
				preAssemble = ["scripts/filter.groovy":""]
			}
		}
		activemq {
			url = "tcp://localhost:9201"
		}
		
	}
}