package com.doubeye.commons.database.ResultSet.DiffrentationTeller;

import com.doubeye.commons.jsonBuilder.JSONWrapper;
import com.doubeye.commons.utils.collection.CollectionUtils;
import com.doubeye.commons.utils.collection.JSONArrayDifferentiationTeller;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 结果集比较器 TODO test
 */
public class ResultSetDifferentiationTeller {

    /**
     * 要比较的第一个结果集
     */
    private ResultSet resultSet1;
    /**
     * 要比较的第二个结果集
     */
    private ResultSet resultSet2;
    /**
     * 结果集中主键字段 TODO
     */
    private String objectKeyPropertyNames;

    private static final String JOINED_UNIQUE_KEY_PROPERTY_NAME = "_|joinedUniqueKey|_";

    private List<String> bothKeys = new ArrayList<>();
    private List<String> identicalKeys = new ArrayList<>();
    private List<String> keyOnlyInArray1 = new ArrayList<>();
    private List<String> keyOnlyInArray2 = new ArrayList<>();
    private List<Map<String, JSONObject>> diffs = new ArrayList<>();

    public void compare() throws SQLException {
        JSONArray array1 = JSONWrapper.getJSON(resultSet1);
        JSONArray array2 = JSONWrapper.getJSON(resultSet2);


        JSONArrayDifferentiationTeller teller = new JSONArrayDifferentiationTeller();
        if (!objectKeyPropertyNames.contains(",")) {
            teller.setArray1(array1);
            teller.setArray2(array2);
            teller.setObjectKeyPropertyName(objectKeyPropertyNames);
        } else {
            teller.setArray1(processOriginArray(array1));
            teller.setArray2(processOriginArray(array2));
            teller.setObjectKeyPropertyName(JOINED_UNIQUE_KEY_PROPERTY_NAME);
        }

        teller.compare();
        bothKeys = teller.getBothKeys();
        identicalKeys = teller.getIdenticalKeys();
        keyOnlyInArray1 = teller.getKeyOnlyInArray1();
        keyOnlyInArray2 = teller.getKeyOnlyInArray2();
        diffs = teller.getDiffs();
    }

    public ResultSet getResultSet1() {
        return resultSet1;
    }

    public void setResultSet1(ResultSet resultSet1) {
        this.resultSet1 = resultSet1;
    }

    public ResultSet getResultSet2() {
        return resultSet2;
    }

    public void setResultSet2(ResultSet resultSet2) {
        this.resultSet2 = resultSet2;
    }

    public String getObjectKeyPropertyNames() {
        return objectKeyPropertyNames;
    }

    public void setObjectKeyPropertyNames(String objectKeyPropertyNames) {
        this.objectKeyPropertyNames = objectKeyPropertyNames;
    }

    public List<String> getBothKeys() {
        return bothKeys;
    }

    public void setBothKeys(List<String> bothKeys) {
        this.bothKeys = bothKeys;
    }

    public List<String> getIdenticalKeys() {
        return identicalKeys;
    }

    public void setIdenticalKeys(List<String> identicalKeys) {
        this.identicalKeys = identicalKeys;
    }

    public List<String> getKeyOnlyInArray1() {
        return keyOnlyInArray1;
    }

    public void setKeyOnlyInArray1(List<String> keyOnlyInArray1) {
        this.keyOnlyInArray1 = keyOnlyInArray1;
    }

    public List<String> getKeyOnlyInArray2() {
        return keyOnlyInArray2;
    }

    public void setKeyOnlyInArray2(List<String> keyOnlyInArray2) {
        this.keyOnlyInArray2 = keyOnlyInArray2;
    }

    public List<Map<String, JSONObject>> getDiffs() {
        return diffs;
    }

    public void setDiffs(List<Map<String, JSONObject>> diffs) {
        this.diffs = diffs;
    }

    public boolean equal() {
        return (keyOnlyInArray1.size() + keyOnlyInArray2.size() + diffs.size()) == 0;
    }

    private JSONArray processOriginArray(JSONArray array) {
        JSONArray result = new JSONArray();
        List<String> uniqueKeys = CollectionUtils.split(objectKeyPropertyNames, ",");
        for (int i = 0; i < array.size(); i ++) {
            JSONObject origin = array.getJSONObject(i);
            JSONObject target = new JSONObject();
            Iterator keys =  origin.keys();
            List<String> uniqueKeyValues = new ArrayList<>();
            while (keys.hasNext()) {
                String key = keys.next().toString();
                if (uniqueKeys.contains(key)) {
                    uniqueKeyValues.add(origin.getString(key));
                } else {
                    target.put(key, origin.get(key));
                }
                target.put(JOINED_UNIQUE_KEY_PROPERTY_NAME, CollectionUtils.split(uniqueKeyValues, ","));
            }
            result.add(target);
        }
        return result;
    }
}
