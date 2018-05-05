package com.hxsd.services.productLine.e;

import com.doubeye.commons.application.ConnectionManager;
import com.doubeye.commons.application.GlobalApplicationContext;
import com.doubeye.commons.database.connection.ConnectionHelper;
import com.doubeye.commons.database.sql.SQLExecutor;
import com.doubeye.commons.utils.DateTimeUtils.DateTimeUtils;
import com.doubeye.commons.utils.request.RequestHelper;
import com.doubeye.commons.utils.response.ResponseHelper;
import com.doubeye.commons.utils.test.ApplicationContextInitiator;
import com.hxsd.productLine.studentInfo.e.student.StudentInfo;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.omg.PortableInterceptor.INACTIVE;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.Map;


/**
 * Created by doubeye(doubeye@sina.com) on 2016/12/16.
 */
public class StudentService {
    public JSONObject resetSecondMobile(Map<String, String[]> parameters) throws Exception {
        Connection conn = null;
        try {
            String dataSource = RequestHelper.getString(parameters, "dataSource");
            int studentId = RequestHelper.getInt(parameters, "studentId");
            String encrytKey = GlobalApplicationContext.getInstance().getStringParameter("dataSourceKey");
            conn = ConnectionManager.getConnection(dataSource, encrytKey);
            StudentInfo studentInfo = new StudentInfo();
            studentInfo.setConn(conn);
            studentInfo.setStudentId(studentId);
            studentInfo.deleteSecondMobile();
            JSONObject obj = new JSONObject();
            obj.put("SUCCESS", true);
            return obj;
        } finally {
            ConnectionHelper.close(conn);
        }
    }

    public JSONObject resetSecondQQ(Map<String, String[]> parameters) throws Exception {
        Connection conn = null;
        try {
            String dataSource = RequestHelper.getString(parameters, "dataSource");
            int studentId = RequestHelper.getInt(parameters, "studentId");
            String encrytKey = GlobalApplicationContext.getInstance().getStringParameter("dataSourceKey");
            conn = ConnectionManager.getConnection(dataSource, encrytKey);
            StudentInfo studentInfo = new StudentInfo();
            studentInfo.setConn(conn);
            studentInfo.setStudentId(studentId);
            studentInfo.deleteSecondQQ();
            JSONObject obj = new JSONObject();
            obj.put("SUCCESS", true);
            return obj;
        } finally {
            ConnectionHelper.close(conn);
        }
    }

    public JSONObject resetBoth(Map<String, String[]> parameters) throws Exception {
        Connection conn = null;
        try {
            String dataSource = RequestHelper.getString(parameters, "dataSource");
            int studentId = RequestHelper.getInt(parameters, "studentId");
            String encrytKey = GlobalApplicationContext.getInstance().getStringParameter("dataSourceKey");
            conn = ConnectionManager.getConnection(dataSource, encrytKey);
            StudentInfo studentInfo = new StudentInfo();
            studentInfo.setConn(conn);
            studentInfo.setStudentId(studentId);
            studentInfo.deleteSecondMobile();
            studentInfo.deleteSecondQQ();
            return ResponseHelper.getSuccessObject();
        } finally {
            ConnectionHelper.close(conn);
        }
    }

    public JSONArray getStudent(Map<String, String[]> parameters) throws Exception {
        Connection conn = null;
        try {
            String dataSource = RequestHelper.getString(parameters, "dataSource");
            String student = RequestHelper.getString(parameters, "student");
            String encrytKey = GlobalApplicationContext.getInstance().getStringParameter("dataSourceKey");
            conn = ConnectionManager.getConnection(dataSource, encrytKey);
            StudentInfo studentInfo = new StudentInfo();
            studentInfo.setConn(conn);
            return studentInfo.getStudent(student);
        } finally {
            ConnectionHelper.close(conn);
        }
    }

    public JSONObject assignStudentToChannel(Map<String, String[]> parameters) throws Exception {
        Connection conn = null;

        try (Connection coreConnection = GlobalApplicationContext.getInstance().getCoreConnection()) {
            String dataSource = RequestHelper.getString(parameters, "dataSource");
            /*
            if (dataSource.equals("E-PRODUCT")) {
                throw new RuntimeException("测试功能，请不要用在生产环境");
            }
            */
            String encrytKey = GlobalApplicationContext.getInstance().getStringParameter("dataSourceKey");
            conn = ConnectionManager.getConnection(dataSource, encrytKey);


            String studentId = RequestHelper.getString(parameters, "studentid");

            String channelUserId = RequestHelper.getString(parameters, "channelUserId");
            String channelManagerId = RequestHelper.getString(parameters, "channelManagerId");
            StudentInfo studentInfo = new StudentInfo();
            studentInfo.setConn(conn);

            JSONArray students = studentInfo.getStudent(studentId);
            String adaId = "";
            if (students.size() > 0) {
                JSONObject student = students.getJSONObject(0);
                adaId = student.getString("userid");
                if (student.getString("isInChannel").equals("渠道学生")) {
                    throw new RuntimeException("学生已经属于渠道，无需指定");
                }
                if (student.getString("oldstudent").equals("是")) {
                    throw new RuntimeException("不支持对旧量的指定");
                }
            } else {
                throw new RuntimeException("没有找到学生");
            }

            studentInfo.assignStudentToChannel(studentId, channelManagerId, channelUserId);
            //记录日志
            String userId = RequestHelper.getString(parameters, "_userId");
            SQLExecutor.execute(coreConnection, String.format(SQL_INSERT_LOG, userId, DateTimeUtils.getCurrentTime(), dataSource, studentId, channelUserId, adaId, channelManagerId));
            return ResponseHelper.getSuccessObject();
        } finally {
            ConnectionHelper.close(conn);
        }

    }


    public JSONObject unassignStudentToChannel(Map<String, String[]> parameters) throws Exception {
        String dataSource = RequestHelper.getString(parameters, "dataSource");
        String encrytKey = GlobalApplicationContext.getInstance().getStringParameter("dataSourceKey");
        try (Connection coreConnection = GlobalApplicationContext.getInstance().getCoreConnection();
            Connection conn = ConnectionManager.getConnection(dataSource, encrytKey);) {

            String studentId = RequestHelper.getString(parameters, "studentid");
            StudentInfo studentInfo = new StudentInfo();
            studentInfo.setConn(conn);

            //获得t_channel_student表中学生的信息
            String records = studentInfo.getChannelStudentByStudentId(studentId);
            //清除标记
            studentInfo.unChannel(studentId);

            //记录日志
            String userId = RequestHelper.getString(parameters, "_userId");
            SQLExecutor.execute(coreConnection, String.format(SQL_INSERT_LOG_UNCHANNEL, userId, DateTimeUtils.getCurrentTime(), dataSource, studentId, records));
            return ResponseHelper.getSuccessObject();
        }

    }

    private static final String SQL_INSERT_LOG = "INSERT INTO to_channel_log(userId, operate_time, datasource, studentid, target_user_id, origin_user_id, manager_id) VALUES (" +
            "'%s', '%s', '%s', %s, %s, %s, %s)";
    private static final String SQL_INSERT_LOG_UNCHANNEL = "INSERT INTO to_channel_log(userId, operate_time, datasource, studentid, unchannel) VALUES (" +
            "'%s', '%s', '%s', %s, '%s')";
}
