package com.hxsd.services.productLine.edu;

import com.doubeye.commons.application.ConnectionManager;
import com.doubeye.commons.database.connection.ConnectionHelper;
import com.doubeye.commons.utils.collection.CollectionUtils;
import com.doubeye.commons.utils.request.RequestHelper;
import com.hxsd.productLine.studentInfo.edu.StudentInfo;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * Created by doubeye(doubeye@sina.com) on 2016/9/21.
 * 学生信息服务
 */
public class StudentInfoService {
    /**
     * 根据手机号获得学生信息
     * @param parameters 参数对象，需包括
     *                   dataSourceGroup 数据源组标示符
     *                   mobileNumber手机号
     * @return 学生信息数组
     * @throws SQLException SQL异常
     * @throws ClassNotFoundException 未找到类的异常
     */
    public JSONArray getStudentByMobile(Map<String, String[]> parameters) throws Exception {
        Connection mainConn = null;
        Connection logConn =null;
        String dataSourceGroup = RequestHelper.getString(parameters, "dataSourceGroup");
        try {
            mainConn = ConnectionManager.getConnection(dataSourceGroup, ConnectionManager.DATASOURCE_EDU_MAIN);
            logConn = ConnectionManager.getConnection(dataSourceGroup, ConnectionManager.DATASOURCE_LOG);
            JSONArray students = StudentInfo.getStudentidByMobile(mainConn, RequestHelper.getString(parameters, "mobileNumber"));
            for (int i = 0; i < students.size(); i ++) {
                JSONObject student = students.getJSONObject(i);
                StudentInfo studentInfo = new StudentInfo();
                studentInfo.setLogConn(logConn);
                studentInfo.setMainConn(mainConn);
                studentInfo.setStudentId(student.getLong("studentid"));
                JSONObject studentLog = new JSONObject();

                studentLog.put("allocate", getStudentAllocate(studentInfo));
                studentLog.put("autoAssign", getStudentAutoAssign(studentInfo));
                studentLog.put("autoAssignBeSpoke", getStudentAutoAssignBeSpoke(studentInfo));
                studentLog.put("feedback", getStudentFeedback(studentInfo));
                studentLog.put("invalid", getStudentInvalid(studentInfo));
                studentLog.put("discard", getStudentDiscard(studentInfo));
                studentLog.put("enrollment", getStudentEnrollment(studentInfo));
                studentLog.put("useOldStudent", getUseStudent(studentInfo));

                JSONArray recovers = getRecoverStudent(studentInfo);
                generateUserInfoForResult(mainConn, recovers, "ecuserid");
                studentLog.put("recover", recovers);

                JSONArray oldStudentLogs = getOldStudentLog(studentInfo);
                generateUserInfoForResult(mainConn, oldStudentLogs, "userid");
                studentLog.put("oldStudentLog", oldStudentLogs);

                student.put("log", studentLog);
                JSONArray timeLine = generateTimeLine(student);
                student.put("timeLine", timeLine);
            }

            return students;
        } finally {
            ConnectionHelper.close(mainConn);
            ConnectionHelper.close(logConn);
        }
    }

    private static JSONArray getStudentAllocate(StudentInfo studentInfo) throws SQLException, ClassNotFoundException {
        return studentInfo.getStudentAllocate();
    }

    private static JSONArray getStudentAutoAssign( StudentInfo studentInfo) throws SQLException, ClassNotFoundException {
        return studentInfo.getStudentAutoAssign();
    }

    private static JSONArray getStudentAutoAssignBeSpoke(StudentInfo studentInfo) throws SQLException, ClassNotFoundException {
        return studentInfo.getStudentAutoAssignBeSpoke();
    }

    private static JSONArray getStudentFeedback( StudentInfo studentInfo) throws SQLException, ClassNotFoundException {
        return studentInfo.getStudentFeedback();
    }

    private static JSONArray getStudentInvalid(StudentInfo studentInfo) throws SQLException, ClassNotFoundException {
        return studentInfo.getStudentInvalid();
    }

    private static JSONArray getStudentDiscard(StudentInfo studentInfo) throws SQLException, ClassNotFoundException {
        return studentInfo.getStudentDiscard();
    }

    private static JSONArray getStudentEnrollment(StudentInfo studentInfo) throws SQLException, ClassNotFoundException {
        return studentInfo.getStudentEnrollment();
    }

    private static JSONArray getUseStudent(StudentInfo studentInfo) throws SQLException, ClassNotFoundException {
        return studentInfo.getUseOldStudent();
    }

    private static JSONArray getRecoverStudent(StudentInfo studentInfo) throws SQLException, ClassNotFoundException {
        return studentInfo.getRecoverStudent();
    }

    private static JSONArray getOldStudentLog(StudentInfo studentInfo) throws SQLException, ClassNotFoundException {
        return studentInfo.getOldStudentLog();
    }

    private static void generateUserInfoForResult(Connection conn, JSONArray result, String userIdPropertyName) throws SQLException, ClassNotFoundException {
        if (result.size() == 0) {
            return ;
        }
        List<Long> userIds = new ArrayList<>();
        for (int i = 0; i < result.size(); i ++) {
            JSONObject recover = result.getJSONObject(i);
            userIds.add(recover.getLong(userIdPropertyName));
        }
        JSONArray users = StudentInfo.getUserInfoByUserIds(conn, CollectionUtils.split(userIds, ","));
        for (int i = 0; i < result.size(); i ++) {
            JSONObject recover = result.getJSONObject(i);
            long userId = recover.getLong(userIdPropertyName);
            String userInfo = "%s(" + userId + ")";
            String userName = null;
            for (int j = 0; j < users.size(); j ++) {
                JSONObject user = users.getJSONObject(j);
                long userInfoId = user.getLong("userid");
                if (userId == userInfoId) {
                    userName = user.getString("username");
                }
            }
            userName = (userName == null ? "未知" : userName);
            userInfo = String.format(userInfo, userName);
            recover.put("userName", userInfo);
        }
    }

    /**
     * 生成学生的时间线
     * @param student 学生的时间线
     */
    private static JSONArray generateTimeLine(JSONObject student) {
        JSONArray timeLine = new JSONArray();
        student.put("timeLine", timeLine);

        //所有的类型
        List<JSONArray> allTypes = new ArrayList<>();
        List<Integer> allIndexes = new ArrayList<>();

        //建表信息
        JSONObject create = new JSONObject();
        create.put("infoTypeName", "建表");
        create.put("infoType", "created");
        create.put("dateline", student.getString("dateline"));

        timeLine.add(create);
        List<String> infoTypeNames = new ArrayList<>();
        generateAllTypes(allTypes, allIndexes, student, infoTypeNames);
        return generateOrder(allTypes, allIndexes, infoTypeNames);
    }

    private static void generateAllTypes(List<JSONArray> allTypes, List<Integer> allIndexes, JSONObject student, List<String> infoTypeNames) {
        JSONObject studentLog = student.getJSONObject("log");

        //分配
        JSONArray allocate = studentLog.getJSONArray("allocate");
        allTypes.add(allocate);
        allIndexes.add(0);
        infoTypeNames.add("allocate");
        //分配历史表
        JSONArray autoAssign = studentLog.getJSONArray("autoAssign");
        allTypes.add(autoAssign);
        allIndexes.add(0);
        infoTypeNames.add("autoAssign");
        //预约分配
        JSONArray autoAssignBeSpoke = studentLog.getJSONArray("autoAssignBeSpoke");
        allTypes.add(autoAssignBeSpoke);
        allIndexes.add(0);
        infoTypeNames.add("autoAssignBeSpoke");
        //反馈
        JSONArray feedback = studentLog.getJSONArray("feedback");
        allTypes.add(feedback);
        allIndexes.add(0);
        infoTypeNames.add("feedback");
        //无效
        JSONArray invalid = studentLog.getJSONArray("invalid");
        allTypes.add(invalid);
        allIndexes.add(0);
        infoTypeNames.add("invalid");
        //废弃
        JSONArray discard = studentLog.getJSONArray("discard");
        allTypes.add(discard);
        allIndexes.add(0);
        infoTypeNames.add("discard");
        //报名
        JSONArray enrollment = studentLog.getJSONArray("enrollment");
        allTypes.add(enrollment);
        allIndexes.add(0);
        infoTypeNames.add("enrollment");
        //使用旧量
        JSONArray useOldStudent = studentLog.getJSONArray("useOldStudent");
        allTypes.add(useOldStudent);
        allIndexes.add(0);
        infoTypeNames.add("useOldStudent");
        //回收日志表
        JSONArray recover = studentLog.getJSONArray("recover");
        allTypes.add(recover);
        allIndexes.add(0);
        infoTypeNames.add("recover");
        //离职人员回收日志
        JSONArray oldStudentLog = studentLog.getJSONArray("oldStudentLog");
        allTypes.add(oldStudentLog);
        allIndexes.add(0);
        infoTypeNames.add("oldStudentLog");
    }


    /**
     * 对学生的所有信息排序
     *
     * @param allTypes 学生的所有信息类型
     * @param allIndexes 每个类型的当前判断索引
     * @param infoTypeNames 每个类型对应的标识
     * @return 排序要的学生信息
     */

    private static JSONArray generateOrder(List<JSONArray> allTypes, List<Integer> allIndexes, List<String> infoTypeNames) {
        JSONArray result = new JSONArray();
        // 每个类型的最大值数组
        List<String> maxOfTypes = new ArrayList<>();
        // 已处理的类型个数
        int processed = 0;
        //生成初始的类型
        for (int i = 0; i < allIndexes.size(); i ++) {
            // System.out.print(infoTypeNames.get(i) + "   ");
            if (allTypes.get(i).size() > 0){
                maxOfTypes.add(allTypes.get(i).getJSONObject(0).getString("dateline"));
            } else {
                //如果该类型没有内容，设置为空
                maxOfTypes.add(null);
                processed ++;
            }
        }

        // System.out.println();

        //防止死循环
        int whileHacker = 0;
        while (processed < allTypes.size()) {
            String maxDateLine = "0000-00-00 00:00:00";
            int maxTypeIndex = -1;
            //找到最大值，及最大值的类型
            for (int i = 0; i < maxOfTypes.size(); i ++) {
                String currentTypeMax = maxOfTypes.get(i);
                if (currentTypeMax != null && (currentTypeMax.compareTo(maxDateLine) > 0)) {
                    maxDateLine = currentTypeMax;
                    maxTypeIndex = i;
                }
            }
            // System.out.println(maxDateLine + "(" + maxTypeIndex + "  " + infoTypeNames.get(maxTypeIndex) + ")" + maxOfTypes.toString() + "   " + allIndexes.toString());
            //当前类型的index
            int currentMaxTypeIndex = allIndexes.get(maxTypeIndex);
            //将当前类型的最大值 +１
            allIndexes.set(maxTypeIndex, currentMaxTypeIndex + 1);
            //如果是该类型的最后一个值，则置空
            if (currentMaxTypeIndex == (allTypes.get(maxTypeIndex).size() - 1)) {
                maxOfTypes.set(maxTypeIndex, null);
                processed ++;
            } else {
                maxOfTypes.set(maxTypeIndex, allTypes.get(maxTypeIndex).getJSONObject(currentMaxTypeIndex + 1).getString("dateline"));
            }
            JSONObject currentMax = new JSONObject();
            JSONArray maxArray = allTypes.get(maxTypeIndex);
            JSONObject maxObj = maxArray.getJSONObject(currentMaxTypeIndex);
            currentMax.put("infoTypeName", maxObj.getString("infoType"));
            currentMax.put("infoType", infoTypeNames.get(maxTypeIndex));
            currentMax.put("index", currentMaxTypeIndex);
            currentMax.put("typeIndex", maxTypeIndex);
            currentMax.put("dateLineInTimeLine", maxDateLine);
            result.add(currentMax);
            // System.out.println(currentMax);
            whileHacker ++;
            if (whileHacker > 10000) {
                throw new RuntimeException("catch a dead while loop");
            }
        }

        return result;
    }
    /**
    public static void main(String[] args) throws SQLException, ClassNotFoundException {

        Map<String, String[]> parameters = new HashMap<>();
        parameters.put("mobileNumber", new String[]{"13548554409"});
        StudentInfoService sis = new StudentInfoService();
        sis.getStudentByMobile(parameters);
    }
     */
}