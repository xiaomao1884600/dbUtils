package com.doubeye.spider.content.analyzer.tongcheng.resultProcess;

import com.doubeye.spider.content.analyzer.tongcheng.TongChengUrlGenerator;
import org.apache.commons.io.input.BOMInputStream;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GenerateRetryUrls {
    private String fileName;

    public List<String> getUrlsNeedToRetry() throws IOException {
        List<String> urlsToBeRetry = new ArrayList<>();
        Set<String> info = loadRetryInfo();
        List<String> urls = TongChengUrlGenerator.getInterestedJobTypeCityUrls(true);
        urls.forEach(url -> {
            for (String part : info) {
                if (url.contains(part)) {
                    urlsToBeRetry.add(url);
                    break;
                }
            }
        });
        return urlsToBeRetry;
    }

    private Set<String> loadRetryInfo() throws IOException {
        Set<String> info = new HashSet<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new BOMInputStream(new FileInputStream(fileName)), "utf-8"))){
            String line;
            while ((line = reader.readLine()) != null) {
                info.add(line);
            }
        }
        return info;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public static void main(String[] args) throws IOException {
        GenerateRetryUrls processor = new GenerateRetryUrls();
        processor.setFileName("D:/spider/tongcheng/noCountCondition.txt");
        List<String> urls = processor.getUrlsNeedToRetry();
        System.out.println(urls);
        System.out.println(urls.size());
    }
}
