package com.dbms;

import com.dbms.models.User;
import com.dbms.presentation.IReadUserInput;
import com.dbms.service.HelloMessageService;
import com.dbms.service.UserLoginRegister;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Date;

import static java.lang.System.exit;

@SpringBootApplication
public class SpringBootConsoleApplication implements CommandLineRunner {

    @Autowired
    private HelloMessageService helloService;

    @Autowired
    private UserLoginRegister userLoginRegister;

    @Autowired
    private IReadUserInput readUserInput;

    public static void main(String[] args) throws Exception {

        //disabled banner, don't want to see the spring logo
        SpringApplication app = new SpringApplication(SpringBootConsoleApplication.class);
        app.setBannerMode(Banner.Mode.OFF);
        app.run(args);

        //SpringApplication.run(SpringBootConsoleApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

        String userName = readUserInput.getStringInput("Please enter the userName to login.");
        User user = userLoginRegister.checkUser(userName);
        if(user == null){
            String password = readUserInput.getStringInput("UserName is new. Please enter the password to register.");
            userLoginRegister.saveUser(userName, password);
        }


    }


}