package com.doubeye.spider.content.analyzer.job51;

import com.doubeye.commons.utils.collection.CollectionUtils;
import net.sf.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Job51CompanyUrlGenerator {
    public static List<String> getCompanyUrls(String fileName) throws IOException {
        List<String> urls = new ArrayList<>();
        List<String> companies = CollectionUtils.loadFromFile(fileName);
        for (String company : companies) {
            JSONObject companyObject = JSONObject.fromObject(company);
            urls.add(companyObject.getString("companyUrl"));
        }
        return urls;
    }

    public static void main(String[] args) throws IOException {
        System.out.println(Job51CompanyUrlGenerator.getCompanyUrls("d:/spider/job51/processedJobs_10_24.txt.companies"));
    }
}
