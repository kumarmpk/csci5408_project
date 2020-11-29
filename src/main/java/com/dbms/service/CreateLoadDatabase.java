package com.dbms.service;

import com.dbms.common.Constants;
import com.dbms.common.Validation;
import com.dbms.datasource.ReadFile;
import com.dbms.datasource.Resource;
import com.dbms.models.CompleteDatabase;
import com.dbms.models.User;
import com.dbms.presentation.ConsoleOutput;
import com.dbms.presentation.ReadUserInput;
import org.json.simple.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@Component
public class CreateLoadDatabase {

    @Autowired
    private ReadUserInput readUserInput;

    @Autowired
    private Validation validation;

    @Autowired
    private ConsoleOutput consoleOutput;

    @Autowired
    private ReadFile readFile;

    @Autowired
    private Resource resource;

   public static String dbname;
    String datapath;

    /*public Map<String, JSONArray> createLoadDatabase(User user) throws Exception {
        Map<String, JSONArray> tableRecords = null;
        String userResponse;
        boolean invalidUserResponse = true;
        while(invalidUserResponse) {
            userResponse = readUserInput
                    .getStringInput("Enter C to create new database or L to load the existing database.");
            if (validation.isValidInput(userResponse) && userResponse.equalsIgnoreCase(Constants.C)) {
                tableRecords = createDatabase(user.getUserName());
                CreateTable t=new CreateTable();
                t.createTableForUser(user, readUserInput, validation, dbname ,user.getUserName(), datapath, consoleOutput);
                invalidUserResponse = false;
            } else if (validation.isValidInput(userResponse) && userResponse.equalsIgnoreCase(Constants.L)) {
                tableRecords = loadDatabase(user.getUserName());
                CreateTable t=new CreateTable();
                t.createTableForUser(user, readUserInput, validation, dbname ,user.getUserName(), datapath, consoleOutput);
                invalidUserResponse = false;
            } else {
                consoleOutput.warning("Invalid response. Please try again.");
                invalidUserResponse = true;
            }
        }
        return tableRecords;
    }*/

    private Map<String, JSONArray> createDatabase(String userName) throws IOException {
        String userResponse = readUserInput
                .getStringInput("Enter the database name to create. Note: Name can have only alphanumeric characters.");
        boolean invalidUserResponse = true;
        while(invalidUserResponse){
            if(validation.isValidInput(userResponse)
                    && validation.isAlphaNumeric(userResponse)){

                if(checkDBNameUserName(userResponse, userName)){
                    consoleOutput.warning("Database already exists. Give a new name.");
                    continue;
                }

                createDirectory(userResponse, userName);
                dbname=userResponse;
                invalidUserResponse = false;
            } else{
                consoleOutput.warning("Invalid database name. Please try again.");
                invalidUserResponse = true;
            }
        }
        return new HashMap<>();
    }

    private void createDirectory(String dbName, String userName) throws IOException {
        try {
            Path path = Paths.get(resource.dbPath + userName + "_" + dbName);
            Files.createDirectory(path);
            datapath=resource.dbPath;
        } catch (IOException e){
            consoleOutput.error("IOException in createDirectory: "+e);
            throw e;
        }
    }

    private boolean checkDBNameUserName(String dbName, String userName){
        boolean isExist = false;
        File file = new File(resource.dbPath + userName + "_" + dbName);
        if(file.exists() && file.isDirectory()){
            isExist = true;
            datapath=resource.dbPath;
        }
        return isExist;
    }

    public void loadDatabase(User user, String dbName) throws Exception {
        Map<String, JSONArray> tableRecords;
        String userName = user.getUserName();
        dbname=dbName;
        if (checkDBNameUserName(dbName, userName)) {
            tableRecords = loadTables(dbName, userName);
            user.setCompleteDatabase(new CompleteDatabase());
            user.getCompleteDatabase().setTableRecords(tableRecords);
            user.getCompleteDatabase().setDbName(dbName);
            user.getCompleteDatabase().setMetaData(user);
        } else{
            consoleOutput.print("Database does not exist. Please load existing database or create new.");
        }
    }

    private Map<String, JSONArray> loadTables(String dbName, String userName) throws Exception {
        return readFile.readFilesFromPath(userName+"_"+dbName);
    }


}
