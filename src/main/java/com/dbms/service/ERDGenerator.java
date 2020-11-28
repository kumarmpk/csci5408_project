package com.dbms.service;

import com.dbms.datasource.Resource;
import com.dbms.presentation.ConsoleOutput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@Component
public class ERDGenerator {

    @Autowired
    private Resource resource;

    @Autowired
    private ConsoleOutput consoleOutput;

    public ERDGenerator(){
        consoleOutput = new ConsoleOutput();
    }

    public void generateERD(String dbName){
        String path = resource.dbPath+dbName;
        //String path = "C:\\Users\\prath\\MPK\\studies\\Term3\\CSCI-5408-Data\\project\\Code\\erdsampletrial";
        generateDotFile(path);
        generateImgFile(path, dbName);
    }

    public void generateDotFile(String path) {
        path = "\""+path+"\"";
        String anyCommand="erdot metaData.json";
        try {
            ProcessBuilder builder = new ProcessBuilder(
                    "cmd.exe", "/c", "cd "+path+" && "+anyCommand);
            builder.redirectErrorStream(true);
            Process p = builder.start();
            BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            while (true) {
                line = r.readLine();
                if (line == null) { break; }
                consoleOutput.print(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void generateImgFile(String path, String dbName) {
        path = "\""+path+"\"";
        String anyCommand="dot metaData.dot -Tpng -o "+dbName+".png";
        try {
            ProcessBuilder builder = new ProcessBuilder(
                    "cmd.exe", "/c", "cd "+path+" && "+anyCommand);
            builder.redirectErrorStream(true);
            Process p = builder.start();
            BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            while (true) {
                line = r.readLine();
                if (line == null) { break; }
                consoleOutput.print(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
