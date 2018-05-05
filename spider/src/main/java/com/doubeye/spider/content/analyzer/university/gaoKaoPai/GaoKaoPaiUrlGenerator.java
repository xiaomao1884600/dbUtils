package com.doubeye.spider.content.analyzer.university.gaoKaoPai;

import com.doubeye.spider.content.analyzer.boss.dictionary.BossDictionary;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class GaoKaoPaiUrlGenerator {
    public static List<String> getUrls() {
        List<String> result = new ArrayList<>();
        result.add(UNIVERSITY_LIST_URL_TEMPLATE);
        return result;
    }

    private static final String UNIVERSITY_LIST_URL_TEMPLATE = "http://www.gaokaopai.com/daxue-0-0-0-0-0-0-0--";
    static final String PAGE_TEMPLATE = "p-%s.html";
}
