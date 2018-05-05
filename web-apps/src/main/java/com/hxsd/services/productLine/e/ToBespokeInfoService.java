package com.hxsd.services.productLine.e;

import com.doubeye.commons.application.ConnectionManager;
import com.doubeye.commons.application.GlobalApplicationContext;
import com.doubeye.commons.database.connection.ConnectionHelper;
import com.doubeye.commons.utils.request.RequestHelper;
import com.hxsd.productLine.studentInfo.e.toBespokeInfo.ToBespokeInfo;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;


/**
 * Created by doubeye(doubeye@sina.com) on 2016/10/26.
 * 预约通道相关信息服务
 */
@SuppressWarnings("unused")
public class ToBespokeInfoService {
    private static Logger logger = LogManager.getLogger(ToBespokeInfoService.class);
    /**
     * 获得所有预约通道的学生信息
     * @param parameters 参数，包括以下内容
     *                   dataSource 数据源标示符
     * @return 所有预约通道中的学生信息
     * @throws SQLException SQL异常
     */
    public JSONArray getAllToBespokeInfo(Map<String, String[]> parameters) throws Exception {
        Connection conn = null;
        String dataSource = RequestHelper.getString(parameters, "dataSource");
        try {
            String encrytKey = GlobalApplicationContext.getInstance().getStringParameter("dataSourceKey");
            conn = ConnectionManager.getConnection(dataSource, encrytKey);
            ToBespokeInfo toBespokeInfo = new ToBespokeInfo();
            toBespokeInfo.setConn(conn);
            //根据周萃萃的需求，要求学生在id重复时，只取最后一条
            JSONArray beforeDistinct = toBespokeInfo.getStudentInToBespoke();
            JSONArray result = new JSONArray();
            String studentId = "";
            for (int i = 0; i < beforeDistinct.size(); i ++) {
                JSONObject student = beforeDistinct.getJSONObject(i);
                if (!studentId.equals(student.getString("studentid"))) {
                    result.add(student);
                    studentId = student.getString("studentid");
                }
            }
            return result;
        } finally {
            ConnectionHelper.close(conn);
        }
    }

    /**
     *
     * @param parameters 参数，包括以下内容
     *                   dataSource 数据源标示符
     *                   campusId 校区id
     * @return 根据校区获得负责人的信息
     * @throws SQLException SQL异常
     */
    public JSONArray getECUsersInfoByCampusId(Map<String, String[]> parameters) throws Exception {
        Connection conn = null;
        String dataSource = RequestHelper.getString(parameters, "dataSource");
        try {
            String encryptKey = GlobalApplicationContext.getInstance().getStringParameter("dataSourceKey");
            conn = ConnectionManager.getConnection(dataSource, encryptKey);
            ToBespokeInfo toBespokeInfo = new ToBespokeInfo();
            String campusId = RequestHelper.getString(parameters, "campusId");
            toBespokeInfo.setConn(conn);
            return toBespokeInfo.getECUsersBy(campusId);
        } finally {
            ConnectionHelper.close(conn);
        }
    }

    public JSONArray getAllCampuses (Map<String, String[]> parameters) throws Exception {
        Connection conn = null;
        String dataSource = RequestHelper.getString(parameters, "dataSource");
        try {
            String encryptKey = GlobalApplicationContext.getInstance().getStringParameter("dataSourceKey");
            conn = ConnectionManager.getConnection(dataSource, encryptKey);
            ToBespokeInfo toBespokeInfo = new ToBespokeInfo();
            toBespokeInfo.setConn(conn);
            return toBespokeInfo.getAllCampuses();
        } finally {
            ConnectionHelper.close(conn);
        }
    }

    public JSONArray getAllocatedStudents (Map<String, String[]> parameters) throws Exception {
        Connection conn = null;
        String dataSource = RequestHelper.getString(parameters, "dataSource");
        String ecuserId = RequestHelper.getString(parameters, "ecuserId");
        try {
            String encrytKey = GlobalApplicationContext.getInstance().getStringParameter("dataSourceKey");
            conn = ConnectionManager.getConnection(dataSource, encrytKey);
            ToBespokeInfo toBespokeInfo = new ToBespokeInfo();
            toBespokeInfo.setConn(conn);
            return toBespokeInfo.getAllocatedStudents(ecuserId);
        } finally {
            ConnectionHelper.close(conn);
        }
    }

    public JSONArray getAllNoReplayedStudent(Map<String, String[]> parameters) throws Exception {
        Connection conn = null;
        String dataSource = RequestHelper.getString(parameters, "dataSource");
        String ecuserId = RequestHelper.getString(parameters, "ecuserId");
        try {
            String encrytKey = GlobalApplicationContext.getInstance().getStringParameter("dataSourceKey");
            conn = ConnectionManager.getConnection(dataSource, encrytKey);
            ToBespokeInfo toBespokeInfo = new ToBespokeInfo();
            toBespokeInfo.setConn(conn);
            return toBespokeInfo.getAllNoReplayedStudent(ecuserId);
        } finally {
            ConnectionHelper.close(conn);
        }
    }
}
