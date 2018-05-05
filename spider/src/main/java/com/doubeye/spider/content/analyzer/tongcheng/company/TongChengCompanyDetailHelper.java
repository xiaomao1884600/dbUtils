package com.doubeye.spider.content.analyzer.tongcheng.company;

import com.doubeye.commons.utils.collection.CollectionUtils;
import com.doubeye.commons.utils.file.FileUtils;
import com.doubeye.commons.utils.net.UrlContentGetter;
import com.doubeye.spider.content.analyzer.tongcheng.TongChengCompanyDetailPageAnalyzer;
import com.doubeye.spider.content.analyzer.tongcheng.TongChengSearchResultListContentAnalyzer;
import com.doubeye.spider.content.analyzer.tongcheng.dictionary.TongChengDictionary;
import com.doubeye.spider.content.analyzer.tongcheng.TongChengCompanyDetailPageAnalyzer;
import com.doubeye.spider.content.analyzer.tongcheng.TongChengSearchResultListContentAnalyzer;
import com.doubeye.spider.content.analyzer.tongcheng.dictionary.TongChengDictionary;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;

/**
 * @author doubeye
 * @version 1.0.0
 * 58同城公司详情助手
 */
public class TongChengCompanyDetailHelper {
    private JSONObject companyObject;
    TongChengSearchResultListContentAnalyzer analyzer = new TongChengSearchResultListContentAnalyzer();

    public TongChengCompanyDetailHelper() {
        analyzer.setIgnoreDetail(true);
    }

    public void setCompanyObject(JSONObject companyObject) {
        this.companyObject = companyObject;
    }

    private String getCityCode() {
        String cityHolder;
        if (companyObject.containsKey("city")) {
            cityHolder = companyObject.getString("city");
        } else {
            cityHolder = companyObject.getString("company");
        }

        for (int i = 0; i < CITIES.size(); i ++) {
            JSONObject cityObject = CITIES.getJSONObject(i);
            if (cityHolder.contains(cityObject.getString("name"))) {
                return cityObject.getString("code");
            }
        }
        return "";
    }

    private static String getCityCode(String cityName) {
        return "";
    }

    private static final JSONArray CITIES = new JSONArray();
    private static void flattenCities() {
        for (int i = 0; i < TongChengDictionary.TONG_CHENG_CITY.size(); i ++){
            JSONObject province = TongChengDictionary.TONG_CHENG_CITY.getJSONObject(i);
            String provinceName = province.keys().next().toString();
            JSONArray citiesInProvince = province.getJSONArray(provinceName);
            for (int city = 0; city < citiesInProvince.size(); city ++) {
                CITIES.add(citiesInProvince.getJSONObject(city));
            }
        }
    }

    private String getCompanyName() {
        return companyObject.getString("company");
    }

    public JSONObject spiderCompany() throws IOException {
        String cityCode = getCityCode();
        if (StringUtils.isEmpty(cityCode)) {
            cityCode = "bj";
        }
        String url = String.format(URL_TEMPLATE, cityCode, URLEncoder.encode(getCompanyName(), "utf-8"));
        System.out.println(url);
        String content = UrlContentGetter.getHtmlCode(url, "utf-8");

        analyzer.setContent(content);
        JSONArray companies = analyzer.doAnalyze();
        for (int i = 0; i < companies.size(); i ++) {
            JSONObject company = companies.getJSONObject(i);
            if (company.getString("company").contains(getCompanyName()) || getCompanyName().contains(company.getString("company"))) {
                TongChengCompanyDetailPageAnalyzer analyzer = new TongChengCompanyDetailPageAnalyzer();
                analyzer.setContent(UrlContentGetter.getHtmlCode(company.getString("companyUrl"), "utf-8"));
                JSONArray detail = analyzer.doAnalyze();
                if (detail.size() > 0) {
                    company.putAll(detail.getJSONObject(0));

                }
                return company;
            }
        }
        return new JSONObject();
    }

    static {
        flattenCities();
    }

    /**
     * 城市和公司名称搜索url模板，第一个参数为城市的code，第二个参数为公司名
     */
    private static final String URL_TEMPLATE = "http://%s.58.com/job/?key=%s&final=1&jump=1";

    public static void main(String[] args) throws IOException {
        //测试5858.com结尾的公司
        /*
        JSONObject company = new JSONObject();
        TongChengCompanyDetailHelper helper = new TongChengCompanyDetailHelper();

        company.put("company", "北京振远护卫中心");
        helper.setCompanyObject(company);
        System.out.println(helper.spiderCompany());


        company.put("company", "北京国威保安服务有限责任公司");
        helper.setCompanyObject(company);
        System.out.println(helper.spiderCompany());
        */

        String fileName = "d:/spider/job51/company_names_11_01_addedByTongChengPage.txt";
        org.apache.commons.io.FileUtils.deleteQuietly(new File(fileName));
        TongChengCompanyDetailHelper helper = new TongChengCompanyDetailHelper();
        List<String> companyNames = CollectionUtils.loadFromFile("d:/spider/job51/company_names_11_01.txt");
        int i = 0, findCount = 0;
        for (String companyName : companyNames) {
            boolean found = false;
            JSONObject companyObject = new JSONObject();
            companyObject.put("company", companyName);
            helper.setCompanyObject(companyObject);
            try {
                i ++;
                JSONObject companyResult = helper.spiderCompany();
                if (!companyResult.isEmpty()) {
                    findCount ++;
                    companyResult.put("originCompanyName", companyName);
                    found = true;
                    StringBuilder builder = new StringBuilder();
                    builder.append(companyResult.toString()).append("\r\n");
                    FileUtils.toFile(fileName, builder, true);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println(i + "  " + companyName + " " + found + "      founded " + findCount + "[" + new Date() + "]");
        }


    }

}
