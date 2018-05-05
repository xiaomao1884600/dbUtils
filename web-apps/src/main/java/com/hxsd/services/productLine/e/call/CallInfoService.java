package com.hxsd.services.productLine.e.call;

import com.doubeye.commons.application.ConnectionManager;
import com.doubeye.commons.application.GlobalApplicationContext;
import com.doubeye.commons.database.ResultSet.ResultSetWarpper.ResultSetJSONWrapper;
import com.doubeye.commons.utils.request.RequestHelper;
import net.sf.json.JSONArray;

import java.sql.Connection;
import java.util.Map;

/**
 * @author doubeye
 * 分析制定电话拨打的服务
 */
public class CallInfoService {
    /**
     * 获得指定电话的分析结果
     * @param parameters 参数，包括以下参数
     *                   mobile {String} 要分析的号码
     * @return 分析结果
     */
    public JSONArray getCallInfo(Map<String, String[]> parameters) throws Exception {
        String mobile = RequestHelper.getString(parameters, "mobile");
        String encryptKey = GlobalApplicationContext.getInstance().getStringParameter("dataSourceKey");
        try (Connection conn = ConnectionManager.getConnection("RECORD-ANALYZE-PRODUCT", encryptKey)) {
            return ResultSetJSONWrapper.getJSONArrayFromSQL(conn, String.format(SQL_SELECT_CALL_INFO, mobile));
        }
    }
    private static final String SQL_SELECT_CALL_INFO = "SELECT\n" +
            "\tusername `userName`,\n" +
            "\tCASE calltype WHEN 1 THEN '拨入' WHEN 2 THEN '拨出' ELSE calltype END `callType`, IF(connected = 1, '接通', '未接通') `connected`, count(*) cnt\n" +
            "FROM\n" +
            "\t(\n" +
            "\t\tSELECT\n" +
            "\t\t\tusername, calltype, (billable > 0) `connected`\n" +
            "\t\tFROM\n" +
            "\t\t\trecord_info\n" +
            "\t\tWHERE\n" +
            "\t\t\tstudent_mobile = '%s'\n" +
            "\t) o\n" +
            "GROUP BY\n" +
            "\tusername, calltype, connected";
}
