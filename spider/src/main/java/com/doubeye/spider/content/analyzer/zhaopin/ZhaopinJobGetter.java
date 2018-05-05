package com.doubeye.spider.content.analyzer.zhaopin;


import com.doubeye.spider.content.analyzer.AbstractJobGetter;
import net.sf.json.JSONArray;
import org.apache.commons.io.FileUtils;


import java.io.File;


public class ZhaopinJobGetter extends AbstractJobGetter {

    @Override
    public boolean hasMoreOnCurrentPage(JSONArray contents, int currentPage) {
        return contents.size() >= POST_PER_PAGE && currentPage < MAX_PAGE_NUMBER;
    }

    public static void main(String[] args)  {
        String fileName = "d:/spider/zhaopin/jobs_08_31.txt";
        FileUtils.deleteQuietly(new File(fileName));
        ZhaopinJobGetter getter = new ZhaopinJobGetter();
        getter.setUrls(ZhaopinUrlGenerator.getInterestedJobSubtypeCityUrls(true));

        /*
        List<String> urls = new ArrayList<>();
        urls.add("http://sou.zhaopin.com/jobs/searchresult.ashx?bj=160000&sj=2040&jl=北京[@Java开发工程师|||北京");
        getter.setUrls(urls);
        */
        getter.setPageTemplate(ZhaopinUrlGenerator.URL_PAGE_TEMPLATE);
        getter.setContentAnalyzer(new ZhaopinSearchResultListContentAnalyzer());
        getter.setFileName(fileName);
        getter.doGet();

    }

    private static final int POST_PER_PAGE = 60;
    private static final int MAX_PAGE_NUMBER = 90;
}
