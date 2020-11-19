package com.dbms.presentation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Scanner;
import java.util.logging.Logger;

@Component
public class ReadUserInput implements IReadUserInput {

    private Scanner scanner;

    Logger logger = new DBMSLogger(ConsoleOutput.class.getName()).logger;

    public ReadUserInput(){
        scanner = new Scanner(System.in);
    }

    @Override
    public String getStringInput(String input){
        logger.info(input);
        return scanner.nextLine();
    }

    @Override
    public int getIntInput(String input){
        logger.info(input);
        return scanner.nextInt();
    }

}
