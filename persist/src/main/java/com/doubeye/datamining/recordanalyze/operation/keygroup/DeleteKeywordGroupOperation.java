package com.doubeye.datamining.recordanalyze.operation.keygroup;

import com.doubeye.core.opration.template.AbstractOperation;
import com.doubeye.datamining.recordanalyze.persist.KeywordGroupPersist;

import java.sql.Connection;
import java.sql.SQLException;

import net.sf.json.JSONObject;

/**
 * @author doubeye
 * @version 1.0.0
 * 删除指定的关键词组的操作
 * 需要在objectParameters中指定id属性
 */
public class DeleteKeywordGroupOperation extends AbstractOperation{
    private KeywordGroupPersist persist;

    /**
     * 构造器
     */
    public DeleteKeywordGroupOperation() {
        persist = new KeywordGroupPersist();
    }

    @Override
    public void run() throws SQLException {
        persist.setConn(getConnection());
        persist.deleteKeywordGroupById(objectParameter.getString(PROPERTY_NAME_ID));
    }

    public static DeleteKeywordGroupOperation getInstance(Connection conn, String keywordGroupId) {
        DeleteKeywordGroupOperation operation = new DeleteKeywordGroupOperation();
        JSONObject parameters = new JSONObject();
        parameters.put(PROPERTY_NAME_ID, keywordGroupId);
        operation.setParameters(parameters);
        operation.setConnection(conn);
        return operation;
    }

    public static final String PROPERTY_NAME_ID = "id";
}
