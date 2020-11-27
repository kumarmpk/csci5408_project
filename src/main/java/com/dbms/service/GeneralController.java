package com.dbms.service;

import com.dbms.models.CompleteDatabase;
import com.dbms.models.User;
import com.dbms.presentation.ConsoleOutput;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class GeneralController {

    @Autowired
    private ConsoleOutput consoleOutput;

    @Autowired
    private CreateLoadDatabase createLoadDatabase;

    public void showTables(User user){
        CompleteDatabase completeDatabase = user.getCompleteDatabase();
        Map<String, JSONArray> tableRecords = completeDatabase.getTableRecords();
        if(tableRecords == null || tableRecords.isEmpty()){
            consoleOutput.print("There are no tables in the database.");
        } else{
            for(String tableName : tableRecords.keySet()){
                consoleOutput.print("\t"+tableName);
            }
        }
    }

    public void descTable(User user, String query){
        CompleteDatabase completeDatabase = user.getCompleteDatabase();
        JSONObject metaData = completeDatabase.getMetaData();
        if(metaData == null){
            consoleOutput.print("There are no tables in the database.");
        } else{
            String words[] = query.split(" ");
            int queryWordCount = words.length;
            if(queryWordCount > 1) {
                String userTableName = words[1];
                JSONArray tables = (JSONArray) metaData.get("tables");
                for(Object table : tables){
                    JSONObject tableJson = (JSONObject) table;
                    if(tableJson.get("tableName").equals(userTableName)){
                        JSONArray columns = (JSONArray) tableJson.get("columns");
                        for(Object columnObject : columns){
                            JSONObject columnJson = (JSONObject) columnObject;
                            for(Object keyObj : columnJson.keySet()){
                                String keyStr = (String) keyObj;
                                String value = (String) columnJson.get(keyStr);
                                consoleOutput.print("Column | Type");
                                consoleOutput.print(keyStr+" | "+value);
                            }
                        }
                    }
                }
            } else{
                consoleOutput.print("Invalid description query. Please try again.");
            }
        }
    }

    public void loadDB(User user, String query) throws Exception {
        String words[] = query.split(" ");
        int queryWordCount = words.length;
        if(queryWordCount > 1) {
            String userDBName = words[1];
            createLoadDatabase.loadDatabase(user, userDBName);
        }
    }

}
