package com.dbms.presentation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Scanner;

@Component
public class ReadUserInput implements IReadUserInput {

    private Scanner scanner;

    @Autowired
    private IConsoleOutput consoleOutput;

    public ReadUserInput(){
        scanner = new Scanner(System.in);
    }

    @Override
    public String getStringInput(String input){
        consoleOutput.printMsgToConsole(input);
        return scanner.nextLine();
    }

    @Override
    public int getIntInput(String input){
        consoleOutput.printMsgToConsole(input);
        return scanner.nextInt();
    }

}
