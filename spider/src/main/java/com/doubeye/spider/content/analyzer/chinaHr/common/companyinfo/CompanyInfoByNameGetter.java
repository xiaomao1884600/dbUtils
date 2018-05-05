package com.doubeye.spider.content.analyzer.chinaHr.common.companyinfo;

import com.doubeye.commons.utils.collection.CollectionUtils;
import com.doubeye.commons.utils.file.FileUtils;
import com.doubeye.commons.utils.net.BrowserSimulatedContentGetter;
import com.doubeye.commons.utils.net.UrlContentGetter;
import com.doubeye.spider.content.analyzer.ContentAnalyzer;
import com.doubeye.spider.content.analyzer.chinaHr.ChinaHrCompanyDetailContentAnalyzer;
import com.doubeye.spider.content.analyzer.chinaHr.ChinaHrSearchResultListContentAnalyzer;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author doubeye
 * @version 1.0.0
 * 根据公司名称查找公司联系信息
 */
public class CompanyInfoByNameGetter {
    private String companyName;
    private ChinaHrSearchResultListContentAnalyzer resultListAnalyzer;
    private ContentAnalyzer companyDetailAnalyzer = new ChinaHrCompanyDetailContentAnalyzer();
    private boolean debug;

    public CompanyInfoByNameGetter() {
        resultListAnalyzer = new ChinaHrSearchResultListContentAnalyzer();
        resultListAnalyzer.setIgnoreDetail(true);
    }

    private JSONObject getPost() throws IOException {
        String url = String.format(URL_SEARCH_BY_COMPANY_NAME_TEMPLATE, URLEncoder.encode(companyName, "utf-8"));
        BrowserSimulatedContentGetter contentGetter = new BrowserSimulatedContentGetter();
        Map<String, String> header = new HashMap<>();
        header.put("Referer", url);
        contentGetter.setHeader(header);
        String content = contentGetter.get(url);
        if (debug) {
            System.err.print(content);
        }
        resultListAnalyzer.setContent(content);
        JSONArray posts = resultListAnalyzer.doAnalyze();
        for (int i = 0; i < posts.size(); i ++) {
            JSONObject post = posts.getJSONObject(i);
            if (post.getString("companyNameInList").contains(companyName) || companyName.contains(post.getString("companyNameInList"))) {
                return post;
            }
        }
        return null;
    }

    public JSONObject getCompanyInfo() throws IOException {
        JSONObject post = getPost();
        if (post != null) {
            String postUrl = post.getString("companyUrl");
            String content = UrlContentGetter.getHtmlCode(postUrl, "utf-8");
            if (debug) {
                System.err.print(content);
            }
            companyDetailAnalyzer.setContent(content);
            JSONArray companyObjects = companyDetailAnalyzer.doAnalyze();
            if (companyObjects.size() > 0) {
                return companyObjects.getJSONObject(0);
            }
        }
        return new JSONObject();
    }

    /**
     * 中华英才根据公司查找职位的url模板，参数为公司名称
     */
    private static final String URL_SEARCH_BY_COMPANY_NAME_TEMPLATE = "http://www.chinahr.com/sou/?keyword=%s";

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }


    public static void main(String[] args) throws IOException {
        //好未来 鲁班软件 北京薪思特文化传媒有限公司
        CompanyInfoByNameGetter getter = new CompanyInfoByNameGetter();
        List<String> companyNames = CollectionUtils.loadFromFile("d:/spider/job51/company_names_11_01.txt");
        JSONArray results = new JSONArray();
        int i = 0;
        for (String companyName : companyNames) {
            i ++;
            getter.setCompanyName(companyName);
            try {
                JSONObject companyObject = getter.getCompanyInfo();
                if (!companyObject.isEmpty()) {
                    companyObject.put("originCompanyName", companyName);
                    System.out.println(i + "---" +results.size() + "  " + companyObject.toString());
                    results.add(companyObject);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        FileUtils.toFile("d:/spider/job51/company_names_11_01_addedByChinaHr.txt", results);
    }
}
