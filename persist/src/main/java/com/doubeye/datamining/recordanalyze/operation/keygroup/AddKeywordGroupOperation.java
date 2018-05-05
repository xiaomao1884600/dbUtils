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
 * 关键词组添加操作
 * 需要jsonObject类型的参数，内容为关键词组的JSONObject
 */
public class AddKeywordGroupOperation extends AbstractOperation {
    /**
     * 关键词组持久类
     */
    private KeywordGroupPersist persist;

    /**
     * 构造器
     */
    public AddKeywordGroupOperation() {
        persist = new KeywordGroupPersist();
    }

    @Override
    public void run() throws SQLException {
        persist.setConn(getConnection());
        persist.addKeywordGroup(objectParameter);
    }

    /**
     * 获得关键词组添加操作实例
     * @param conn 数据库连接
     * @param parameters 参数
     *                   需要jsonObject类型的参数，内容为关键词组的JSONObject
     * @return 关键词组添加操作对象
     */
    public static Operation getInstance(Connection conn, JSONObject parameters) {
        AddKeywordGroupOperation operation = new AddKeywordGroupOperation();
        operation.setConnection(conn);
        operation.setParameters(parameters);
        return operation;
    }
}
