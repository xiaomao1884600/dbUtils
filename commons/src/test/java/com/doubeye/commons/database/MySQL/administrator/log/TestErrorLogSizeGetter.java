package com.doubeye.commons.database.MySQL.administrator.log;

import com.doubeye.commons.application.GlobalApplicationContext;
import com.doubeye.commons.utils.test.ApplicationContextInitiator;
import junit.framework.TestCase;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

public class TestErrorLogSizeGetter extends TestCase{
    public void testLogErrorLogFileSize() throws SQLException {
        ApplicationContextInitiator.init();
        ErrorLogSizeGetter getter = new ErrorLogSizeGetter();
        try (Connection coreConnection = GlobalApplicationContext.getInstance().getCoreConnection()) {
            getter.setConn(coreConnection);
            long errorLogFileSize = getter.getErrorLogSize();
            System.out.println(errorLogFileSize);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
