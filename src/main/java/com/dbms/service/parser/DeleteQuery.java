package com.dbms.service.parser;

import org.json.simple.JSONObject;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DeleteQuery {

    private final String errorMessage = "Invalid delete query. Please check syntax/spacing.";
    private final String tableNameRegex = "(\\w+)";
    private final String valueTypes = "(?:\".*\"|\\d+(?:\\.\\d+)?|TRUE|true|FALSE|false)";
    private final String conditionRegex = "(?:(?:\\sWHERE\\s)(\\w+=" + valueTypes + "))?";
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
            String condition = null;
            if(queryParts.find()) {
                tableName = queryParts.group(1);
                condition = queryParts.group(2);
            } else {
                System.out.println(errorMessage);
            }
            selectObject.put("tableName", tableName);
            selectObject.put("condition", getMappings(condition));
            return selectObject;
        } catch(Exception e) {
            System.out.println(e.getLocalizedMessage());
            return null;
        }
    }

    public boolean executeDeleteQuery(JSONObject parsedQuery) {
        try {
            String tableName = (String) parsedQuery.get("tableName");
            JSONObject condition = (JSONObject) parsedQuery.get("condition");
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
