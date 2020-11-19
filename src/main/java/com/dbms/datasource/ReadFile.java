package com.dbms.datasource;

import com.dbms.presentation.IConsoleOutput;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.stream.Collectors;

@Component
public class ReadFile implements IReadFile{

    @Autowired
    private IConsoleOutput consoleOutput;

    @Autowired
    private ResourceLoader resourceLoader;

    @Override
    public JSONArray readJSON(String filePath) throws IOException, ParseException {
        JSONParser jsonParser = new JSONParser();
        try{
            Resource resource = resourceLoader.getResource("classpath:"+filePath);
            InputStream inputStream = resource.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String file = reader.lines().collect(Collectors.joining("\n"));
            JSONArray userJsonArray = (JSONArray) jsonParser.parse(file);
            return userJsonArray;
        } catch (FileNotFoundException e) {
            consoleOutput.printMsgToConsole("ReadFile: readJSON: File not found. " + e);
            throw e;
        } catch (IOException e) {
            consoleOutput.printMsgToConsole("ReadFile: readJSON: File read failed. " + e);
            throw e;
        } catch (ParseException e) {
            consoleOutput.printMsgToConsole("ReadFile: readJSON: Imported file is not valid. Please make sure it is JSON format or if all fields are either boolean,string,array,object");
            throw e;
        }
    }

}
