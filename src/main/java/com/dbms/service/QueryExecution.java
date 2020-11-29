package com.dbms.service;

import com.dbms.common.Constants;
import com.dbms.common.Validation;
import com.dbms.models.CompleteDatabase;
import com.dbms.models.User;
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

    public void queryConsole(User user) throws Exception {
        String userResponse;
        boolean invalidUserResponse = true;
        while(invalidUserResponse) {
            userResponse = readUserInput
                    .getStringInput("Console to execute Queries\n" +
                            "1. provide a query to execute\n" +
                            "2. type exit to quit the app\n" +
                            "3. type generateERD to create ERD image\n" +
                            "4. type exportDump to create DDL scripts of the database\n"+
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
                        } else if(userResponse.equalsIgnoreCase(generateERD)){
                            erdGenerator.generateERD(user.getCompleteDatabase().getDbName());
                        } else {
                            consoleOutput.print("Invalid query.");
                        }
                    }
                }
            }
        }
    }


}
