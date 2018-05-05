package com.doubeye.commons.utils.collection.ali.AliKeyValueConverterTest;

import com.doubeye.commons.utils.collection.baidu.AliKeyValueConverter;
import junit.framework.TestCase;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.io.IOException;

/**
 * Created by doubeye(doubeye@sina.com) on 2017/6/1.
 */
public class AliKeyValueConverterTest extends TestCase{
    public void testToAliKeyValue() throws IOException {
        JSONObject test = generateTestJSON();
        String result = AliKeyValueConverter.toAliKeyValue(test);
        assertEquals(result, "信用\u000398\u0002性别\u0003男\u0002类目偏好\u00033298\u00040.89\u00033456\u00040.98\u0002标签\u0003美包控\u0003准妈妈");
    }

    public void testFromAliKeyValue() {
        JSONObject test = generateTestJSON();
        JSONObject result = AliKeyValueConverter.fromAliKeyValue(AliKeyValueConverter.toAliKeyValue(test));
        assertEquals(test.toString(), result.toString());
    }

    private static JSONObject generateTestJSON() {
        JSONObject test = new JSONObject();
        test.put("信用", "98");
        test.put("性别", "男");
        // value 中可以是int类型，但是为了unit test跑通，故意设置为String类型
        JSONObject categoryPreference = new JSONObject();
        categoryPreference.put(3298, "0.89");
        categoryPreference.put(3456, "0.98");
        test.put("类目偏好", categoryPreference);
        JSONArray tags = new JSONArray();
        tags.add("美包控");
        tags.add("准妈妈");
        test.put("标签", tags);
        return test;
    }
}
