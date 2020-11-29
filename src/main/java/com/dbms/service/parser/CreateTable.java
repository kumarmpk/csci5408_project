package com.dbms.service.parser;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dbms.datasource.ReadFile;
import com.dbms.models.User;
import com.dbms.presentation.ReadUserInput;
import com.dbms.service.CreateLoadDatabase;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.*;

@Component
public class CreateTable {

	@Autowired
	private CreateLoadDatabase dbname;

	@Autowired
	private ReadFile readFile;
	public  String dbPath = Paths.get("").toAbsolutePath().toString() + "\\data\\";
	private final String errorMessage = "Invalid create table query. Please check syntax/spacing.";
	private final String tableNameRegex = "(\\w+)";
	private final String primaryKey = ", PRIMARY KEY[(](\\w+)[)]";
	private final String values = "[(]((((\\w+) (varchar|int)[(]\\d+[)])(,)*\\s*)+)"+(primaryKey)+"[)]";
	private final String createRegex = "CREATE TABLE\\s" +
			tableNameRegex + values+
			";?$";
	String tableName = null;
	JSONArray tables = new JSONArray();
	JSONObject object = new JSONObject();


	public  void runTableQuery(String createdbQuery, User user) {
		JSONObject parsedQuery = parseCreateTableQuery(createdbQuery);
		System.out.println("object received="+parsedQuery);
		executeCreateTableQuery(parsedQuery, user);
	}

	public JSONObject parseCreateTableQuery(String createdbQuery) {
		JSONObject selectObject = new JSONObject();
		JSONArray tables = new JSONArray();

		try {
			Pattern syntaxExp = Pattern.compile(createRegex, Pattern.CASE_INSENSITIVE);
			Matcher queryParts = syntaxExp.matcher(createdbQuery);
			String attributes = null;
			String primary = null;

			if(queryParts.find()) {
				tableName = queryParts.group(1);

				attributes = queryParts.group(2);

				primary = queryParts.group(8);

			} else {
				System.out.println(errorMessage);
			}
			JSONArray arr2 = new JSONArray();

			HashMap<String, String> map = new HashMap<String, String>();

			String s[]= attributes.split("[\\s,]+");
			JSONObject jsonobj = new JSONObject();

			for(int i=0;i<s.length-1;i=i+2) {

				JSONObject js = new JSONObject();
				map.put(s[i], s[i+1]);
				JSONObject jsonarr = new JSONObject();

				arr2.add(jsonarr);
				jsonarr.putAll(map);
				jsonobj=jsonarr;

			}
			JSONObject jsonobj2 = new JSONObject();
			jsonobj2.put("columns",jsonobj);
			jsonobj2.put("primaryKey",primary);
			JSONObject jsonobj3 = new JSONObject();
			jsonobj3.put(tableName, jsonobj2);  //json object for a single table created
			JSONObject jsonobj4 = new JSONObject();

			selectObject.put(tableName, jsonobj2);

			return selectObject;
		} catch(Exception e) {
			System.out.println(e.getLocalizedMessage());
			return null;
		}
	}

	public boolean executeCreateTableQuery(JSONObject parsedQuery, User user) {
		try {
			String table_Name = parsedQuery.get(tableName).toString();
			if (table_Name.isEmpty()) {
				System.out.println(errorMessage);
				return false;
			}
			System.out.println(parsedQuery);

			tables.add(parsedQuery);
			JSONParser jsonParser = new JSONParser();


			FileReader file1 = new FileReader(dbPath+user.getUserName()+"_"+dbname+"\\"+"metadata"+".json");
			Object obj = jsonParser.parse(file1);
			JSONObject object1 = new JSONObject();
			JSONArray object2 = new JSONArray();
			JSONArray list = new JSONArray();


			object1=(JSONObject) obj;
			object2=(JSONArray) object1.get("tables");
			object2.add(parsedQuery);

			for(int i=0;i<object2.size();i++) {

				System.out.println("object2="+object2);

			}	           
			try (FileWriter file = new FileWriter(dbPath+user.getUserName()+"_"+dbname+"\\"+"metadata"+".json", false)) {

				file.write("");

				JSONObject tableObject = new JSONObject();

				tableObject.put("tables", object2);
				System.out.println("tableObject="+tableObject); //json object for the whole metadata file

				file.write(tableObject.toString());
				System.out.println("Data Successfully added to database"); 

				file.close();

			} catch (IOException e) {
				e.printStackTrace();
			}

			return true;
		} catch (Exception e) {
			System.out.println(e.getLocalizedMessage());
			return false;
		}
	}


	/*    public static void main(String []a) {

		        String s3 = "create TABLE orders(productname varchar(60), id int(50), price int(50), PRIMARY KEY(id))";
		        String s1 = "create TABLE customers(name varchar(60), id int(50), age int(50), city varchar(50), PRIMARY KEY(id))";
		        String s2 = "create TABLE tablename(column varchar(60), ok int(50))";

		        runTableQuery(s1);
		    }*/
}


