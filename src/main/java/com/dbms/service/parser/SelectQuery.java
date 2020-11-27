package com.dbms.service.parser;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class SelectQuery {

    private final String errorMessage = "Invalid select query. Please check syntax/spacing.";
    private final String tableNameRegex = "(\\w+)";
    private final String columnNameRegex = "((?:\\*)|(?:(?:\\w+)(?:,\\s?\\w+)*))";
    private final String valueTypes = "(?:\".*\"|\\d+(?:\\.\\d+)?|TRUE|true|FALSE|false)";
    private final String conditionRegex = "(?:(?:\\sWHERE\\s)(\\w+=" + valueTypes + "))?";
    private final String selectRegex = "SELECT\\s" +
            columnNameRegex +
            "\\sFROM\\s" +
            tableNameRegex +
            conditionRegex +
            ";?$";

    public void runQuery(String selectQuery) {
        JSONObject parsedQuery = parseSelectQuery(selectQuery);
        executeSelectQuery(parsedQuery);
    }

    public JSONObject parseSelectQuery(String selectQuery) {
        JSONObject selectObject = new JSONObject();
        try {
            Pattern syntaxExp = Pattern.compile(selectRegex, Pattern.CASE_INSENSITIVE);
            Matcher queryParts = syntaxExp.matcher(selectQuery);
            String tableName = null;
            String columnNames = null;
            String condition = null;
            if(queryParts.find()) {
                columnNames = queryParts.group(1);
                tableName = queryParts.group(2);
                condition = queryParts.group(3);
            } else {
                System.out.println(errorMessage);
            }
            selectObject.put("tableName", tableName);
            selectObject.put("columns", getColumnArray(columnNames));
            selectObject.put("condition", getMappings(condition));
            return selectObject;
        } catch(Exception e) {
            System.out.println(e.getLocalizedMessage());
            return null;
        }
    }

    public boolean executeSelectQuery(JSONObject parsedQuery) {
        try {
            String tableName = (String) parsedQuery.get("tableName");
            JSONArray columns = (JSONArray) parsedQuery.get("columns");
            JSONObject condition = (JSONObject) parsedQuery.get("condition");
            if (tableName.isEmpty() || columns.isEmpty()) {
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

    private JSONObject getMappings(String assignments) {
        try {
            JSONObject assignmentMapping = new JSONObject();
            if(assignments == null || assignments.isEmpty()) {
                return assignmentMapping;
            }
            int currentIndex = 0;
            while(currentIndex < assignments.length()) {
                String key, val;
                if (assignments.charAt(currentIndex) == ',') {
                    currentIndex = currentIndex + 1;
                    continue;
                }
                int keyIndex = assignments.indexOf("=", currentIndex);
                key = assignments.substring(currentIndex, keyIndex);
                int valStart = keyIndex + 1;
                int valEnd;
                if (assignments.charAt(valStart) == '"') {
                    valStart = valStart + 1;
                    valEnd = assignments.indexOf('"', valStart);
                } else {
                    valEnd = valStart + 1;
                    while(valEnd < assignments.length() && assignments.charAt(valEnd) != ',') {
                        valEnd = valEnd + 1;
                    }
                }
                val = assignments.substring(valStart, valEnd);
                assignmentMapping.put(key, val);
                currentIndex = valEnd + 1;
            }
            return assignmentMapping;
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
            return null;
        }
    }

}
