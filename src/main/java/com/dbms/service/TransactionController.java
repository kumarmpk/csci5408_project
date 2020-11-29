package com.dbms.service;

import com.dbms.common.Validation;
import com.dbms.models.Transaction;
import com.dbms.models.User;
import com.dbms.presentation.ConsoleOutput;
import com.dbms.presentation.ReadUserInput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class TransactionController {

    private String showTables = "show tables";
    private String desc = "desc";
    private String exit = "exit";
    private List<String> ddlList;
    private List<String> dmlList;
    private String commit = "commit";
    private String rollback = "rollback";

    @Autowired
    private ReadUserInput readUserInput;

    @Autowired
    private Validation validation;

    @Autowired
    private DDLController ddlController;

    @Autowired
    private DMLController dmlController;

    @Autowired
    private GeneralController generalController;

    @Autowired
    private ConsoleOutput consoleOutput;

    public TransactionController(){
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

    public void startTransaction(User user){
        boolean invalidUserResponse = true;
        String userResponse;
        user.setTransaction(new Transaction());
        user.getTransaction().setQueryList(new ArrayList<>());
        boolean commitFlag = false;
        while(invalidUserResponse){
            boolean validQuery = false;
            userResponse = readUserInput
                    .getStringInput("Query part of a transaction\n"+
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
                    String queryType = words[0];

                    if (userResponse.equalsIgnoreCase(showTables)) {
                        generalController.showTables(user);
                    } else if (queryType.equalsIgnoreCase(desc)) {
                        generalController.descTable(user, userResponse);
                    } else if (ddlList.contains(queryType)) {
                        ddlController.processQueryForTransaction(user, userResponse);
                        commitFlag = true;
                        break;
                    } else if (dmlList.contains(queryType)) {
                        dmlController.processQueryForTransaction(user, userResponse);
                    } else if (userResponse.equalsIgnoreCase(exit)) {
                        break;
                    } else if (userResponse.equalsIgnoreCase(commit)){
                        commitFlag = true;
                        break;
                    } else if(userResponse.equalsIgnoreCase(rollback)){
                        break;
                    }
                    else {
                        consoleOutput.print("Invalid query.");
                    }
                }
            }
            if(validQuery){
                user.getTransaction().getQueryList().add(userResponse);
            }
        }
        if(commitFlag){
            commitTransaction(user);
        }
    }

    private void commitTransaction(User user){
        List<String> queries = user.getTransaction().getQueryList();
        for(String query : queries){
            dmlController.processQuery(user, query);
        }
    }

}
