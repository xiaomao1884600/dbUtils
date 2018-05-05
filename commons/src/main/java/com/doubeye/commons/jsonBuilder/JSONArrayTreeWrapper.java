package com.doubeye.commons.jsonBuilder;

import com.doubeye.commons.database.ResultSet.ResultSetWarpper.ResultSetJSONWrapper;

import com.doubeye.commons.utils.json.JSONUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;


import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author douebye
 * @version 1.0.0
 * 将JSONArray 转换为tree形式的JSONArray的助手类
 */
@SuppressWarnings("unused | WeakerAccess")
public class JSONArrayTreeWrapper {
    /**
     * 结果中的id属性名
     */
    private String idPropertyName;
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
    private String parentIdPropertyName;
    /**
     * 结果中id的属性名
     */
    private String idPropertyNameInResult;
    /**
     * 结果中text的属性名
     */
    private String textPropertyNameInResult;

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
    /**
     * 顶级的条件
     */
    private JSONObject topCondition;

    /**
     * 要转换的JSONArray 对象
     */
    private JSONArray originArray;


    public String getIdPropertyName() {
        return idPropertyName;
    }

    public void setIdPropertyName(String idPropertyName) {
        this.idPropertyName = idPropertyName;
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

    public String getParentIdPropertyName() {
        return parentIdPropertyName;
    }

    public void setParentIdPropertyName(String parentIdPropertyName) {
        this.parentIdPropertyName = parentIdPropertyName;
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
        return StringUtils.isEmpty(orderColumnName) ? idPropertyName : orderColumnName;
    }

    public JSONArray getOriginArray() {
        return originArray;
    }

    public void setOriginArray(JSONArray originArray) {
        this.originArray = originArray;
    }

    public void setIdPropertyNameInResult(String idPropertyNameInResult) {
        this.idPropertyNameInResult = idPropertyNameInResult;
    }

    public void setTextPropertyNameInResult(String textPropertyNameInResult) {
        this.textPropertyNameInResult = textPropertyNameInResult;
    }

    public void setTopCondition(JSONObject topCondition) {
        this.topCondition = topCondition;
    }

    /**
     * 生成树对象
     * @return 树对象，格式为JSONArray
     * @throws Exception 异常
     */
    public JSONArray generateTree() throws Exception {
        if (originArray == null) {
            return new JSONArray();
        }
        JSONArray result = new JSONArray();
        generateTree(result, originArray, topCondition);
        JSONUtils.sortString(result, orderColumnName);
        return result;
    }

    private void generateTree(JSONArray result, JSONArray source, JSONObject condition) {
        fillResultArray(result, JSONUtils.findAll(source, condition));
        for (int i = 0; i < result.size(); i ++) {
            JSONObject element = result.getJSONObject(i);
            Object id = element.get(idPropertyName);
            JSONObject childrenCondition = new JSONObject();
            childrenCondition.put(parentIdPropertyName, id);
            JSONArray children = new JSONArray();
            generateTree(children, originArray, childrenCondition);
            if (children.size() > 0) {
                JSONUtils.sortString(children, orderColumnName);
                // 此处需要添加对intresting property的设置
                element.put(childPropertyName, children);
            }
        }

    }

    private void fillResultArray(JSONArray result, JSONArray toBeAddedArray) {
        for (int i = 0; i < toBeAddedArray.size(); i ++) {
            JSONObject element = toBeAddedArray.getJSONObject(i);
            JSONObject resultElement = new JSONObject();
            resultElement.put(idPropertyNameInResult, element.get(idPropertyName));
            resultElement.put(textPropertyNameInResult, element.get(textPropertyName));
            if (interestingColumns != null) {
                interestingColumns.forEach((key, value) -> {
                    if (element.containsKey(key)) {
                        resultElement.put(value, element.get(key));
                    }
                });
            }
            result.add(resultElement);
        }
    }

    public static void main(String[] args) throws Exception {
        com.doubeye.commons.utils.test.ApplicationContextInitiator.init();
        try (Connection conn = com.doubeye.commons.application.GlobalApplicationContext.getInstance().getCoreConnection()) {
            JSONArray menus = ResultSetJSONWrapper.getJSONArrayFromSQL(conn, "SELECT id menuId, name name , identifier, component, if(length(component) > 0, 'link', 'toggle') type, parent_id, `order` FROM core_menu");
            JSONArrayTreeWrapper tw = new JSONArrayTreeWrapper();
            tw.setOriginArray(menus);
            tw.setIdPropertyName("menuId");
            tw.setIdPropertyNameInResult("menuId");
            tw.setTextPropertyName("name");
            tw.setTextPropertyNameInResult("name");
            tw.setParentIdPropertyName("parent_id");
            tw.setChildPropertyName("pages");
            tw.setOrderColumnName("order");
            JSONObject condition = new JSONObject();
            condition.put("parent_id", 1);
            tw.setTopCondition(condition);
            Map<String, String> otherColumns = new HashMap<>();
            otherColumns.put("component", "state");
            otherColumns.put("identifier", "id");
            otherColumns.put("type", "type");
            otherColumns.put("order", "order");
            tw.setInterestingColumns(otherColumns);
            JSONArray tree = tw.generateTree();
            System.out.println(tree.toString());

        }
    }

}
