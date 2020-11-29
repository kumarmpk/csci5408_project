package com.dbms.service.parser;

import com.dbms.models.CompleteDatabase;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dbms.common.Validation;
import com.dbms.datasource.ReadFile;
import com.dbms.datasource.Resource;
import com.dbms.presentation.ConsoleOutput;
import com.dbms.presentation.ReadUserInput;
import com.dbms.models.User;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.regex.*;

@Component
public class CreateDB {
	@Autowired
	private ReadUserInput readUserInput;

	@Autowired
	private Validation validation;

	@Autowired
	private ConsoleOutput consoleOutput;

	@Autowired
	private ReadFile readFile;

	@Autowired
	private  Resource resource;
	public  String dbPath = Paths.get("").toAbsolutePath().toString() + "\\data\\";
	String datapath=null;

	private  final String errorMessage = "Invalid create query. Please check syntax/spacing.";
	private  final String databaseNameRegex = "(\\w+)";

	private  final String createRegex = "CREATE DATABASE\\s" +
			databaseNameRegex +
			";?$";
	JSONArray tables = new JSONArray();
	JSONObject object = new JSONObject();


	public  void runDBQuery(String createdbQuery, User user) {
		JSONObject parsedQuery = parseCreateDBQuery(createdbQuery);
		executeCreateDBQuery(parsedQuery, user);
	}

	public  JSONObject parseCreateDBQuery(String createdbQuery) {
		JSONObject selectObject = new JSONObject();
		try {
			Pattern syntaxExp = Pattern.compile(createRegex, Pattern.CASE_INSENSITIVE);
			Matcher queryParts = syntaxExp.matcher(createdbQuery);
			String dbName = null;
			String condition = null;
			if(queryParts.find()) {
				dbName = queryParts.group(1);
			} else {
				System.out.println(errorMessage);
			}
			selectObject.put("dbName", dbName);
			return selectObject;
		} catch(Exception e) {
			System.out.println(e.getLocalizedMessage());
			return null;
		}
	}

	public boolean executeCreateDBQuery(JSONObject parsedQuery, User user) {
		try {
			String dbName = (String) parsedQuery.get("dbName");
			if (dbName.isEmpty()) {
				System.out.println(errorMessage);
				return false;
			}
			createDirectory(dbName, user);

			try (FileWriter file =
						 new FileWriter(dbPath+
								 user.getUserName()+"_"+dbName+"\\"+"metadata"+".json")) {
				object.put("tables", tables);

				file.write(object.toJSONString());
				file.close();
				user.setCompleteDatabase(new CompleteDatabase());

			} catch (IOException e) {
				e.printStackTrace();
				throw e;
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	private void createDirectory(String dbName, User user) throws IOException {

		try {
			Path path = Paths.get(dbPath + user.getUserName() + "_" + dbName);
			Files.createDirectory(path);
		} catch (IOException e){
			System.out.println(e.getLocalizedMessage());
			throw e;
		}
	}

}
