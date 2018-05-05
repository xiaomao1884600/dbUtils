package com.hxsd.services.utils;

import com.doubeye.commons.utils.collection.JSONObjectDifferentiationTeller;
import com.doubeye.commons.utils.collection.differentFilter.DifferentFilter;
import com.doubeye.commons.utils.collection.differentFilter.EqualFilter;
import com.doubeye.commons.utils.request.RequestHelper;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.Map;

/**
 * Created by doubeye(doubeye@sina.com) on 2017/3/23.
 */
public class JSONCompareService {
    public JSONArray compareJSON(Map<String, String[]> parameters) {
        JSONObject firstJSON = JSONObject.fromObject(RequestHelper.getString(parameters, "firstJSON"));
        JSONObject secondJSON = JSONObject.fromObject(RequestHelper.getString(parameters, "secondJSON"));
        JSONObjectDifferentiationTeller differentiationTeller = new JSONObjectDifferentiationTeller();
        differentiationTeller.setObject1(firstJSON);
        differentiationTeller.setObject2(secondJSON);
        differentiationTeller.compare();
        JSONObject result = differentiationTeller.getCompareResult();
        DifferentFilter filter = new EqualFilter(result);
        result = filter.doFilter();
        JSONArray array = new JSONArray();
        array.add(result);
        return array;
    }
}
