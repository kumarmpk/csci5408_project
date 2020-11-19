package com.dbms.presentation;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.*;

public class DBMSLogger {

    Logger logger;

    DBMSLogger(String className) {
        logger = java.util.logging.Logger.getLogger(className);
        ConsoleHandler consoleHandler = new ConsoleHandler();
        for (Handler handler : logger.getHandlers()) {  logger.removeHandler(handler); }
        logger.addHandler(consoleHandler);
        logger.setLevel(Level.INFO);
        consoleHandler.setFormatter(new Formatter() {
            @Override
            public String format(LogRecord record) {
                DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
//                String filePathArr = record.getSourceClassName();
//                String fileName = filePathArr.substring(filePathArr.lastIndexOf('.') + 1);
                return String.format("[%-15s] [%s] %s\n",
                        dateFormat.format(new Date(record.getMillis())),
                        record.getLevel(),
                        record.getMessage());
            }
        });
    }
}
