package com.dbms.service;

import com.dbms.common.Validation;
import com.dbms.model.CompleteDatabase;
import com.dbms.model.Query;
import com.dbms.model.User;
import com.dbms.presentation.ConsoleOutput;
import com.dbms.presentation.ReadUserInput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class QueryExecution {

    private List<String> ddlList;
    private List<String> dmlList;
    private String startTransaction = "start transaction";
    private String showTables = "show tables";
    private String desc = "desc";
    private String exit = "exit";
    private String use = "use";
    private String generateERD = "generateERD";
    private String create = "create";
    private String exportDump = "export";

    @Autowired
    private DDLController ddlController;

    @Autowired
    private DMLController dmlController;

    @Autowired
    private GeneralController generalController;

    @Autowired
    private TransactionController transactionController;

    @Autowired
    private ReadUserInput readUserInput;

    @Autowired
    private ConsoleOutput consoleOutput;

    @Autowired
    private Validation validation;

    @Autowired
    private ERDGenerator erdGenerator;

    @Autowired
    private ExportDump dumpExporter;

    public QueryExecution(){
        createDDLList();
        createDMLList();
    }

    public void createDDLList(){
        ddlList = new ArrayList<>();
        ddlList.add("create");
        ddlList.add("alter");
        ddlList.add("drop");
        ddlList.add("truncate");
    }

    public void createDMLList(){
        dmlList = new ArrayList<>();
        dmlList.add("insert");
        dmlList.add("select");
        dmlList.add("update");
        dmlList.add("delete");
    }

<<<<<<< HEAD
    public Query queryConsole(User user, Query query) throws Exception {
        String userResponse = query.getUserQuery();
        if (validation.isValidInput(userResponse)) {
            List<String> output = new ArrayList<>();
            userResponse = userResponse.trim();
            int userResponseLength = userResponse.length();
            if (userResponseLength > 3) {
                if (userResponse.substring(userResponseLength - 2, userResponseLength - 1)
                        .equalsIgnoreCase(";")) {
                    userResponse = userResponse.substring(0, userResponseLength - 2);
                }
                String[] words = userResponse.split(" ");
                System.out.println("words=" + words);
                String queryType = words[0];
                System.out.println("word0=" + words[0]);

                if (queryType.equalsIgnoreCase(use)) {
                    user = generalController.loadDB(user, userResponse);
                    if(user.getCompleteDatabase() != null){
                        output.add("Database loaded");
                    } else{
                        query.setError(true);
                        output.add("Database is not loaded. Please try again.");
                    }
                } else {
                    CompleteDatabase completeDatabase = user.getCompleteDatabase();

                    if (completeDatabase == null && !create.equalsIgnoreCase(queryType)) {
                        query.setError(true);
                        output.add("Please load a database to show the tables in the database.");
                    } else if (userResponse.equalsIgnoreCase(showTables)) {
                        output = generalController.showTables(user);
                    } else if (queryType.equalsIgnoreCase(desc)) {
                        output = generalController.descTable(user, userResponse);
                    } else if (ddlList.contains(queryType)) {
                        output.add(ddlController.processQuery(user, userResponse));
                    } else if (dmlList.contains(queryType)) {
                        output.addAll(dmlController.processQuery(user, userResponse));
                    } else if (userResponse.equalsIgnoreCase(generateERD)) {
                        output.add(erdGenerator.generateERD(user.getCompleteDatabase().getDbName()));
                    } else {
                        query.setError(true);
                        output.add("Invalid query.");
=======
    public void queryConsole(User user) throws Exception {
        String userResponse;
        boolean invalidUserResponse = true;
        while(invalidUserResponse) {
            userResponse = readUserInput
                    .getStringInput("Console to execute Queries\n" +
                            "1. provide a query to execute\n" +
                            "2. type exit to quit the app\n" +
                            "3. type generateERD to create ERD image\n" +
                            "4. type export {dbname} to create DDL scripts of the database\n"+
                            "sql: ");
            if (validation.isValidInput(userResponse)) {
                userResponse = userResponse.trim();
                int userResponseLength = userResponse.length();
                if (userResponseLength > 3) {
                    if (userResponse.substring(userResponseLength - 2, userResponseLength - 1)
                            .equalsIgnoreCase(";")) {
                        userResponse = userResponse.substring(0, userResponseLength - 2);
                    }
                    String[] words = userResponse.split(" ");
                    System.out.println("words="+words);
                    String queryType = words[0];
                    System.out.println("word0="+words[0]);

                    if(queryType.equalsIgnoreCase(use)){
                        generalController.loadDB(user, userResponse);
                    }
                    else{
                        CompleteDatabase completeDatabase = user.getCompleteDatabase();

                        if(completeDatabase == null && !create.equalsIgnoreCase(queryType)){
                            consoleOutput.print("Please load a database to show the tables in the database.");
                        } else if (userResponse.equalsIgnoreCase(showTables)) {
                            generalController.showTables(user);
                        } else if (userResponse.equalsIgnoreCase(startTransaction)) {
                            transactionController.startTransaction(user);
                        } else if (queryType.equalsIgnoreCase(desc)) {
                            generalController.descTable(user, userResponse);
                        } else if (ddlList.contains(queryType)) {
                            ddlController.processQuery(user, userResponse);
                        } else if (dmlList.contains(queryType)) {
                            dmlController.processQuery(user, userResponse);
                        } else if (userResponse.equalsIgnoreCase(exit)) {
                            break;
                        } else if (userResponse.equalsIgnoreCase(generateERD)) {
                            erdGenerator.generateERD(user.getCompleteDatabase().getDbName());
                        } else if (queryType.equalsIgnoreCase(exportDump)) {
                            dumpExporter.exportSQLDump(user, userResponse);
                        } else {
                            consoleOutput.print("Invalid query.");
                        }
>>>>>>> f5fd08cc370398e7f2d58cbcdca5aea5eb67cb4c
                    }
                }
            }
            if (!query.isError()) {
                query.setResultFlag(true);
                if (query.getResultList() == null) {
                    query.setResultList(new ArrayList<>());
                }
                query.getResultList().addAll(output);
            } else{
                query.setError(true);
                query.setAppResponse(output.get(0));
            }
        }
         else{
            query.setError(true);
            query.setAppResponse("Query is invalid. Please try again.");
        }
        return query;
    }


}
