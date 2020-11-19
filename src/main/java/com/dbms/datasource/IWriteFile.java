package com.dbms.datasource;

import java.io.IOException;

public interface IWriteFile {

    void writeFile(String content, String filePath) throws IOException;

}
