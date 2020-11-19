package com.dbms.datasource;

import com.dbms.DBMSApp;
import com.dbms.presentation.ConsoleOutput;
import com.dbms.presentation.IConsoleOutput;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;

@Component
public class ReadFile implements IReadFile{

    @Autowired
    private IConsoleOutput consoleOutput;

    @Autowired
    private Resource resource;

    @Override
    public JSONArray readJSON(String filePath) throws IOException, ParseException {
        JSONParser jsonParser = new JSONParser();
        try{
            filePath = resource.dbPath + filePath;
            FileReader reader = new FileReader(filePath);
            JSONArray userJsonArray = (JSONArray) jsonParser.parse(reader);
            return userJsonArray;
        } catch (FileNotFoundException e) {
            consoleOutput.error("ReadFile: readJSON: File not found. " + e);
            throw e;
        } catch (IOException e) {
            consoleOutput.error("ReadFile: readJSON: File read failed. " + e);
            throw e;
        } catch (ParseException e) {
            consoleOutput.error("ReadFile: readJSON: Imported file is not valid. Please make sure it is JSON format or if all fields are either boolean,string,array,object");
            throw e;
        }
    }

}
