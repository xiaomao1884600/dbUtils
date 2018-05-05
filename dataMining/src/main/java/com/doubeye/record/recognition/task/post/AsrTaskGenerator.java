package com.doubeye.record.recognition.task.post;

import com.doubeye.commons.database.ResultSet.ResultSetWarpper.ResultSetJSONWrapper;
import com.doubeye.commons.utils.refactor.RefactorUtils;
import com.doubeye.core.opration.template.AbstractOperation;
import com.doubeye.core.opration.template.Operation;
import com.doubeye.record.recognition.task.constant.PropertyNameConstants;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author doubeye
 * 用来生成上传识别任务的类
 * 该类生成的规则包括：
 * 在指定时间之前，通话时长在指定范围内的指定条通话录音
 */
@SuppressWarnings("unused")
public class AsrTaskGenerator extends AbstractOperation{
    /**
     * 电话录音开始时间
     */
    private String startTime;
    /**
     * 最小有效通话时间
     */
    private int minCallLength;
    /**
     * 最大有效通话时间
     */
    private int maxCallLength;
    /**
     * 记录数
     */
    private int recordCount;


    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public int getMinCallLength() {
        return minCallLength;
    }

    public void setMinCallLength(int minCallLength) {
        this.minCallLength = minCallLength;
    }

    public int getMaxCallLength() {
        return maxCallLength;
    }

    public void setMaxCallLength(int maxCallLength) {
        this.maxCallLength = maxCallLength;
    }

    public int getRecordCount() {
        return recordCount;
    }

    public void setRecordCount(int recordCount) {
        this.recordCount = recordCount;
    }

    private static final String SQL_SELECT_RECORD_INFO = "SELECT\n" +
            "\ti.record_id `recordId`,\n" +
            "\ta.oss_path\n" +
            "FROM\n" +
            "\trecord_info i,\n" +
            "\trecord_analyze a\n" +
            "WHERE\n" +
            "\ti.record_id = a.record_id\n" +
            "AND datetime > '%s'\n" +
            "AND billable >= %d\n" +
            "AND billable <= %d\n" +
            "ORDER BY\n" +
            "\tdatetime DESC\n" +
            "LIMIT %d";

    @Override
    public void run() throws Exception {
        System.out.println(String.format(SQL_SELECT_RECORD_INFO, startTime, minCallLength, maxCallLength, recordCount));
        JSONArray records = ResultSetJSONWrapper.getJSONArrayFromSQL(conn, String.format(SQL_SELECT_RECORD_INFO, startTime, minCallLength, maxCallLength, recordCount));
        getSharedResult().put(PropertyNameConstants.PROPERTY_NAME.RECORD, records);
    }

    /**
     * 获得任务生成器实例，需要如下参数
     *
     * @param conn 数据库连接对象
     * @param parameters 参数，需要指定以下值
     *  startTime {String} 取数据中录音时间开始条件，格式为YYYY-MM-DD
     *  minCallLength {int} 最小通话时长，单位秒
     *  maxCallLength {int} 最大通话时长，单位秒
     *  recordCount {int} 获得记录最大条数
     * @return 任务生成器实例
     */
    public static Operation getInstance(Connection conn, JSONObject parameters) {
        AsrTaskGenerator instance = new AsrTaskGenerator();
        instance.setConnection(conn);
        RefactorUtils.fillByJSON(instance, parameters);
        return instance;
    }
}
