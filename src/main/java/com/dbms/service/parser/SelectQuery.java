package com.dbms.service.parser;

import com.dbms.models.User;
import com.dbms.presentation.IConsoleOutput;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class SelectQuery {

    @Autowired
    private IConsoleOutput logger;

    private static String dbPath = Paths.get("").toAbsolutePath().toString() + "\\data\\";

    private final String errorMessage = "Invalid select query. Please check syntax/spacing.";
    private final String tableNameRegex = "(\\w+)";
    private final String columnNameRegex = "((?:\\*)|(?:(?:\\w+)(?:,\\s?\\w+)*))";
    private final String ConditionValueTypes = "(\".*\"|\\d+(?:.\\d+)?|TRUE|true|FALSE|false)";
    private final String conditionEquality = "(=|<=|>=|>|<|!=)";
    private final String conditionRegex = "(?:(?:\\sWHERE\\s)(?:(\\w+)" + "\\s?" + (conditionEquality) + "\\s?" + ConditionValueTypes + "))?";
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
                logger.error(errorMessage);
            }
            selectObject.put("tableName", tableName);
            selectObject.put("columns", getColumnArray(columnNames));
            selectObject.put("conditionCol", conditionCol);
            selectObject.put("conditionType", conditionType);
            selectObject.put("conditionVal", conditionVal);
            return selectObject;
        } catch(Exception e) {
            logger.error(e.getLocalizedMessage());
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
                logger.error(errorMessage);
                return false;
            }
            String dbName = user.getCompleteDatabase().getDbName();
            Map<String, JSONArray> tableRecords = user.getCompleteDatabase().getTableRecords();
            JSONObject metaData = user.getCompleteDatabase().getMetaData();
            JSONArray tablesMetaData = (JSONArray) metaData.get("tables");
            JSONObject currentTableMetadata = null;
            for (Object curObj : tablesMetaData) {
                JSONObject tableObj = (JSONObject) curObj;
                JSONObject metadata = (JSONObject) tableObj.get(tableName);
                if (metadata != null) {
                    currentTableMetadata = (JSONObject) metadata.get("columns");
                    break;
                }
            }
            if (currentTableMetadata == null) {
                logger.error("Unable to fetch table metadata");
                return false;
            }

            JSONArray currentTableRecords = tableRecords.get(tableName);
            ArrayList filteredRows = new ArrayList();
            ArrayList filteredColumns = new ArrayList();

            if (conditionCol == null) {
                filteredRows = currentTableRecords;
            } else {
                filteredRows = filterRows(conditionCol, conditionType, conditionVal,
                        currentTableMetadata, currentTableRecords);
            }

            if (columns.size() == 1 && columns.get(0).equals("*")) {
                Set keys = currentTableMetadata.keySet();
                for(Object key : keys) {
                    key = (String) key;
                    filteredColumns.add(key);
                }
            } else {
                filteredColumns = (ArrayList) columns;
            }

            if (filteredColumns.size() < 1) {
                logger.error("Unable to fetch table columns");
                return false;
            }

            displayOutput(filteredRows, filteredColumns);

            return true;
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage());
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

    private ArrayList filterRows(String col, String condition, String value,
                                 JSONObject metadata, JSONArray rows) {

        ArrayList filteredRows = new ArrayList();
        String colType = (String) metadata.get(col);
        for (Object obj : rows) {
            boolean matched = false;
            JSONObject curObj = (JSONObject) obj;
            if (colType.contains("int")) {
                int originalValue = (int) (long) curObj.get(col);
                int givenValue = Integer.parseInt(value);
                switch(condition){
                    case "<":
                        matched = originalValue < givenValue;
                        break;
                    case "<=":
                        matched = originalValue <= givenValue;
                        break;
                    case ">":
                        matched = originalValue > givenValue;
                        break;
                    case ">=":
                        matched = originalValue >= givenValue;
                        break;
                    case "=":
                        matched = originalValue == givenValue;
                        break;
                    case "!=":
                        matched = originalValue != givenValue;
                        break;
                    default:
                        matched = false;
                        break;
                }
            } else if (colType.contains("varchar")) {
                String originalValue = (String) curObj.get(col);
                String givenValue = value;
                if (givenValue.startsWith("\"")) givenValue = givenValue.substring(1);
                if (givenValue.endsWith("\"")) givenValue = givenValue.substring(0, givenValue.length() - 1);
                switch(condition){
                    case "=":
                        matched = originalValue.equals(givenValue);
                        break;
                    case "!=":
                        matched = !originalValue.equals(givenValue);
                        break;
                    default:
                        matched = false;
                        break;
                }
            }
            if (matched) {
                filteredRows.add(curObj);
            }
        }
        return filteredRows;
    }

    private void displayOutput(ArrayList filteredRows, ArrayList filteredColumns) {
        String separator = new String(new char[(20*filteredColumns.size()) + filteredColumns.size() +1])
                .replace('\0', '_');

        System.out.println(separator);
        System.out.printf("|");
        for (Object filteredColumn : filteredColumns) {
            System.out.printf("%-20S|", filteredColumn);
        }
        System.out.println();
        System.out.println(separator);

        for (Object filteredRow : filteredRows) {
            JSONObject row = (JSONObject) filteredRow;
            System.out.printf("|");
            for (Object filteredColumn : filteredColumns) {
                System.out.printf("%-20s|", row.get(filteredColumn));
            }
            System.out.println();
            System.out.println(separator);
        }
        System.out.println();
        String printQuery = filteredRows.size() + " row(s) returned.";
        logger.info(printQuery);
    }
}
