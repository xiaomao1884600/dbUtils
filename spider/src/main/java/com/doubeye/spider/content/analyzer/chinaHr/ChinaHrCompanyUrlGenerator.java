package com.doubeye.spider.content.analyzer.chinaHr;

import com.doubeye.commons.utils.collection.CollectionUtils;
import net.sf.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ChinaHrCompanyUrlGenerator {
    public static List<String> getCompanyUrls(String fileName) throws IOException {
        List<String> urls = new ArrayList<>();
        List<String> companies = CollectionUtils.loadFromFile(fileName);
        for (String company : companies) {
            JSONObject companyObject = JSONObject.fromObject(company);
            //if (needReget(companyObject)) {
                urls.add(companyObject.getString("companyUrl"));
            //}
        }
        return urls;
    }

    private static boolean needReget(JSONObject company) {

        if (company.containsKey("mobile")) {
            return false;
        }
        if (company.containsKey("phone")) {
            return false;
        }
        if (company.containsKey("email")) {
            return false;
        }
        //todo change to realUrl
        if (company.containsKey("readUrl")) {
            return false;
        }
        if (company.containsKey("address")) {
            return false;
        }
        if (company.containsKey("contacts")) {
            return false;
        }
        return true;
    }

    public static void main(String[] args) throws IOException {
        System.out.println(ChinaHrCompanyUrlGenerator.getCompanyUrls("D:\\spider\\chinaHr\\processedJobs_09_19.txt.companies").size());
    }
}
