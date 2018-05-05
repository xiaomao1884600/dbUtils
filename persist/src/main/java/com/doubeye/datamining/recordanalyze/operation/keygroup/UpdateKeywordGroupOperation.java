package com.doubeye.datamining.recordanalyze.operation.keygroup;

import com.doubeye.core.opration.template.AbstractOperation;
import com.doubeye.core.opration.template.Operation;
import com.doubeye.datamining.recordanalyze.persist.KeywordGroupPersist;
import net.sf.json.JSONObject;

import java.sql.Connection;
import java.sql.SQLException;


/**
 * @author doubeye
 * @version 1.0.0
 * 更新关键词组操作
 * 需要jsonObject类型的参数，内容为关键词组的JSONObject
 */
public class UpdateKeywordGroupOperation extends AbstractOperation {
    /**
     * 关键词组持久类
     */
    private KeywordGroupPersist persist;

    /**
     * 构造器
     */
    public UpdateKeywordGroupOperation() {
        persist = new KeywordGroupPersist();
    }

    /**
     * 获得关键词组添加操作实例
     *
     * @param conn       数据库连接
     * @param parameters 参数
     *                   需要jsonObject类型的参数，内容为关键词组的JSONObject
     * @return 关键词组添加操作对象
     */
    public static Operation getInstance(Connection conn, JSONObject parameters) {
        UpdateKeywordGroupOperation operation = new UpdateKeywordGroupOperation();
        operation.setConnection(conn);
        operation.setParameters(parameters);
        return operation;
    }

    @Override
    public void run() throws SQLException {
        persist.setConn(getConnection());
        persist.updateKeywordGroup(objectParameter);
    }
}
