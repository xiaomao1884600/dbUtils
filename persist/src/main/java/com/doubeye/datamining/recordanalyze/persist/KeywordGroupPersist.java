package com.doubeye.datamining.recordanalyze.persist;

import com.doubeye.commons.database.ResultSet.ResultSetWarpper.ResultSetJSONWrapper;
import com.doubeye.commons.database.sql.SQLExecutor;
import com.doubeye.commons.utils.string.StringUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author doubeye
 * @version 1.0.0
 * 关键词组持久类类
 */
public class KeywordGroupPersist {
    private Connection conn;

    public void setConn(Connection conn) {
        this.conn = conn;
    }

    public void addKeywordGroup(JSONObject keywordGroup) throws SQLException {
        SQLExecutor.execute(conn, StringUtils.format(SQL_INSERT_INTO_KEYWORD_GROUP, keywordGroup));
    }

    public JSONArray getAllKeywordGroups() throws SQLException {
        return ResultSetJSONWrapper.getJSONArrayFromSQL(conn, SQL_SELECT_ALL_KEYWORD_GROUP);
    }

    public JSONArray getAllKeywordGroupNames() throws SQLException {
        return ResultSetJSONWrapper.getJSONArrayFromSQL(conn, SQL_SELECT_ALL_KEYWORD_GROUP_NAME);
    }

    public void deleteKeywordGroupById(String keywordGroupId) throws SQLException {
        SQLExecutor.execute(conn, String.format(SQL_DELETE_KEYWORD_GROUP_BY_ID, keywordGroupId));
    }

    public void updateKeywordGroup(JSONObject keywordGroup) throws SQLException {
        SQLExecutor.execute(conn, StringUtils.format(SQL_UPDATE_KEYWORD_GROUP, keywordGroup));
    }

    private static final String SQL_INSERT_INTO_KEYWORD_GROUP = "INSERT INTO dict_keyword_group(name, keywords, category, objection) VALUES('([{name}])', '([{keywords}])', '([{category}])', ([{objection}]))";
    private static final String SQL_SELECT_ALL_KEYWORD_GROUP = "SELECT id, name, keywords, category, objection FROM dict_keyword_group";
    private static final String SQL_DELETE_KEYWORD_GROUP_BY_ID = "DELETE FROM dict_keyword_group WHERE id = %s";
    private static final String SQL_UPDATE_KEYWORD_GROUP = "UPDATE dict_keyword_group SET name = '([{name}])', keywords = '([{keywords}])', category = '([{category}])',  objection = ([{objection}]) WHERE id = ([{id}])";
    private static final String SQL_SELECT_ALL_KEYWORD_GROUP_NAME = "SELECT id, name FROM dict_keyword_group";
}
