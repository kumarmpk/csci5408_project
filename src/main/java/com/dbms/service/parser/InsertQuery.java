package com.dbms.service.parser;

import com.dbms.models.User;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;
import java.util.regex.*;

@Component
public class InsertQuery {

    private final String errorMessage = "Invalid insert query. Please check syntax/spacing.";
    private final String tableNameRegex = "(\\w+)";
    private final String columnHeadingRegex = "(\\((?:\\w+)(?:,\\s?\\w+)*\\))?";
    private final String valueTypes = "(?:\".*\"|\\d+(?:.\\d+)?|TRUE|true|FALSE|false)";
    private final String columnValuesRegex = "(\\((?:" + valueTypes + ")(?:,\\s?" + valueTypes + ")*\\))";
    private final String insertRegex = "INSERT INTO " +
            tableNameRegex +
            "\\s" +
            columnHeadingRegex +
            "\\s*" +
            "VALUES" +
            "\\s*" +
            columnValuesRegex +
            ";?$";

    public void runQuery(String insertQuery, User user) {
        JSONObject parsedQuery = parseInsertQuery(insertQuery);
        executeInsertQuery(parsedQuery, user);
    }

    public JSONObject parseInsertQuery(String insertQuery) {
        JSONObject insertObject = new JSONObject();
        try {
            Pattern syntaxExp = Pattern.compile(insertRegex, Pattern.CASE_INSENSITIVE);
            Matcher queryParts = syntaxExp.matcher(insertQuery);
            String tableName = null;
            String columnNames = null;
            String columnValues = null;
            if(queryParts.find()) {
                tableName = queryParts.group(1);
                columnNames = queryParts.group(2);
                columnValues = queryParts.group(3);
            } else {
                System.out.println(errorMessage);
            }
            insertObject.put("tableName", tableName);
            insertObject.put("columns", getColumnArray(columnNames));
            insertObject.put("values", getValuesArray(columnValues));
            return insertObject;
        } catch(Exception e) {
            System.out.println(e.getLocalizedMessage());
            return null;
        }
    }

    public boolean executeInsertQuery(JSONObject parsedQuery, User user) {
        try {
            String tableName = (String) parsedQuery.get("tableName");
            JSONArray columns = (JSONArray) parsedQuery.get("columns");
            JSONArray values = (JSONArray) parsedQuery.get("values");
            if (tableName.isEmpty()) {
                System.out.println(errorMessage);
                return false;
            }
            System.out.println(parsedQuery);
            String dbName = user.getCompleteDatabase().getDbName();
            JSONObject metaData = user.getCompleteDatabase().getMetaData();
            Map<String, JSONArray> x = user.getCompleteDatabase().getTableRecords();
            // get data
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
        columnNames = columnNames.substring(1, columnNames.length() - 1); // to remove ( )
        String[] tempArray = columnNames.split(",");
        Collections.addAll(columns, tempArray);
        return columns;
    }

    private JSONArray getValuesArray(String columnValues){
        JSONArray values = new JSONArray();
        if(columnValues == null || columnValues.isEmpty()) {
            return values;
        }
        int currIndex = 1; // to avoid open bracket "("
        while(currIndex < columnValues.length()) {
            int endIndex;
            if (columnValues.charAt(currIndex) == ',') {
                currIndex = currIndex + 1;
                continue;
            } else if (columnValues.charAt(currIndex) == '"') { // strings
                endIndex = columnValues.indexOf('"', currIndex + 1);
            } else {
                try {
                    endIndex = currIndex + 1;
                    while (columnValues.charAt(endIndex) != ',' && columnValues.charAt(endIndex) != ')') {
                        endIndex  = endIndex + 1;
                    }
                } catch (Exception e) {
                    endIndex = -1;
                }
            }
            if (endIndex <= currIndex) {
                System.out.println(errorMessage);
                return null;
            }
            values.add(columnValues.substring(currIndex + 1, endIndex).trim());
            currIndex = endIndex + 1;
        }
        return values;
    }

}
