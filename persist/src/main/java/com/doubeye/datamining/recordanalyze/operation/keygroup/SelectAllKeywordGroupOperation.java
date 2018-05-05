package com.doubeye.datamining.recordanalyze.operation.keygroup;

import com.doubeye.core.opration.template.AbstractOperation;
import com.doubeye.datamining.recordanalyze.persist.KeywordGroupPersist;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.sql.Connection;
import java.sql.SQLException;



/**
 * @author doubeye
 * @version 1.0.0
 * 获得所有的关键词组的操作
 */
public class SelectAllKeywordGroupOperation extends AbstractOperation{
    private KeywordGroupPersist persist;

    /**
     * 构造器
     */
    @SuppressWarnings("WeakerAccess")
    SelectAllKeywordGroupOperation() {
        persist = new KeywordGroupPersist();
    }

    @Override
    public void run() throws SQLException {
        persist.setConn(getConnection());
        JSONArray keywordGroups = persist.getAllKeywordGroups();
        getResult().put(PROPERTY_NAME_KEYWORD_GROUP, keywordGroups);
    }

    public static SelectAllKeywordGroupOperation getInstance(Connection conn) {
        SelectAllKeywordGroupOperation operation = new SelectAllKeywordGroupOperation();
        operation.setConnection(conn);
        return operation;
    }

    @SuppressWarnings("unused")
    private static void printKeyWordGroups(JSONArray keywordGroups) {
        for (int i = 0; i < keywordGroups.size(); i ++) {
            JSONObject keywordGroup = keywordGroups.getJSONObject(i);
            JSONArray keywords = keywordGroup.getJSONArray("keywords");
            for (int j = 0; j < keywords.size(); j ++) {
                System.out.print("\t" + keywords.getString(j));
            }
            System.out.println();
        }
    }

    public static final String PROPERTY_NAME_KEYWORD_GROUP = "keywordGroups";
}
