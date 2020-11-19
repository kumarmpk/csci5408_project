package com.dbms.models;

import java.util.Date;

public class User implements Comparable<User>{

    private long id;
    private String userName;
    private String password;
    private Date createdDate;
    private Date lastLoggedInDate;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getLastLoggedInDate() {
        return lastLoggedInDate;
    }

    public void setLastLoggedInDate(Date lastLoggedInDate) {
        this.lastLoggedInDate = lastLoggedInDate;
    }

    @Override
    public int compareTo(User user) {
        long compare = this.id - user.getId();
        if(compare > 0){
            return 1;
        } else if(compare < 0){
            return -1;
        }
        return 0;
    }
}
