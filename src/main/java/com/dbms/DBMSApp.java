package com.dbms;

import com.dbms.models.User;
import com.dbms.presentation.ConsoleOutput;
import com.dbms.presentation.IConsoleOutput;
import com.dbms.presentation.IReadUserInput;
import com.dbms.presentation.ReadUserInput;
import com.dbms.service.HelloMessageService;
import com.dbms.service.UserAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DBMSApp implements CommandLineRunner {

    @Autowired
    private HelloMessageService helloService;

    @Autowired
    private UserAuthentication userAuth;

    @Autowired
    private IReadUserInput readUserInput;

    @Autowired
    private IConsoleOutput consoleOutput;

    public static void main(String[] args) throws Exception {

        //disabled banner, don't want to see the spring logo
        SpringApplication app = new SpringApplication(DBMSApp.class);
        app.setBannerMode(Banner.Mode.OFF);
        app.run(args);

        //SpringApplication.run(SpringBootConsoleApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        boolean isUserLoggedIn = false;
        String userName = null;
        String password = null;
        User user = null;
        boolean isNewUser = false;
        while(!isUserLoggedIn) {
            while (!isValidInput(userName)) {
                userName = readUserInput.getStringInput("Enter username:");
                if (isValidInput(userName)) {
                    user = userAuth.checkUser(userName);
                } else {
                    consoleOutput.warning("Invalid username");
                }
            }

            if(user == null){
                isNewUser = true;
                consoleOutput.info("User name entered is new. Please enter a password to register.");
            }

            while(!isValidInput(password)) {
                password = readUserInput.getStringInput("Enter password:");
                if (isValidInput(password)) {
                    if (isNewUser) {
                        userAuth.saveUser(userName, password);
                    }
                    isUserLoggedIn = true;
                } else {
                    consoleOutput.warning("Invalid password");
                }
            }
        }
        consoleOutput.info("Logged in successfully -- write new logic here");
    }

    private boolean isValidInput(String input) {
        try {
            if (input.isEmpty()) {
                return false;
            } else {
                return true;
            }
        } catch (Exception e) {
            return false;
        }
    }

}