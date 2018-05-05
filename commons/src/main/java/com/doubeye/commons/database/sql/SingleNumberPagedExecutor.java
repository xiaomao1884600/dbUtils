package com.doubeye.commons.database.sql;

import com.doubeye.commons.database.connection.ConnectionHelper;
import com.doubeye.commons.jsonBuilder.JSONWrapper;
import net.sf.json.JSONArray;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;



import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * @author doubeye
 * @version 1.0.0
 * 分页式的SQL运行器，为了降低每个语句的执行时间，需要将大的语句分页执行
 * 注意：SQL模板中一定要包含以下两个部分
 * 分页中的:start
 * 分页中的:end
 * SQL模板举例：
 * SELECT
 * p.passportid, comeFromPassport, IFNULL(gender, 0) gender, IFNULL(age, 0) age, IFNULL(comeFromE, 0) `comeFromE`, IFNULL(cityid, '0') cityid, IFNULL(faculties, '') faculties
 * FROM
 * t_user_from_passport p
 * LEFT OUTER JOIN t_student_from_edu e ON p.passportid = e.passportid
 * WHERE p.passportid > :start AND p.passportId <= :end
 * TODO 此方法只适用于单个数字型字段，作为分页条件
 */
public abstract class SingleNumberPagedExecutor {
    private static Logger logger = LogManager.getLogger(SingleNumberPagedExecutor.class);

    /**
     * 执行语句的连接对象
     */
    private Connection conn;
    /**
     * SQL语句模板，该模板中一定要包括:start和:end参数
     */
    private String sqlTemplate;
    /**
     * 每次执行的记录条数，用来计算每次的start和end
     * 注意：除非使用分页语句，如MySQL中的LIMIT，否则该值只能是近似值。
     * 比如使用主键作为分页条件，则可能存在空洞(如删除的记录)，造成实际的返回条数与指定不等
     */
    private int recordCountPerPage;

    /**
     * 当使用自增主键进行分页时，如果主键空洞大于recordCountPerPage，会造成数据遍历提前结束，将此参数设置为大于最大id，可以避免此问题
     */
    private int startMustAfter = -1;
    /**
     * 分页字段字段名，用来每次生成新的分页条件
     * TODO 需增加对LIMIT的支持，增加对多字段的支持
     */
    private String autoIncrementColumnName;
    /**
     * 是否还有数据，内部使用，用来查看是否所有数据都执行完毕
     */
    private boolean hasData = false;
    /**
     * 是否开始，内部使用
     */
    private boolean started = false;
    /**
     * 预处理语句，内部使用
     */
    private PreparedStatement ps = null;
    /**
     * 预处理语句的参数列表，内部使用
     */
    private List<String> namedParameters = new ArrayList<>();

    /**
     * 执行
     * @throws Exception 异常
     */
    public void run() throws Exception {
        logger.debug(sqlTemplate);
        String sql = SQLExecutor.getSQLFromTemplate(sqlTemplate, namedParameters);
        ps = conn.prepareStatement(sql);
        PageBean bean = new PageBean();
        bean.end = recordCountPerPage;
        executeQuery(bean);
    }

    /**
     * 执行语句
     * @param bean 分页类
     * @throws Exception 异常
     */
    private void executeQuery(PageBean bean) throws Exception {
        ResultSet resultSet = null;
        try {
            //如果没有开始，或者仍然有数据时，一直执行
            while (!started || hasData) {
                started = true;
                hasData = false;
                SQLExecutor.setParameterInPreparedStatement(ps, namedParameters, bean);
                resultSet = ps.executeQuery();
                JSONArray result = JSONWrapper.getJSON(resultSet);
                if (result.size() > 0 && bean.start > startMustAfter) {
                    hasData = true;
                    bean.start = result.getJSONObject(result.size() - 1).getLong(autoIncrementColumnName);
                    //计算结束值
                    bean.end = bean.start + recordCountPerPage;
                    afterRetrieveData(result);
                }
                /*
                while(resultSet.next()) {
                    //只要存在任何数据，都需要再次执行语句
                    hasData = true;
                    //获得开始的值 TODO 支持多字段
                    bean.start = resultSet.getLong(autoIncrementColumnName);
                    afterRetrieveData(resultSet);
                }
                 */


            }
        } finally {
            ConnectionHelper.close(resultSet);
        }
    }

    /**
     * 分页类
     */
    class PageBean {
        /**
         * 开始
         */
        long start = 0;
        /**
         * 结束
         */
        long end = 0;

        public long getStart() {
            return start;
        }

        public void setStart(long start) {
            this.start = start;
        }

        public long getEnd() {
            return end;
        }

        public void setEnd(long end) {
            this.end = end;
        }
    }

    /**
     * 每条数据处理完后执行的回调函数
     * @param result 结果集
     * @throws Exception 异常
     */

    public abstract void afterRetrieveData(JSONArray result) throws Exception;

    public Connection getConn() {
        return conn;
    }

    public void setConn(Connection conn) {
        this.conn = conn;
    }
    @SuppressWarnings("unused")
    public String getSqlTemplate() {
        return sqlTemplate;
    }

    public void setSqlTemplate(String sqlTemplate) {
        this.sqlTemplate = sqlTemplate;
    }
    @SuppressWarnings("unused")
    public int getRecordCountPerPage() {
        return recordCountPerPage;
    }

    public void setRecordCountPerPage(int recordCountPerPage) {
        this.recordCountPerPage = recordCountPerPage;
    }
    @SuppressWarnings("unused")
    public String getAutoIncrementColumnName() {
        return autoIncrementColumnName;
    }

    public void setStartMustAfter(int startMustAfter) {
        this.startMustAfter = startMustAfter;
    }

    public void setAutoIncrementColumnName(String autoIncrementColumnName) {
        this.autoIncrementColumnName = autoIncrementColumnName;
    }
}
