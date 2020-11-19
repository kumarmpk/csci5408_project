package com.dbms.datasource;

import com.dbms.DBMSApp;
import com.dbms.presentation.ConsoleOutput;
import com.dbms.presentation.IConsoleOutput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;

@Component
public class WriteFile implements IWriteFile{

    @Autowired
    private IConsoleOutput consoleOutput;

    @Autowired
    private Resource resource;

    public void writeFile(String content, String filePath) throws IOException {
        FileWriter fileWriter = null;
        try {
            filePath = resource.dbPath + filePath;
            fileWriter = new FileWriter(filePath);
            fileWriter.write(content);
        } catch (IOException e){
            consoleOutput.error("WriteFile: writeFile: IOException: "+e);
            throw e;
        } finally {
            if(fileWriter != null) {
                fileWriter.flush();
                fileWriter.close();
            }
        }

    }


}
