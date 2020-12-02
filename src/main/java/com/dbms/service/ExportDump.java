package com.dbms.service;

import com.dbms.datasource.Resource;
import com.dbms.model.User;
import com.dbms.presentation.ConsoleOutput;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class ExportDump {

    @Autowired
    private Resource resource;

    @Autowired
    private ConsoleOutput logger;

    private String exportRegex = "(?:export)\\s(\\w+);?";
    private String errorMessage = "Invalid export query. Please check syntax/spacing";

    public List<String> exportSQLDump(User user, String exportQuery){
        return executeExport(user, exportQuery);
    }

    private List<String> executeExport(User user, String exportQuery) {
        List<String> output = new ArrayList<>();
        String accessibleDBName = user.getCompleteDatabase().getDbName();
        Pattern syntaxExp = Pattern.compile(exportRegex, Pattern.CASE_INSENSITIVE);
        Matcher queryParts = syntaxExp.matcher(exportQuery);
        String dbName = null;
        if(queryParts.find()) {
            dbName = queryParts.group(1);
        } else {
            logger.error(errorMessage);
            output.add(errorMessage);
            return output;
        }
        if (!accessibleDBName.equals(dbName)) {
            logger.warning("You do not have access to this database.");
            output.add("You do not have access to this database.");
            return output;
        }
        String metaDataFilePath = resource.dbPath + user.getUserGroup() +"\\" + dbName + "\\metadata.json";

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        String exportFileName = dbName +
                "_export_" + timestamp.getTime() + ".sql";
        String dumpFilePath = resource.dumpPath + exportFileName; // ex: user1_db_export_1233456.sql
        String completeDBName = dbName;
        try {
            boolean exported = exportToFile(metaDataFilePath, dumpFilePath, completeDBName);
            if (!exported) {
                logger.error("Failed to export dump");
                output.add("Failed to export dump");
                return output;
            }
            logger.info("File successfully exported as: " + exportFileName);
            output.add("File successfully exported as: " + exportFileName);
        } catch (Exception e) {
            logger.error("Failed to export dump");
            output.add("Failed to export dump");
            return output;
        }
        return output;
    }

    private boolean exportToFile(String metaDataFilePath, String dumpFilePath, String dbName) throws Exception {
        File metaDataFile = new File(metaDataFilePath);
        if (metaDataFile.exists()) {
            FileReader metaDataReader= null;
            try {
                FileReader reader = new FileReader(metaDataFile);
                JSONParser jsonParser = new JSONParser();
                JSONObject metaData = (JSONObject) jsonParser.parse(reader);
                writeContents(dumpFilePath, metaData, dbName);
            } catch (Exception e) {
                logger.error("Failed to export dump");
            } finally {
                if(metaDataReader!=null) {
                    metaDataReader.close();
                }
            }
            return true;
        } else {
            logger.error("Metadata file does not exists for the requested DB.");
            return false;
        }
    }

    private void writeContents(String filePath, JSONObject metaData, String dbName) throws Exception {
        FileWriter dumpWriter = new FileWriter(filePath);
        try {
            JSONArray tablesMetaData = (JSONArray) metaData.get("tables");
            String useDBCommand = "USE " + dbName + ";";
            dumpWriter.write(useDBCommand);
            dumpWriter.write("\n\n\n\n");

            for (Object table: tablesMetaData) {
                JSONObject tableMetaData = (JSONObject) table;
                ArrayList<String> tableNameArray = new ArrayList<>(tableMetaData.keySet());
                String dropTableCommand = "DROP TABLE IF EXISTS " + tableNameArray.get(0) + ";";
                dumpWriter.write(dropTableCommand);
                dumpWriter.write("\n\n");

                JSONObject tableData = (JSONObject) tableMetaData.get(tableNameArray.get(0));
                JSONObject columns = (JSONObject) tableData.get("columns");
                String createTableCommand = "CREATE TABLE " + tableNameArray.get(0) + " (";
                dumpWriter.write(createTableCommand);
                dumpWriter.write("\n");

                ArrayList<String> columnHeadings = new ArrayList<>(columns.keySet());
                for(String heading : columnHeadings) {
                    dumpWriter.write("`" + heading + "` ");
                    dumpWriter.write(((String) columns.get(heading)).toUpperCase());
                    dumpWriter.write(",\n");
                }

                String primaryKey = (String) tableData.get("primaryKey");
                if(!primaryKey.isEmpty()) {
                    dumpWriter.write("PRIMARY KEY (" + primaryKey + ")\n");
                }
                dumpWriter.write(");\n\n\n");
            }

        } catch (Exception e) {

        } finally {
            if(dumpWriter!=null) {
                dumpWriter.close();
            }
        }
    }
}
