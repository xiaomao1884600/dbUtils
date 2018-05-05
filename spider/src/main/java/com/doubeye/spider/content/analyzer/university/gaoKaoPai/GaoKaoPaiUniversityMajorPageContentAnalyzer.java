package com.doubeye.spider.content.analyzer.university.gaoKaoPai;

import com.doubeye.spider.content.analyzer.AbstractContentAnalyzer;
import com.doubeye.spider.content.analyzer.AbstractContentAnalyzer;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author doubeye
 * @version 1.0.0
 * 高考派院校开设专业页分析器
 */
public class GaoKaoPaiUniversityMajorPageContentAnalyzer extends AbstractContentAnalyzer {

    @Override
    public JSONArray doAnalyze() throws IOException {
        JSONArray result = new JSONArray();
        Elements majorElements = getDocument().select(SELECTOR_MAJOR_ROOT_ELEMENTS);
        for (Element majorElement : majorElements) {
            JSONObject majorObject = new JSONObject();
            String majorTitle = StringUtils.substringBetween(Jsoup.parse(majorElement.getElementsByTag("h3").html()).text(), "■ ", "（");
            List<String> subMajors = new ArrayList<>();
            Elements subMajorElements = majorElement.getElementsByTag("li");
            for (Element subMajorElement : subMajorElements) {
                String subMajorTitle = Jsoup.parse(subMajorElement.html()).text();
                subMajors.add(subMajorTitle);
            }
            majorObject.put(majorTitle, subMajors);
            result.add(majorObject);
        }
        return result;
    }

    @Override
    public int getCurrentPage() {
        return 1;
    }

    private static final String SELECTOR_MAJOR_ROOT_ELEMENTS = "div.majorCon";

}
