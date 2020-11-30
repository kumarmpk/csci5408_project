package com.dbms.service.parser;

import com.dbms.models.User;
import com.dbms.presentation.IConsoleOutput;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.regex.*;

@Component
public class InsertQuery {

    @Autowired
    private IConsoleOutput logger;

    @Autowired
    private Utils utils;

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

            String dbName = user.getCompleteDatabase().getDbName();
            JSONObject metaData = user.getCompleteDatabase().getMetaData();
            Map<String, JSONArray> tableRecords = user.getCompleteDatabase().getTableRecords();
            JSONArray tablesMetaData = (JSONArray) metaData.get("tables");
            JSONArray currentTableRecords = tableRecords.get(tableName);
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

            ArrayList filteredColumns = new ArrayList();

            if (columns.size() == 0) {
                Set keys = currentTableMetadata.keySet();
                for(Object key : keys) {
                    key = (String) key;
                    filteredColumns.add(key);
                }
            } else {
                filteredColumns = (ArrayList) columns;
            }

            if (filteredColumns.size() != values.size()) {
                logger.error("errorMessage");
                logger.error(errorMessage);
                return false;
            }

            JSONObject newRow = createRow(filteredColumns, values, currentTableMetadata);

            currentTableRecords.add(newRow);

            String fileNameWithDB = user.getUserName() + "_" + dbName + "\\" + tableName + ".json";

            utils.updateTableFile(currentTableRecords.toJSONString(), fileNameWithDB);

            logger.info("1 row inserted.");
            return true;
        } catch (Exception e) {
            System.out.println(errorMessage);
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
            if (columnValues.charAt(currIndex) == ',' || columnValues.charAt(currIndex) == ')') {
                currIndex = currIndex + 1;
                continue;
            } else if (columnValues.charAt(currIndex) == '"') { // strings
                endIndex = columnValues.indexOf('"', currIndex + 1);
                currIndex++;
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
            values.add(columnValues.substring(currIndex, endIndex).trim());
            currIndex = endIndex + 1;
        }
        return values;
    }

    private JSONObject createRow(ArrayList filteredColumns, ArrayList values, JSONObject metaData) {
        JSONObject newRecord = new JSONObject();
        for (int i = 0; i < filteredColumns.size(); i++) {
            String colType = (String) metaData.get(filteredColumns.get(i));
            if (colType.contains("int")) {
                int value = Integer.parseInt((String) values.get(i));
                newRecord.put(filteredColumns.get(i), value);
            } else if (colType.contains("varchar")) {
                String value = (String) values.get(i);
                if (value.startsWith("\"")) value = value.substring(1);
                if (value.endsWith("\"")) value = value.substring(0, value.length() - 1);
                newRecord.put(filteredColumns.get(i), value);
            } else if (colType.contains("float")) {
                float value = Float.parseFloat((String) values.get(i));
                newRecord.put(filteredColumns.get(i), value);
            } else if (colType.contains("boolean")) {
                boolean value = Boolean.parseBoolean((String) values.get(i));
                newRecord.put(filteredColumns.get(i), value);
            }
        }

        return newRecord;
    }
}
