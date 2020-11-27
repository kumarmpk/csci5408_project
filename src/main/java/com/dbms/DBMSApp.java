package com.dbms;

import com.dbms.models.CompleteDatabase;
import com.dbms.models.User;
import com.dbms.service.CreateLoadDatabase;
import com.dbms.service.QueryExecution;
import com.dbms.service.UserAuthentication;
import org.json.simple.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Map;

@SpringBootApplication
public class DBMSApp implements CommandLineRunner {

    @Autowired
    private UserAuthentication userAuth;

    @Autowired
    private QueryExecution queryExecution;

    public static void main(String[] args) throws Exception {

        //disabled banner, don't want to see the spring logo
        SpringApplication app = new SpringApplication(DBMSApp.class);
        app.setBannerMode(Banner.Mode.OFF);
        app.run(args);

        //SpringApplication.run(SpringBootConsoleApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        while(true) {
            User user = userAuth.userRegisterLogin();
            if (user != null) {
                queryExecution.queryConsole(user);
            }
        }
    }

}