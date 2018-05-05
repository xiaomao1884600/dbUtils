package com.hxsd.tempNeeds;

import com.doubeye.commons.application.GlobalApplicationContext;
import com.doubeye.commons.database.sql.SQLExecutor;
import com.doubeye.commons.utils.test.ApplicationContextInitiator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by doubeye(doubeye@sina.com) on 2017/1/20.
 */
public class AttendenceAudit {
    private static final String FILE_PATH_DK = "D:\\EXPORT\\DK\\";
    private static final String FILE_PATH_XJ_2015 = "D:\\EXPORT\\XJ2015\\";
    private static final String FILE_PATH_XJ_2016 = "D:\\EXPORT\\XJ2016\\";
    private static final String FILE_PATH_BC = "D:\\EXPORT\\BC\\";
    private static final String FILE_PATH_19 = "D:\\EXPORT\\19\\";
    private static final String FILE_PATH_20 = "D:\\EXPORT\\20\\";
    private static final String SQL_SELECT_ATTENDANCE = "select studentid, studentname, clazzid, IFNULL(signin, ' ') signin, IFNULL(signout, ' ') signout from ";
    private static final String SQL_SELECT_CLASS = "select title from clazz where clazzid = ";

    public static void main(String[] args) throws SQLException, IOException {
        /*
        File file = new File("D:\\workcode\\dbUtils\\web\\config\\init.json");
        JSONObject initConfig = JSONUtils.getJsonObjectFromFile(file);
        GlobalApplicationContext.init(initConfig);
        */
        ApplicationContextInitiator.init();
        String tableName = "abb_20";
        Connection conn = GlobalApplicationContext.getInstance().getCoreConnection();
        ResultSet rs = SQLExecutor.executeQuery(conn, SQL_SELECT_ATTENDANCE + tableName);
        int studentId = -1;
        int classId = -1;
        String classTitle = "";
        FileOutputStream fileOutputStream = null;
        try {
            while (rs.next()) {
                if (studentId != rs.getInt("studentid") || classId != rs.getInt("clazzid")) {
                    if (fileOutputStream != null) {
                        fileOutputStream.flush();
                        fileOutputStream.close();
                    }
                    studentId = rs.getInt("studentid");
                    classId = rs.getInt("clazzid");
                    classTitle = getClazzName(conn, classId);
                    String fileName = FILE_PATH_20 + studentId + "_" + classId + "_" + classTitle + ".csv";
                    File output = new File(fileName);
                    fileOutputStream = new FileOutputStream(output);
                    System.out.println(fileName);
                }
                String line = rs.getString("studentid") + "," + rs.getString("studentname") + "," + rs.getString("clazzid") + "," + classTitle + "," + rs.getString("signin") + "," + rs.getString("signout");
                fileOutputStream.write((line + "\r\n").getBytes("gb2312"));
            }

        } finally {
            if (fileOutputStream != null) {
                fileOutputStream.flush();
                fileOutputStream.close();
            }
        }
    }

    private static String getClazzName(Connection conn, int classId) throws SQLException {
        ResultSet rs = SQLExecutor.executeQuery(conn, SQL_SELECT_CLASS + classId);
        if (rs.next()) {
            String classTitle = rs.getString("title").replace("/", "-");
            rs.close();
            return classTitle;
        }
        return "未知";
    }
}
