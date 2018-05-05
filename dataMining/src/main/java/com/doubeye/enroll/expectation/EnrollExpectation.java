package com.doubeye.enroll.expectation;

import com.doubeye.commons.utils.collection.CollectionUtils;
import com.doubeye.commons.utils.math.MathUtils;
import com.doubeye.commons.utils.request.RequestHelper;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.Map;

/**
 * @author doubeye
 * 招生情况预测
 */
public class EnrollExpectation {
    public JSONArray getBunchExpectation(Map<String, String[]> parameters) {
        int expectationDays = RequestHelper.getInt(parameters, "expectationDays");
        JSONArray result = new JSONArray();
        JSONArray toBeExpected = RequestHelper.getJSONArray(parameters, "enrolls");
        for (int i = 0; i < toBeExpected.size(); i ++) {
            JSONArray entry = toBeExpected.getJSONArray(i);
            double[] values = CollectionUtils.splitDouble(entry);
            double[] expectation = MathUtils.getDoubleExponential(values, expectationDays, 0.8f, 0.2f);
            long value = Math.round(expectation[expectation.length - 1]);
            result.add(value < 0 ? 0 : value);
        }
        return result;
    }
}
