package com.dbms.models;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.Map;

public class CompleteDatabase {

    private Map<String, JSONArray> tableRecords;
    private JSONObject metaData;
    private String dbName;

    public CompleteDatabase(){}

    public Map<String, JSONArray> getTableRecords() {
        return tableRecords;
    }

    public void setTableRecords(Map<String, JSONArray> tableRecords) {
        this.tableRecords = tableRecords;
    }

    public JSONObject getMetaData() {
        return metaData;
    }

    public void setMetaData(JSONObject metaData) {
        this.metaData = metaData;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }
}
