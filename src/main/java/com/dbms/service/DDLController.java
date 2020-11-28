package com.dbms.service;

import com.dbms.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DDLController {

    private final String create = "create";
    private final String drop = "drop";
    private final String truncate = "truncate";
    private final String alter = "alter";

    @Autowired
    private CreateTable createTable;

    public void processQuery(User user, String query){
        String words[] = query.split(" ");
        int queryWordCount = words.length;
        if(queryWordCount > 1) {
            String qyeryType = words[0];
            switch (qyeryType) {
                case create:

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
