package com.doubeye.spider.content.analyzer.university.gx211;

import com.doubeye.commons.utils.log.LogUtils;
import com.doubeye.commons.utils.net.JsoupContentGetter;
import com.doubeye.spider.content.analyzer.AbstractJobGetter;
import com.doubeye.spider.content.analyzer.ContentGetter;
import com.doubeye.spider.content.analyzer.AbstractJobGetter;
import com.doubeye.spider.content.analyzer.ContentGetter;
import net.sf.json.JSONArray;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.Level;

import java.io.File;

/**
 * @author doubeye
 * @version 1.0.0
 * 中国高校网高校抓取器
 */
public class GxUniversityGetter extends AbstractJobGetter {

    @Override
    public boolean hasMoreOnCurrentPage(JSONArray contents, int currentPage) {
        return false;
    }

    public static void main(String[] args) {
        LogUtils.setLogLevel(Level.TRACE);
        String fileName = "d:/spider/gx211/universities.txt";
        FileUtils.deleteQuietly(new File(fileName));
        ContentGetter getter = new GxUniversityGetter();
        getter.setUrls(Gx211UrlGenerator.getUrls());

        JsoupContentGetter contentGetter = new JsoupContentGetter();
        getter.setPageContentGetter(contentGetter);

        getter.setContentAnalyzer(new GxUniversityListAnalyzer());
        getter.setFileName(fileName);
        getter.doGet();
    }
}
