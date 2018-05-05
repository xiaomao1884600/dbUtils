package com.hxsd;

import com.doubeye.commons.application.ConnectionManager;
import com.doubeye.commons.database.connection.ConnectionHelper;
import com.doubeye.commons.database.sql.SQLExecutor;
import com.doubeye.commons.utils.test.ApplicationContextInitiator;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

class ResultBean {
    private String columnName;

    public long getRowCount() {
        return rowCount;
    }

    public void setRowCount(long rowCount) {
        this.rowCount = rowCount;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    private long rowCount;

    public String toString() {
        return columnName + "~" + rowCount;
    }

}

/**
 * Created by zhanglu1782 on 2016/5/30.
 */
public class GetStudentAttrCompletion {
    private static final String SQL_STUDENT_FIELD = "select concat('select \"', column_name, '\" column_name ,count(*) cnt FROM student WHERE (', column_name, if(data_type = 'varchar' or data_type = 'char', concat(\" = '' or \" , column_name, ' is null) '), ' = 0)')) statement from information_schema.columns where table_schema = 'hxsdedusystem' and table_name = 'STUDENT'";
    private static final String SQL_ENROLLED_STUDENT_FIELD = "select concat('select \"', column_name, '\" column_name ,count(*) cnt FROM student WHERE student.deleted = 0 AND student.invalid = 0 AND student.`discard` = 0 AND student.enrollmentid > 0 AND student.ecuserid > 0 AND (', column_name, if(data_type = 'varchar' or data_type = 'char', concat(\" = '' or \" , column_name, ' is null) '), ' = 0)')) statement from information_schema.columns where table_schema = 'hxsdedusystem' and table_name = 'STUDENT'";
    private static final String SQL_STUDENT_FIELD_2016 = "select concat('select \"', column_name, '\" column_name ,count(*) cnt FROM student WHERE dateline > unix_timestamp(\"2016-01-01 00:00:00\") AND (', column_name, if(data_type = 'varchar' or data_type = 'char', concat(\" = '' or \" , column_name, ' is null) '), ' = 0)')) statement from information_schema.columns where table_schema = 'hxsdedusystem' and table_name = 'STUDENT'";
    private static final String SQL_ENROLLED_STUDENT_FIELD_2016 = "select concat('select \"', column_name, '\" column_name ,count(*) cnt FROM student WHERE dateline > unix_timestamp(\"2016-01-01 00:00:00\") AND student.deleted = 0 AND student.invalid = 0 AND student.`discard` = 0 AND student.enrollmentid > 0 AND student.ecuserid > 0 AND (', column_name, if(data_type = 'varchar' or data_type = 'char', concat(\" = '' or \" , column_name, ' is null) '), ' = 0)')) statement from information_schema.columns where table_schema = 'hxsdedusystem' and table_name = 'STUDENT'";
    private static final String SQL_GET_ALL_STUDENT_COUNT = "SELECT COUNT(*) cnt FROM student";
    private Connection conn;
    public void init() throws Exception {
        conn = ConnectionManager.getConnectionByDatasourceId(0, "");
    }

    public List<ResultBean> comput() throws SQLException {
        List<ResultBean> result = new ArrayList<ResultBean>();
        ResultSet rs = SQLExecutor.executeQuery(conn, SQL_STUDENT_FIELD_2016);
        while (rs.next()) {

            String sql = rs.getString("statement");
            //System.out.println(sql);

            ResultSet cntRs = SQLExecutor.executeQuery(conn, sql);

            if (cntRs.next()) {
                long cnt = 0;
                String columnName = cntRs.getString("column_name");
                cnt = cntRs.getLong("cnt");
                ResultBean bean = new ResultBean();
                bean.setColumnName(columnName);
                bean.setRowCount(cnt);
                result.add(bean);
                ConnectionHelper.close(cntRs);
            }

        }
        ConnectionHelper.close(rs);
        return result;
    }

    public void destroy() {
        ConnectionHelper.close(conn);
    }

    public static void main(String[] args) throws Exception {
        ApplicationContextInitiator.init();
        GetStudentAttrCompletion obj = new GetStudentAttrCompletion();
        obj.init();
        List<ResultBean> result = obj.comput();
        obj.destroy();
        System.out.println(result);
    }
}
