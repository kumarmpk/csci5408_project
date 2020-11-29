package com.dbms.service;

import com.dbms.models.User;
import com.dbms.service.parser.DeleteQuery;
import com.dbms.service.parser.InsertQuery;
import com.dbms.service.parser.SelectQuery;
import com.dbms.service.parser.UpdateQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class DMLController {

    private final String insert = "insert";
    private final String select = "select";
    private final String update = "update";
    private final String delete = "delete";

    @Autowired
    private InsertQuery insertQuery;

    @Autowired
    private DeleteQuery deleteQuery;

    @Autowired
    private SelectQuery selectQuery;

    @Autowired
    private UpdateQuery updateQuery;

    public DMLController(){
    }

    public void processQuery(User user, String query){
        String words[] = query.split(" ");
        int queryWordCount = words.length;
        if(queryWordCount > 1) {
            String queryType = words[0];
            switch (queryType) {
                case insert:
                    insertQuery.runQuery(query, user);
                    break;
                case select:
                    selectQuery.runQuery(query, user);
                    break;
                case delete:
                    deleteQuery.runQuery(query, user);
                    break;
                case update:
                    updateQuery.runQuery(query, user);
                    break;
            }
        }
    }

    public void processQueryForTransaction(User user, String query){
        String words[] = query.split(" ");
        int queryWordCount = words.length;
        if(queryWordCount > 1) {
            String queryType = words[0];
            switch (queryType) {
                case insert:
                    insertQuery.parseInsertQuery(query);
                    break;
                case select:
                    selectQuery.parseSelectQuery(query);
                    break;
                case delete:
                    deleteQuery.parseDeleteQuery(query);
                    break;
                case update:
                    updateQuery.parseUpdateQuery(query);
                    break;
            }
        }
    }

}
