package com.doubeye.commons.database.ResultSet.ResultSetWarpper;

import com.doubeye.commons.database.sql.SQLExecutor;
import com.doubeye.commons.utils.constant.CommonConstant;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author doubeye
 * 结果集的CSV格式包裹器，将结果包裹为StringBuffer
 */
@SuppressWarnings("WeakerAccess")
public class ResultSetCsvWrapper {
    public static List<String> getCsvFromResultSet(ResultSet resultSet) throws SQLException {
        List<String> result = new ArrayList<>();
        int columnCount = resultSet.getMetaData().getColumnCount();
        while (resultSet.next()) {
            StringBuilder line = new StringBuilder();
            for (int i = 1; i <= columnCount; i ++) {
                line.append(resultSet.getString(i).replace(CommonConstant.SEPARATOR.COMMA.toString(), "，").replace("\n", "").replace("\r", ""));
                if (i < columnCount) {
                    line.append(CommonConstant.SEPARATOR.COMMA);
                }
            }
            result.add(line.toString());
        }
        return result;
    }

    public static List<String> getCsvFromSQL(Connection conn, String sql) throws SQLException {
        try (ResultSet rs = SQLExecutor.executeQuery(conn, sql)){
            return ResultSetCsvWrapper.getCsvFromResultSet(rs);
        }
    }
}
