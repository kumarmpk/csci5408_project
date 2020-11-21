package com.dbms.models;

import org.json.simple.JSONArray;

import java.util.Map;

public class CompleteDatabase {

    private static CompleteDatabase completeDatabase;
    private User user;
    private Map<String, JSONArray> tableRecords;

    private CompleteDatabase(){}

    public static CompleteDatabase getInstance(){
        if(null == completeDatabase){
            return new CompleteDatabase();
        }
        return completeDatabase;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Map<String, JSONArray> getTableRecords() {
        return tableRecords;
    }

    public void setTableRecords(Map<String, JSONArray> tableRecords) {
        this.tableRecords = tableRecords;
    }
}
