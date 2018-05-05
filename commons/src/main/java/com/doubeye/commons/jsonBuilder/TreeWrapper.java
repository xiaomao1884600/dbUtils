package com.doubeye.commons.jsonBuilder;

import com.doubeye.commons.database.sql.SQLExecutor;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author doubeye
 * @version 1.0.1
 * history :
 *  1.0.1
 *      + 添加 additionalCondition
 * 将数据库查询结果封装为Tree格式
 */
@SuppressWarnings("unused | WeakerAccess")
public class TreeWrapper {
    /**
     * 数据表名
     */
    private String tableName;
    /**
     * id列名
     */
    private String idColumnName;
    /**
     * 结果中的id属性名
     */
    private String idPropertyName;
    /**
     * 显示的文本字段名
     */
    private String textColumnName;
    /**
     * 文本字段名
     */
    private String textPropertyName;
    /**
     * 子属性名
     */
    private String childPropertyName;
    /**
     * 父记录id字段名
     */
    private String parentIdColumnName;
    /**
     * 条件
     */
    private String topCondition;
    /**
     * 额外的查询条件，可以用作权限控制或其他过滤规则
     */
    private String additionalCondition = "";

    /**
     * 其他感兴趣的字段，在选择是一并选择出，其中的map的key为数据库字段名，value为属性名
     */
    private Map<String, String> interestingColumns;
    /**
     * 排序字段
     */
    private String orderColumnName;
    /**
     * 是否需要建
     */
    private boolean needKey = false;

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getIdColumnName() {
        return idColumnName;
    }

    public void setIdColumnName(String idColumnName) {
        this.idColumnName = idColumnName;
    }

    public String getIdPropertyName() {
        return idPropertyName;
    }

    public void setIdPropertyName(String idPropertyName) {
        this.idPropertyName = idPropertyName;
    }

    public String getTextColumnName() {
        return textColumnName;
    }

    public void setTextColumnName(String textColumnName) {
        this.textColumnName = textColumnName;
    }

    public String getTextPropertyName() {
        return textPropertyName;
    }

    public void setTextPropertyName(String textPropertyName) {
        this.textPropertyName = textPropertyName;
    }

    public String getParentColumnName() {
        return childPropertyName;
    }

    public void setChildPropertyName(String childPropertyName) {
        this.childPropertyName = childPropertyName;
    }

    public String getParentIdColumnName() {
        return parentIdColumnName;
    }

    public void setParentIdColumnName(String parentIdColumnName) {
        this.parentIdColumnName = parentIdColumnName;
    }

    public String getTopCondition() {
        return topCondition;
    }

    public void setTopCondition(String topCondition) {
        this.topCondition = topCondition;
    }

    public void setAdditionalCondition(String additionalCondition) {
        this.additionalCondition = additionalCondition;
    }

    public boolean isNeedKey() {
        return needKey;
    }

    public void setNeedKey(boolean needKey) {
        this.needKey = needKey;
    }
    public void setOrderColumnName(String orderColumnName) {
        this.orderColumnName = orderColumnName;
    }

    public void setInterestingColumns(Map<String, String> interestingColumns) {
        this.interestingColumns = interestingColumns;
    }
    private String getOrderByColumnName() {
        return StringUtils.isEmpty(orderColumnName) ? idColumnName : orderColumnName;
    }
    /**
     * 数据库连接对象
     */
    private Connection conn;

    /**
     * 获得指定内容的语句
     */
    private static final String SQL_SELECT_TREE_LEVEL = "SELECT %s %s, %s %s %s FROM %s WHERE %s ORDER BY %s";

    /**
     * 获得顶层
     * @return 最顶层的数据
     * @throws Exception 异常
     */
    private ResultSet getTopLevel() throws Exception {
        ResultSet rs = SQLExecutor.executeQuery(conn, getSql(topCondition, additionalCondition));
        if (rs == null) {
            throw new Exception("结果集为空");
        } else {
            return rs;
        }
    }

    /**
     * 根据条件生成语句
     * @param condition 条件
     * @param additionalCondition 额外的条件
     * @return SQL语句
     */
    private String getSql(String condition, String additionalCondition) {
        StringBuilder interestingSelectClause = new StringBuilder();
        interestingColumns.keySet().forEach(key -> interestingSelectClause.append(", ").append(key));
        return String.format(SQL_SELECT_TREE_LEVEL, idColumnName, idPropertyName, textColumnName, textPropertyName, interestingSelectClause.toString(), tableName, condition + additionalCondition, getOrderByColumnName());
    }

    /**
     * 获得指定节点的字节点
     * @param parentId 父节点
     * @return 子节点结果集
     * @throws Exception 异常
     */
    private ResultSet getChildNodesRS(long parentId) throws Exception {
        ResultSet rs = SQLExecutor.executeQuery(conn, getSql(parentIdColumnName + " = " + parentId, additionalCondition));
        if (rs == null) {
            throw new Exception("结果集为空");
        } else {
            return rs;
        }
    }

    /**
     * 生成树对象
     * @return 树对象，格式为JSONArray
     * @throws Exception 异常
     */
    public JSONArray generateTree() throws Exception {
        JSONArray tree = new JSONArray();

        ResultSet topRs = getTopLevel();

        while (topRs.next()) {
            JSONObject node = new JSONObject();
            long id = topRs.getLong(idPropertyName);
            String name = topRs.getString(textPropertyName);
            node.put(idPropertyName, id);
            node.put(textPropertyName, name);
            addInterestingInfo(node, interestingColumns, topRs);
            JSONArray children = getChildNodes(id);
            node.put(childPropertyName, children);
            JSONObject newNode = getNewNode(id, node, needKey);
            children.add(newNode);
            tree.add(node);
        }

        return tree;
    }

    private static void addInterestingInfo(JSONObject node, Map<String, String> interestingColumns, ResultSet rs) {
        if (interestingColumns != null) {
            interestingColumns.forEach((key, value) -> {
                try {
                    String columnName = key.contains(" ") ? StringUtils.substring(key, key.lastIndexOf(" ") + 1) : key;
                    node.put(value, rs.getString(columnName));
                } catch (SQLException e) {
                    node.put(value, null);
                }
            });
        }
    }

    /**
     * 获得指定节点的子节点
     * @param parentId 父节点编号
     * @return 子节点对象，格式为JSONArray
     * @throws Exception 异常
     */
    private JSONArray getChildNodes(long parentId) throws Exception {
        JSONArray children = new JSONArray();
        ResultSet childrenRs = getChildNodesRS(parentId);
        while (childrenRs.next()) {
            JSONObject node = new JSONObject();
            long id = childrenRs.getLong(idPropertyName);
            String name = childrenRs.getString(textPropertyName);
            node.put(idPropertyName, id);
            node.put(textPropertyName, name);
            addInterestingInfo(node, interestingColumns, childrenRs);
            JSONArray sub = getChildNodes(id);
            if (sub.size() > 0) {
                node.put(childPropertyName, sub);
            }
            JSONObject newNode = getNewNode(id, node, needKey);
            children.add(newNode);
        }
        return children;
    }

    /**
     * 根据node及id生成node对象
     * @param id id
     * @param node node对象
     * @param needKey 是否生成key
     * @return node对象
     */
    private static JSONObject getNewNode(long id, JSONObject node, boolean needKey) {
        if (needKey) {
            JSONObject newNode = new JSONObject();
            newNode.put(id, node);
            return newNode;
        } else {
            return node;
        }
    }

    /**
     * 设置连接对象
     * @param conn 连接对象
     */
    public void setConn(Connection conn) {
        this.conn = conn;
    }

    public static void main(String[] args) throws Exception {
        /*
        TreeWrapper tb = new TreeWrapper();
        ApplicationContextInitiator.init();
        tb.setConn(GlobalApplicationContext.getInstance().getCoreConnection());
        tb.setTableName("area");
        tb.setIdColumnName("areaid");
        tb.setIdPropertyName("areaid");
        tb.setTextColumnName("title");
        tb.setTextPropertyName("areaName");
        tb.setParentIdColumnName("parentid");
        tb.setChildPropertyName("children");
        tb.setNeedKey(true);
        tb.setTopCondition("parentid = 0 and areaid = 110000");
        JSONArray tree = tb.generateTree();
        System.out.println(tree.toString());
        */
        TreeWrapper tb = new TreeWrapper();
        com.doubeye.commons.utils.test.ApplicationContextInitiator.init();
        tb.setConn(com.doubeye.commons.application.GlobalApplicationContext.getInstance().getCoreConnection());
        tb.setTableName("core_menu");
        tb.setIdColumnName("id");
        tb.setIdPropertyName("menuId");
        tb.setTextColumnName("name");
        tb.setTextPropertyName("name");
        tb.setParentIdColumnName("parent_id");
        tb.setChildPropertyName("pages");
        tb.setNeedKey(false);
        tb.setTopCondition("parent_id = 1");
        Map<String, String> otherColumns = new HashMap<>();
        tb.setInterestingColumns(otherColumns);
        otherColumns.put("component", "state");
        otherColumns.put("identifier", "id");
        otherColumns.put("if(length(component) > 0, 'link', 'toggle') type", "type");
        JSONArray tree = tb.generateTree();
        System.out.println(tree.toString());
    }

}
