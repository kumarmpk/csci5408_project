package com.dbms.models;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Map;

public class CompleteDatabase {

    private Map<String, JSONArray> tableRecords;
    private JSONObject metaData;
    private String dbName;
    private String dbPath = Paths.get("").toAbsolutePath().toString() + "\\data\\";

    public CompleteDatabase(){
    	
    }

    public Map<String, JSONArray> getTableRecords() {
        return tableRecords;
    }

    public void setTableRecords(Map<String, JSONArray> tableRecords) {
        this.tableRecords = tableRecords;
    }

    public JSONObject getMetaData() {
        return metaData;
    }

    public void setMetaData(String userName) {
        try {
            JSONParser jsonParser = new JSONParser();
            FileReader file1 = new FileReader(dbPath + userName + "_" + this.dbName + "\\"
                    + "metadata" + ".json");
            Object obj = jsonParser.parse(file1);
            this.metaData = (JSONObject) obj;
        } catch (Exception e) {
            System.out.println("No metadata found for" + userName + "_" + this.dbName);
        }
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }
}
