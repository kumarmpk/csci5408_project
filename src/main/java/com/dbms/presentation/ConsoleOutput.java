package com.dbms.presentation;

import org.springframework.stereotype.Component;
import java.util.logging.Logger;

@Component
public class ConsoleOutput implements IConsoleOutput{

    Logger logger = new DBMSLogger(ConsoleOutput.class.getName()).logger;

    @Override
    public void info(String text) {
        logger.info(text);
    }

    @Override
    public void warning(String text) {
        logger.warning(text);
    }

    @Override
    public void error(String text) {
        logger.severe(text);
    }
}
