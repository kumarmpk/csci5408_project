package com.dbms.service;

import com.dbms.models.User;
import com.dbms.service.parser.DeleteQuery;
import com.dbms.service.parser.InsertQuery;
import com.dbms.service.parser.SelectQuery;
import com.dbms.service.parser.UpdateQuery;
import com.dbms.service.parser.CreateDB;
import com.dbms.service.parser.CreateTable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DDLController {

    private final String create = "create";
    private final String drop = "drop";
    private final String truncate = "truncate";
    private final String alter = "alter";

    @Autowired
    private CreateDB createdbQuery;
    
    @Autowired
    private CreateTable createtableQuery;

    public void processQuery(User user, String query){
        String words[] = query.split(" ");
        int queryWordCount = words.length;
        if(queryWordCount > 1) {
            String qyeryType = words[0];
            switch (qyeryType) {
                case create:
                	if(query.contains("DATABASE")||(query.contains("database"))) {
                	    createdbQuery.runDBQuery(query, user);
                	}
                	if(query.contains("TABLE")||(query.contains("table"))) {
                		createtableQuery.runTableQuery(query, user);
                	}
                    break;
                case alter:

                    break;
                case drop:

                    break;
                case truncate:

                    break;
            }
        }
    }

    public void processQueryForTransaction(User user, String query){
        String words[] = query.split(" ");
        int queryWordCount = words.length;
        if(queryWordCount > 1) {
            String qyeryType = words[0];
            switch (qyeryType) {
                case create:
                    if(query.contains("DATABASE")||(query.contains("database"))) {
                        createdbQuery.parseCreateDBQuery(query);
                    }
                    if(query.contains("TABLE")||(query.contains("table"))) {
                        createtableQuery.parseCreateTableQuery(query);
                    }
                    break;
                case alter:

                    break;
                case drop:

                    break;
                case truncate:

                    break;
            }
        }
    }


}
