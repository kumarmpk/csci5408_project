package com.dbms.service.parser;

import org.json.simple.JSONObject;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class UpdateQuery {

    private final String errorMessage = "Invalid update query. Please check syntax/spacing.";
    private final String tableNameRegex = "(\\w+)";
    private final String valueTypes = "(?:\".*\"|\\d+(?:.\\d+)?|TRUE|true|FALSE|false)";
    private final String ConditionValueTypes = "(\".*\"|\\d+(?:.\\d+)?|TRUE|true|FALSE|false)";
    private final String conditionEquality = "(=|<=|>=|>|<)";
    // no spaces allowed for updations
    private final String assignmentRegex = "((?:\\w+="+ valueTypes + ")(?:,\\w+="+ valueTypes + ")*)";
    private final String conditionRegex = "(?:(?:\\sWHERE\\s)(?:(\\w+)" + (conditionEquality) + ConditionValueTypes + "))?";
    private final String updateRegex = "UPDATE " +
            tableNameRegex +
            "\\sSET\\s" +
            assignmentRegex +
            conditionRegex +
            ";?$";

    public void runQuery(String updateQuery) {
        JSONObject parsedQuery = parseUpdateQuery(updateQuery);
        executeUpdateQuery(parsedQuery);
    }

    public JSONObject parseUpdateQuery(String updateQuery) {
        JSONObject insertObject = new JSONObject();
        try {
            Pattern syntaxExp = Pattern.compile(updateRegex, Pattern.CASE_INSENSITIVE);
            Matcher queryParts = syntaxExp.matcher(updateQuery);
            String tableName = null;
            String assignments = null;
            String conditionCol = null;
            String conditionType = null;
            String conditionVal = null;
            if(queryParts.find()) {
                tableName = queryParts.group(1);
                assignments = queryParts.group(2);
                conditionCol = queryParts.group(3);
                conditionType = queryParts.group(4);
                conditionVal = queryParts.group(5);
            } else {
                System.out.println(errorMessage);
            }
            insertObject.put("tableName", tableName);
            insertObject.put("assignments", getMappings(assignments, "="));
            insertObject.put("conditionCol", conditionCol);
            insertObject.put("conditionType", conditionType);
            insertObject.put("conditionVal", conditionVal);
            return insertObject;
        } catch(Exception e) {
            System.out.println(e.getLocalizedMessage());
            return null;
        }
    }

    public boolean executeUpdateQuery(JSONObject parsedQuery) {
        try {
            String tableName = (String) parsedQuery.get("tableName");
            JSONObject assignments = (JSONObject) parsedQuery.get("assignments");
            String conditionCol = (String) parsedQuery.get("conditionCol");
            String conditionType = (String) parsedQuery.get("conditionType");
            String conditionVal = (String) parsedQuery.get("conditionVal");
            if (tableName.isEmpty() || assignments.isEmpty()) {
                System.out.println(errorMessage);
                return false;
            }
            System.out.println(parsedQuery);
            // get column types and data and update
            return true;
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
            return false;
        }
    }

    private JSONObject getMappings(String assignments, String equality) {
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
                int keyIndex = assignments.indexOf(equality, currentIndex);
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