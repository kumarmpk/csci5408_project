package com.dbms.presentation;

import org.springframework.stereotype.Component;

@Component
public class ConsoleOutput implements IConsoleOutput{

    private void printMsg(String input){
        System.out.println(input);
    }

    @Override
    public void printMsgToConsole(String input){
        printMsg(input);
    }

}
