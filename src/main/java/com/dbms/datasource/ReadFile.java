package com.dbms.datasource;

import com.dbms.presentation.IConsoleOutput;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    @Override
    public Map<String, JSONArray> readFilesFromPath(String dirName) throws Exception {
        Map<String, JSONArray> files = null;

        try {
            dirName = resource.dbPath + dirName;
            Path directoryPath = Paths.get(dirName);
            List<Path> filePathList = Files.list(directoryPath).collect(Collectors.toList());
            if(filePathList != null && !filePathList.isEmpty()) {
                files = new HashMap<>();
                for (Path path : filePathList) {
                    String pathStr = path.toString();
                    String[] pathArr = pathStr.split("\\\\");
                    String fileNameWithExt = pathArr[pathArr.length - 1];
                    String[] fileNameArr = fileNameWithExt.split("\\.");
                    String fileName = fileNameArr[0];
                    JSONArray arr = readJSON(pathStr);
                    files.put(fileName, arr);
                }
            }
        } catch (IOException e){
            consoleOutput.error("ReadFile: readFilesFromPath: IOException: "+e);
            throw e;
        } catch (Exception e){
            consoleOutput.error("ReadFile: readFilesFromPath: Exception: "+e);
            throw e;
        }
        return files;
    }

}
