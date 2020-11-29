package com.dbms.service.parser;

import org.json.simple.JSONObject;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class DeleteQuery {

    private final String errorMessage = "Invalid delete query. Please check syntax/spacing.";
    private final String tableNameRegex = "(\\w+)";
    private final String valueTypes = "(?:\".*\"|\\d+(?:\\.\\d+)?|TRUE|true|FALSE|false)";
    private final String ConditionValueTypes = "(\".*\"|\\d+(?:.\\d+)?|TRUE|true|FALSE|false)";
    private final String conditionEquality = "(=|<=|>=|>|<)";
    private final String conditionRegex = "(?:(?:\\sWHERE\\s)(?:(\\w+)" + (conditionEquality) + ConditionValueTypes + "))?";
    private final String deleteRegex = "DELETE FROM\\s" +
            tableNameRegex +
            conditionRegex +
            ";?$";

    public void runQuery(String deleteQuery) {
        JSONObject parsedQuery = parseDeleteQuery(deleteQuery);
        executeDeleteQuery(parsedQuery);
    }

    public JSONObject parseDeleteQuery(String deleteQuery) {
        JSONObject selectObject = new JSONObject();
        try {
            Pattern syntaxExp = Pattern.compile(deleteRegex, Pattern.CASE_INSENSITIVE);
            Matcher queryParts = syntaxExp.matcher(deleteQuery);
            String tableName = null;
            String conditionCol = null;
            String conditionType = null;
            String conditionVal = null;
            if(queryParts.find()) {
                tableName = queryParts.group(1);
                conditionCol = queryParts.group(2);
                conditionType = queryParts.group(3);
                conditionVal = queryParts.group(4);
            } else {
                System.out.println(errorMessage);
            }
            selectObject.put("tableName", tableName);
            selectObject.put("conditionCol", conditionCol);
            selectObject.put("conditionType", conditionType);
            selectObject.put("conditionVal", conditionVal);
            return selectObject;
        } catch(Exception e) {
            System.out.println(e.getLocalizedMessage());
            return null;
        }
    }

    public boolean executeDeleteQuery(JSONObject parsedQuery) {
        try {
            String tableName = (String) parsedQuery.get("tableName");
            String conditionCol = (String) parsedQuery.get("conditionCol");
            String conditionType = (String) parsedQuery.get("conditionType");
            String conditionVal = (String) parsedQuery.get("conditionVal");
            if (tableName.isEmpty()) {
                System.out.println(errorMessage);
                return false;
            }
            System.out.println(parsedQuery);
            // get data and delete
            return true;
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
            return false;
        }
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