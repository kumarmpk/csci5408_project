package com.dbms.service;

import com.dbms.common.Constants;
import com.dbms.common.Validation;
import com.dbms.datasource.IReadFile;
import com.dbms.datasource.IWriteFile;
import com.dbms.datasource.ReadFile;
import com.dbms.datasource.Resource;
import com.dbms.models.User;
import com.dbms.presentation.ConsoleOutput;
import com.dbms.presentation.ReadUserInput;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.Map;

@Component
public class CreateTable {

    static String tablename="";
    static String coltype="";

    static HashMap<String, File> store = new HashMap<String, File>();

    @Autowired
    private IWriteFile writeFile;

   /* @Autowired
    private ConsoleOutput consoleOutput;*/

    @Autowired
    private ReadFile readFile;

    @Autowired
    private Resource resource;

    public Map<String, JSONArray> createTableForUser(User user, ReadUserInput rUI, Validation validation , String dbname, String username, String datapath, ConsoleOutput consoleOutput) throws Exception {
        Map<String, JSONArray> tableRecords = null;
        String uR;
        boolean invalidUserResponse = true;

        while(invalidUserResponse) {
            uR = rUI.getStringInput("Enter T to create new table.");
            if (validation.isValidInput(uR) && uR.equalsIgnoreCase(Constants.T)) {
                tableRecords = createTable(user.getUserName(),validation,rUI,dbname,username,datapath, consoleOutput);
                invalidUserResponse = false;
            }  else {
                consoleOutput.warning("Invalid response. Please try again.");
                invalidUserResponse = true;
            }
        }
        return tableRecords;
    }

    private Map<String, JSONArray> createTable(String userName, Validation validation, ReadUserInput rUI, String dbname , String username, String datapath, ConsoleOutput consoleOutput) throws IOException {
        String userResponse ;
        boolean invalidUserResponse = true;
        while(invalidUserResponse){

            userResponse = rUI
                    .getStringInput("Enter the query to create table");

            Pattern p = Pattern.compile("create table (\\w+[^0-9]) [(]((((\\w+) (varchar|int)[(]\\d+[)])(,)*\\s*)+)[)];", Pattern.CASE_INSENSITIVE);

            Matcher m1 = p.matcher(userResponse);
            Matcher m2 = p.matcher(userResponse);

            if(!(m2.matches())) {
                consoleOutput.warning("Invalid table query. Please try again.");
                invalidUserResponse = true;

            }
            else {

                while (m1.find()) {
                    tablename=m1.group(1);
                    coltype=m1.group(2);
                }
                try {
                    JSONObject obj = new JSONObject();
                    JSONArray arr = new JSONArray();
                    HashMap<String, String> map = new HashMap<String, String>();
                    String s[]= coltype.split("[\\s,]+");
                    JSONObject jsonarr = new JSONObject();

                    for(int i=0;i<s.length-1;i=i+2) {
                        JSONObject js = new JSONObject();
                        map.put("column name", s[i]);
                        map.put("column type", s[i+1]);
                        arr.add(map);

                    }
                   // obj.put(tablename, arr);

                    try (FileWriter file = new FileWriter(datapath+username+"_"+dbname+"\\"+tablename+".json")) {

                        file.write(arr.toJSONString());
                        file.close();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                   // System.out.println("arr="+arr);
                    invalidUserResponse = false;
                }
                catch(Exception e) {
                    System.out.println("exception occured is = "+e);
                }
                invalidUserResponse = false;

            }

        }

        return new HashMap<>();
    }

}

