package com.dbms.service.parser;

import com.dbms.models.User;
import com.dbms.service.CreateLoadDatabase;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.FileReader;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class SelectQuery {

    @Autowired
    private CreateLoadDatabase dbname;
    private String dbPath = Paths.get("").toAbsolutePath().toString() + "\\data\\";
    private final String errorMessage = "Invalid select query. Please check syntax/spacing.";
    private final String tableNameRegex = "(\\w+)";
    private final String columnNameRegex = "((?:\\*)|(?:(?:\\w+)(?:,\\s?\\w+)*))";
    private final String ConditionValueTypes = "(\".*\"|\\d+(?:.\\d+)?|TRUE|true|FALSE|false)";
    private final String conditionEquality = "(=|<=|>=|>|<)";
    private final String conditionRegex = "(?:(?:\\sWHERE\\s)(?:(\\w+)" + (conditionEquality) + ConditionValueTypes + "))?";
    private final String selectRegex = "SELECT\\s" +
            columnNameRegex +
            "\\sFROM\\s" +
            tableNameRegex +
            conditionRegex +
            ";?$";

    public void runQuery(String selectQuery, User user) {
        JSONObject parsedQuery = parseSelectQuery(selectQuery);
        executeSelectQuery(parsedQuery, user);
    }

    public JSONObject parseSelectQuery(String selectQuery) {
        JSONObject selectObject = new JSONObject();
        try {
            Pattern syntaxExp = Pattern.compile(selectRegex, Pattern.CASE_INSENSITIVE);
            Matcher queryParts = syntaxExp.matcher(selectQuery);
            String tableName = null;
            String columnNames = null;
            String conditionCol = null;
            String conditionType = null;
            String conditionVal = null;
            if(queryParts.find()) {
                columnNames = queryParts.group(1);
                tableName = queryParts.group(2);
                conditionCol = queryParts.group(3);
                conditionType = queryParts.group(4);
                conditionVal = queryParts.group(5);
            } else {
                System.out.println(errorMessage);
            }
            selectObject.put("tableName", tableName);
            selectObject.put("columns", getColumnArray(columnNames));
            selectObject.put("conditionCol", conditionCol);
            selectObject.put("conditionType", conditionType);
            selectObject.put("conditionVal", conditionVal);
            return selectObject;
        } catch(Exception e) {
            System.out.println(e.getLocalizedMessage());
            return null;
        }
    }

    public boolean executeSelectQuery(JSONObject parsedQuery, User user) {
        try {
            String tableName = (String) parsedQuery.get("tableName");
            JSONArray columns = (JSONArray) parsedQuery.get("columns");
            String conditionCol = (String) parsedQuery.get("conditionCol");
            String conditionType = (String) parsedQuery.get("conditionType");
            String conditionVal = (String) parsedQuery.get("conditionVal");
            if (tableName.isEmpty() || columns.isEmpty()) {
                System.out.println(errorMessage);
                return false;
            }
            JSONParser jsonParser = new JSONParser();
            System.out.println(parsedQuery);
            String dbName = user.getCompleteDatabase().getDbName();
            JSONObject metaData = user.getCompleteDatabase().getMetaData();
            Map<String, JSONArray> x = user.getCompleteDatabase().getTableRecords();
            // get column types and map values and insert
            return true;
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
            return false;
        }
    }

    private JSONArray getColumnArray(String columnNames) {
        JSONArray columns = new JSONArray();
        if(columnNames == null || columnNames.isEmpty()) {
            return columns;
        }
        if (columnNames.equals("*")) {
            columns.add(columnNames);
        } else {
            columnNames = columnNames.substring(0, columnNames.length());
            String[] tempArray = columnNames.split(",");
            Collections.addAll(columns, tempArray);
        }
        return columns;
    }
}
