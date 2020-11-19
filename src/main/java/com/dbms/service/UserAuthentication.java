package com.dbms.service;

import com.dbms.DBMSApp;
import com.dbms.datasource.IReadFile;
import com.dbms.datasource.IWriteFile;
import com.dbms.models.User;
import com.dbms.presentation.ConsoleOutput;
import com.dbms.presentation.IConsoleOutput;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Component
public class UserAuthentication {

    @Autowired
    private IReadFile readFile;

    @Autowired
    private IWriteFile writeFile;

    @Autowired
    private IConsoleOutput consoleOutput;

    public User checkUser(String userName) throws Exception {

        List<User> userList = getUserDetails();
        for(User user : userList){
            if(user.getUserName().equalsIgnoreCase(userName)) {
                return user;
            }
        }

        return null;
    }

    public void saveUser(String userName, String password) throws Exception {
        User user = new User();
        user.setUserName(userName);
        user.setPassword(password);
        user.setCreatedDate(new Date());
        user.setLastLoggedInDate(new Date());
        user.setId(getLatestId());
        saveUserToFile(user);
    }

    public void saveUserToFile(User user) throws IOException, ParseException {
        JSONArray userJsonArray = getUserFile();
        JSONObject userJsonObject = new JSONObject();
        userJsonObject.put("id", user.getId());
        userJsonObject.put("userName", user.getUserName());
        userJsonObject.put("password", user.getPassword());
        DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
        String createdDateStr = dateFormat.format(user.getCreatedDate());
        userJsonObject.put("createdDate", createdDateStr);
        String lastLoggedInDateStr = dateFormat.format(user.getLastLoggedInDate());
        userJsonObject.put("lastLoggedInDate", lastLoggedInDateStr);
        userJsonArray.add(userJsonObject);
        writeFile.writeFile(userJsonArray.toJSONString(), "user.json");
    }

    public long getLatestId() throws Exception {
        long id = 0;

        List<User> userList = getUserDetails();
        Collections.sort(userList, Collections.reverseOrder());
        if (userList.size() > 0) {
            id = userList.get(0).getId() + 1;
        } else {
            id = 0;
        }
        return id;
    }

    public List<User> getUserDetails() throws Exception {
        List<User> userList = new ArrayList<>();
        try {
            for(Object userObject : getUserFile()){
                JSONObject userJsonObject = (JSONObject) userObject;
                User user = new User();
                user.setId((Long) userJsonObject.get("id"));
                user.setUserName((String) userJsonObject.get("userName"));
                user.setPassword((String) userJsonObject.get("password"));
                String createdDateStr = (String) userJsonObject.get("createdDate");
                Date createdDate = new SimpleDateFormat("dd-MMM-yyyy").parse(createdDateStr);
                user.setCreatedDate(createdDate);
                String lastLoggedInDateStr = (String) userJsonObject.get("lastLoggedInDate");
                Date lastLoggedInDate = new SimpleDateFormat("dd-MMM-yyyy").parse(lastLoggedInDateStr);
                user.setLastLoggedInDate(lastLoggedInDate);
                userList.add(user);
            }
        } catch (Exception e){
            consoleOutput.info("UserLoginRegister: getUserDetails: Exception: "+e);
            throw e;
        }

        return userList;
    }

    public JSONArray getUserFile() throws IOException, ParseException {
        return readFile.readJSON("user.json");
    }


}
