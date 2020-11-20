package com.dbms;

import com.dbms.models.User;
import com.dbms.service.CreateLoadDatabase;
import com.dbms.service.UserAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DBMSApp implements CommandLineRunner {

    @Autowired
    private UserAuthentication userAuth;

    @Autowired
    private CreateLoadDatabase createLoadDatabase;

    public static void main(String[] args) throws Exception {

        //disabled banner, don't want to see the spring logo
        SpringApplication app = new SpringApplication(DBMSApp.class);
        app.setBannerMode(Banner.Mode.OFF);
        app.run(args);

        //SpringApplication.run(SpringBootConsoleApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        User user = userAuth.userRegisterLogin();
        createLoadDatabase.createLoadDatabase(user);
    }

}