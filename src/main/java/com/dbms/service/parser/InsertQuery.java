package com.dbms.service.parser;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import java.util.Collections;
import java.util.regex.*;

public class InsertQuery {

    private static final String errorMessage = "Invalid insert query. Please check syntax/spacing.";
    private static final String tableNameRegex = "(\\w+)";
    private static final String columnHeadingRegex = "(\\((?:\\w+)(?:,\\s?\\w+)*\\))?";
    private static final String valueTypes = "(?:\".*\")|(?:\\d+.?\\d+)|(?:'.')|TRUE|true|FALSE|false";
    private static final String columnValuesRegex = "(\\((?:.*)*\\))";
    private static final String insertRegex = "INSERT INTO " +
            tableNameRegex +
            "\\s" +
            columnHeadingRegex +
            "\\s*" +
            "VALUES" +
            "\\s*" +
            columnValuesRegex +
            ";?$";

    public static void runQuery(String insertQuery) {
        JSONObject parsedQuery = parseInsertQuery(insertQuery);
        executeInsertQuery(parsedQuery);
    }

    public static JSONObject parseInsertQuery(String insertQuery) {
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

    public static boolean executeInsertQuery(JSONObject parsedQuery) {
        try {
            String tableName = (String) parsedQuery.get("tableName");
            JSONArray columns = (JSONArray) parsedQuery.get("columns");
            JSONArray values = (JSONArray) parsedQuery.get("values");
            if (tableName.isEmpty() || columns.isEmpty() || values.isEmpty() || columns.size() != values.size()) {
                System.out.println(errorMessage);
                return false;
            }
            System.out.println(parsedQuery);
            // get column types and map values and insert
            return true;
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
            return false;
        }
    }

    private static JSONArray getColumnArray(String columnNames) {
        JSONArray columns = new JSONArray();
        if(columnNames.isEmpty()) {
            return columns;
        }
        columnNames = columnNames.substring(1, columnNames.length() - 1); // to remove ( )
        String[] tempArray = columnNames.split(",");
        Collections.addAll(columns, tempArray);
        return columns;
    }
    private static JSONArray getValuesArray(String columnValues){
        JSONArray values = new JSONArray();
        if(columnValues.isEmpty()) {
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

    public static void main(String []a) {
        runQuery("INSERT INTO hello_world (col1,col2,col3,col4) VALUES (\"ku78*&\",12.3, 12, false);");
    }
}
