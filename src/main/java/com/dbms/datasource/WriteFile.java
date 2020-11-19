package com.dbms.datasource;

import com.dbms.presentation.ConsoleOutput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.file.Paths;

@Component
public class WriteFile implements IWriteFile{

    @Autowired
    private ConsoleOutput consoleOutput;

    @Autowired
    private Resource resource;

    public void writeFile(String content, String filePath) throws IOException {
        FileWriter fileWriter = null;
        try {
            filePath = resource.dbPath + filePath;
            fileWriter = new FileWriter(filePath);
            fileWriter.write(content);
        } catch (IOException e){
            consoleOutput.printMsgToConsole("WriteFile: writeFile: IOException: "+e);
            throw e;
        } finally {
            if(fileWriter != null) {
                fileWriter.flush();
                fileWriter.close();
            }
        }

    }


}
