package com.data.service;

import org.springframework.stereotype.Service;

import java.util.Scanner;

@Service
public class HelloMessageService {

    private String name;

    public String getMessage() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Type a name");
        name = scanner.next();
        return getMessage(name);
    }

    public String getMessage(String name) {
        return "Hello " + name;
    }

}
