package com.doubeye.commons.test.DifferentiationTeller.JSONObjectDifferentiationTellerTest;

import com.doubeye.commons.utils.collection.differentFilter.DifferentFilter;
import com.doubeye.commons.utils.collection.differentFilter.EqualFilter;
import com.doubeye.commons.utils.collection.JSONObjectDifferentiationTeller;
import com.doubeye.commons.utils.json.JSONUtils;
import junit.framework.TestCase;
import net.sf.json.JSONObject;

import java.io.IOException;

/**
 * Created by doubeye(doubeye@sina.com) on 2017/3/21.
 */
public class TestJSONObjectDifferentiationTeller extends TestCase{
    public void testEqual() throws IOException {
        JSONObject result = getTestResult(TEST_ITEM_EQUAL);
        System.out.println(result);
        assertEquals("identical json should have no different count", 0, result.keySet().size());
    }

    public void testFirstHasMore() throws IOException {
        JSONObject result = getTestResult(TEST_ITEM_FIRST_HAS_MORE);
        System.out.println(result);
        assertEquals("first JSON has 2 more properties", 2, result.keySet().size());
        //assertEquals("content test","{\"btn_keywords_jiaofei\":{\"onlyExistsInTheFirst\":true},\"btn_initialdiscard\":{\"onlyExistsInTheFirst\":true}}", result.toString());
    }

    public void testSecondHasMore() throws IOException {
        JSONObject result = getTestResult("secondHasMore");
        System.out.println(result);
        assertEquals("first JSON has 2 more properties", 2, result.keySet().size());
        //assertEquals("content test", "{\"btn_leaveclazz\":{\"onlyExistsInTheSecond\":true},\"btn_queueclazz\":{\"onlyExistsInTheSecond\":true}}", result.toString());
    }

    public void testDifferent() throws IOException {
        JSONObject result = getTestResult("hasDiff");
        System.out.println(result);
        assertEquals("has two different property, and 2 properties appear in one file", 4, result.keySet().size());
        assertEquals("content test", "{\"data_reportonline_studeninfo\":{\"_notEquals\":true,\"_differences\":{\"_firstObjectValue\":\"1\",\"_secondObjectValue\":\"first\"}},\"btn_paymentdetail\":{\"_notEquals\":true,\"_differences\":{\"_firstObjectValue\":\"second\",\"_secondObjectValue\":\"1\"}},\"btn_leaveclazz\":{\"_onlyExistsInTheSecond\":true},\"btn_queueclazz\":{\"_onlyExistsInTheSecond\":true}}", result.toString());
    }

    public void testNestedJSONObjectEqual() throws IOException {
        JSONObject result = getTestResult(TEST_ITEM_ONLY_JSON_OBJECT_NESTED + TEST_ITEM_EQUAL);
        System.out.println(result);
        assertEquals("identical json should have no different count", 0, result.keySet().size());
        assertEquals("content test", "{}", result.toString());
    }

    public void testNestedJSONObjectFirstHasMore() throws IOException {
        JSONObject result = getTestResult(TEST_ITEM_ONLY_JSON_OBJECT_NESTED + TEST_ITEM_FIRST_HAS_MORE);
        System.out.println(result);
        //assertEquals("identical json should have no different count", 0, result.keySet().size());
        //assertEquals("content test", "{}", result.toString());
    }

    private static final String ROOT_PATH = "JSONObjectDifferentiationTeller/";
    private static final String TEST_ITEM_EQUAL = "equal";
    private static final String TEST_ITEM_FIRST_HAS_MORE = "firstHasMore";
    private static final String TEST_ITEM_ONLY_JSON_OBJECT_NESTED = "onlyJSONObjectNested/";
    private static final String FIRST_JSON_FILE = "/json1.txt";
    private static final String SECOND_JSON_FILE = "/json2.txt";


    private JSONObject getTestResult(String testItem) throws IOException {
        JSONObject object1 = JSONUtils.getJsonObjectFromInputStream(this.getClass().getClassLoader().getResourceAsStream(ROOT_PATH + testItem + FIRST_JSON_FILE));
        JSONObject object2 = JSONUtils.getJsonObjectFromInputStream(this.getClass().getClassLoader().getResourceAsStream(ROOT_PATH + testItem + SECOND_JSON_FILE));

        JSONObjectDifferentiationTeller differentiationTeller = new JSONObjectDifferentiationTeller();
        differentiationTeller.setObject1(object1);
        differentiationTeller.setObject2(object2);
        differentiationTeller.compare();
        JSONObject result = differentiationTeller.getCompareResult();

        DifferentFilter filter = new EqualFilter(result);
        result = filter.doFilter();
        return result;
    }

    public static void main(String[] args) throws IOException {
        TestJSONObjectDifferentiationTeller tester = new TestJSONObjectDifferentiationTeller();
        /* flat json object test group
        tester.testEqual();
        tester.testFirstHasMore();
        tester.testSecondHasMore();
        */
        //tester.testDifferent();

        //tester.testNestedJSONObjectEqual();
        tester.testNestedJSONObjectFirstHasMore();
    }
}

