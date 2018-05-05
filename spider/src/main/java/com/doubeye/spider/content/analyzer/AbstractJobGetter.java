package com.doubeye.spider.content.analyzer;

import com.doubeye.commons.utils.DateTimeUtils.DateTimeUtils;
import com.doubeye.commons.utils.ProgressedRunnable;
import com.doubeye.commons.utils.file.FileUtils;
import com.doubeye.commons.utils.net.DefaultUrlPageContentGetter;
import com.doubeye.commons.utils.net.UrlPageContentGetter;
import com.doubeye.commons.utils.string.StringUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.nodes.Document;

import java.io.IOException;

import java.util.List;

public abstract class AbstractJobGetter implements ProgressedRunnable, ContentGetter, Captchable {

    /**
     * 两次抓取的间隔时间
     */
    private int interval = -1;
    protected static Logger logger = LogManager.getLogger(AbstractJobGetter.class);

    protected float progress;
    private int processingUrl = 0;
    private JSONObject description = new JSONObject();
    private int errorCount = 0;
    private long startTime = System.currentTimeMillis();
    private long cost;
    private UrlPageContentGetter pageContentGetter = new DefaultUrlPageContentGetter();

    /**
     * 当前url的记录数量，如果无法获得当前url的确切页数，返回-1
     */
    private int pageCountOnCurrentUrl = -1;
    private String pageTemplate = "";
    /**
     * url地址列表
     */
    protected List<String> urls;

    protected ContentAnalyzer analyzer;

    protected String fileName;

    @Override
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public void setContentAnalyzer(ContentAnalyzer analyzer) {
        this.analyzer = analyzer;
    }

    protected void setPageCountOnCurrentUrl(int pageCountOnCurrentUrl) {
        this.pageCountOnCurrentUrl = pageCountOnCurrentUrl;
    }

    /**
     * 当前url的记录数量，如果无法获得当前url的确切页数，返回-1
     */
    @Override
    public void setPageCountOnCurrentUrl(Document document) {
        this.pageCountOnCurrentUrl = -1;
    }

    protected int getPageCountOnCurrentUrl() {
        return pageCountOnCurrentUrl;
    }

    @Override
    public float getProgress() {
        return progress;
    }

    @Override
    public String getProgressDescription() {
        return description.toString();
    }

    @Override
    public long getTotalRunCost() {
        return cost;
    }

    @Override
    public void run() {
        doGet();
    }
    @Override
    public void setUrls(List<String> urls) {
        this.urls = urls;
    }

    @Override
    public void persistResult(String fileName, JSONArray contents) throws IOException {
        FileUtils.toFile(fileName, contents, true);
    }

    @Override
    public void setPageTemplate(String pageTemplate) {
        this.pageTemplate = pageTemplate;
    }

    @Override
    public ContentAnalyzer getContentAnalyzer() {
        return analyzer;
    }
    @Override
    public void doGet(String url, int startPage) {
        int index = 0;
        for (String element : urls) {
            if (StringUtils.getStringBefore(element, "[@").equals(url)) {
                break;
            } else {
                index++;
            }
        }
        if (index > 0) {
            urls = urls.subList(index - 1, urls.size() - 1);
        }
        doGet(startPage);
    }

    @Override
    public void doGet(int startPage) {
        int allUrlCount = urls.size();
        if (allUrlCount > 0) {
            urls.forEach(url -> {
                processingUrl ++;
                progress =  (float)processingUrl / allUrlCount * 100;
                String condition = StringUtils.getStringAfterLast(url, "[@");
                analyzer.setAdditionInfo(condition);
                int pageNumber = startPage;
                while (true) {
                    long t1 = System.currentTimeMillis();
                    pageNumber ++;
                    String pagedUrl = StringUtils.getStringBeforeLast(url, "[@") + String.format(pageTemplate, pageNumber);
                    System.out.println(pagedUrl);
                    String content = "";
                    try {
                        content = pageContentGetter.get(pagedUrl);
                        if (interval > 0) {
                            Thread.sleep(interval);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        errorCount ++;
                        logger.error("getting data from " + pagedUrl + " failed : " + e.getMessage());
                        continue;
                    }
                    long t2 = System.currentTimeMillis();
                    analyzer.setContent(content);
                    analyzer.setUrl(pagedUrl);
                    if (pageNumber == 1) {
                        setPageCountOnCurrentUrl(analyzer.getDocument());
                    }
                    if (hasForbidden(analyzer.getDocument())) {
                        logger.error("too Frequent : " + pagedUrl + "[" + url + "]");
                    }
                    try {
                        JSONArray contents = analyzer.doAnalyze();
                        postProcessContents(contents);
                        long t3 = System.currentTimeMillis();
                        persistResult(fileName, contents);
                        long t4 = System.currentTimeMillis();
                        //维护进度和描述信息
                        description.put("lastFinishedUrl", url);
                        description.put("lastFinishedPage", pageNumber);
                        cost = System.currentTimeMillis() - startTime;
                        logger.trace(String.format(LOG_TEMPLATE, pagedUrl, (t2 - t1), (t3 - t2), (t4 - t3), (t4 - t1), contents.size(), progress));
                        if (contents.size() == 0) {
                            errorCount ++;
                            if (errorCount > 5) {
                                System.exit(0);
                            }
                        }
                        //当内容的大小小于每页记录数，表示不在有更多内容，此时无返回结果
                        if (!hasMoreOnCurrentPage(contents, pageNumber)){
                            break;
                        }
                    } catch (Exception e) {
                        errorCount ++;
                        e.printStackTrace();
                        logger.error(pagedUrl + "获取职位信息失败:" + e.getMessage());
                    }
                }
            });
        }
    }
    @Override
    public void doGet() {
        doGet(0);
    }
    //TODO 做成一个列表，保存后处理类
    @Override
    public void postProcessContents(JSONArray contents) {
        for (int i = 0; i < contents.size(); i ++) {
            JSONObject jobObject = contents.getJSONObject(i);
            jobObject.put("date", DateTimeUtils.today());
        }
    }

    @Override
    public void setPageContentGetter(UrlPageContentGetter pageContentGetter) {
        this.pageContentGetter = pageContentGetter;
    }

    @Override
    public boolean hasForbidden(Document document) {
        return false;
    }
    @Override
    public boolean needCaptcha(Document document) {
        return false;
    }

    @Override
    public String getCaptchaContentUrl(Document document) {
        return "";
    }

    @Override
    public void setInterval(int interval) {
        this.interval = interval;
    }

    @Override
    public int getErrorCount() {
        return errorCount;
    }

}
