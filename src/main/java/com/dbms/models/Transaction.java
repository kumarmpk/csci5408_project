package com.dbms.models;

import java.util.List;

public class Transaction {

    private List<String> queryList;

    public List<String> getQueryList() {
        return queryList;
    }

    public void setQueryList(List<String> queryList) {
        this.queryList = queryList;
    }
}
