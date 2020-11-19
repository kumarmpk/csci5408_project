package com.dbms.datasource;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.IOException;

public interface IReadFile {

    JSONArray readJSON(String filePath) throws IOException, ParseException;
    //boolean validJSON(String filePath) throws Exception;

}
