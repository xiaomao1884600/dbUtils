package com.doubeye.spider.content.analyzer.job51;

import com.doubeye.spider.content.analyzer.AbstractContentAnalyzer;
import com.doubeye.spider.content.analyzer.AbstractContentAnalyzer;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

/**
 * @author douebye
 * @version 1.0.0
 * 前程无忧岗位详情页内容分析器
 */
public class Job51PostDetailContentAnalyzer extends AbstractContentAnalyzer {
    @Override
    public JSONArray doAnalyze() throws IOException {
        JSONArray result = new JSONArray();
        JSONObject jobObject = new JSONObject();
        //公司属性
        Element companyPropertyElement = getDocument().select(SELECTOR_COMPANY_PROPERTY_ELEMENT).first();
        if (companyPropertyElement != null) {
            String content = Jsoup.parse(companyPropertyElement.html()).text();
            String[] parts = content.split("\\|");
            for (String part : parts) {
                if (part.contains("公司")) {
                    jobObject.put("companyProperty", part.replace(" ", "").trim());
                } else if (part.contains("人")){
                    jobObject.put("companySize", part.replace(" ", "").trim());
                }
            }
        }

        Elements jobRequirementElements = getDocument().select(SELECTOR_POST_REQUIREMENT_ELEMENTS);
        for (Element element : jobRequirementElements) {
            //经验
            if (element.select(SELECTOR_EXPERIENCE).size() > 0) {
                jobObject.put("experience", Jsoup.parse(element.html()).text());
            } else if (element.select(SELECTOR_DEGREE).size() > 0) {//学历
                jobObject.put("degree", Jsoup.parse(element.html()).text());
            } else if (element.select(SELECTOR_COUNT).size() > 0) {//招聘人数
                jobObject.put("count", Jsoup.parse(element.html()).text().replace("招", "").replace("人", ""));
            }
        }


        result.add(jobObject);
        return result;
    }

    @Override
    public int getCurrentPage() {
        return 1;
    }

    private static final String SELECTOR_COMPANY_PROPERTY_ELEMENT = "p.ltype";
    private static final String SELECTOR_POST_REQUIREMENT_ELEMENTS = "div.jtag span.sp4";
    private static final String SELECTOR_EXPERIENCE = "em.i1";
    private static final String SELECTOR_DEGREE = "em.i2";
    private static final String SELECTOR_COUNT = "em.i3";
}
