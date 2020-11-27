package com.dbms.service.parser;

import org.json.simple.JSONObject;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class UpdateQuery {

    private static final String errorMessage = "Invalid update query. Please check syntax/spacing.";
    private static final String tableNameRegex = "(\\w+)";
    private static final String valueTypes = "(?:\".*\"|\\d+(?:.\\d+)?|TRUE|true|FALSE|false)";
    // no spaces allowed for updations
    private static final String assignmentRegex = "((?:\\w+=" + valueTypes + ")(?:,\\w+=" + valueTypes + ")*)";
    private static final String conditionRegex = "(?:(?:\\sWHERE\\s)(\\w+=" + valueTypes + "))?";
    private static final String updateRegex = "UPDATE " +
            tableNameRegex +
            "\\sSET\\s" +
            assignmentRegex +
            conditionRegex +
            ";?$";

    public static void runQuery(String updateQuery) {
        JSONObject parsedQuery = parseUpdateQuery(updateQuery);
        executeUpdateQuery(parsedQuery);
    }

    public static JSONObject parseUpdateQuery(String updateQuery) {
        JSONObject insertObject = new JSONObject();
        try {
            Pattern syntaxExp = Pattern.compile(updateRegex, Pattern.CASE_INSENSITIVE);
            Matcher queryParts = syntaxExp.matcher(updateQuery);
            String tableName = null;
            String assignments = null;
            String condition = null;
            if(queryParts.find()) {
                tableName = queryParts.group(1);
                assignments = queryParts.group(2);
                condition = queryParts.group(3);
            } else {
                System.out.println(errorMessage);
            }
            insertObject.put("tableName", tableName);
            insertObject.put("assignments", getMappings(assignments));
            insertObject.put("condition", getMappings(condition));
            return insertObject;
        } catch(Exception e) {
            System.out.println(e.getLocalizedMessage());
            return null;
        }
    }

    public static boolean executeUpdateQuery(JSONObject parsedQuery) {
        try {
            String tableName = (String) parsedQuery.get("tableName");
            JSONObject assignments = (JSONObject) parsedQuery.get("assignments");
            JSONObject condition = (JSONObject) parsedQuery.get("condition");
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

    private static JSONObject getMappings(String assignments) {
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

    public static void main(String []a) {
        String s1 = "UPDATE tab1 SET col1=12.2,col2=\"done\",col3=false,col4=TRUE WHERE col5=12.6;";
        String s2 = "UPDATE tab1 SET col1=12.2,col2=\"done\",col3=false,col4=TRUE;";
        runQuery(s1);
        runQuery(s2);
    }
}
