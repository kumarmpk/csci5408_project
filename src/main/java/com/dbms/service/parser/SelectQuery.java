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
    private final String ConditionValueTypes = "(\".*\"|\\d+(?:.\\d+)?|TRUE|true|FALSE|false)";
    private final String conditionEquality = "(=|<=|>=|>|<)";
    private final String conditionRegex = "(?:(?:\\sWHERE\\s)(?:(\\w+)" + (conditionEquality) + ConditionValueTypes + "))?";
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

    public boolean executeSelectQuery(JSONObject parsedQuery) {
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
