package com.doubeye.core.log.replay.player;

import com.doubeye.commons.utils.constant.CommonConstant;
import com.doubeye.commons.utils.elasticsearch.ClientHelper;
import com.doubeye.commons.utils.elasticsearch.CustomSearchHelper;
import com.doubeye.commons.utils.json.JSONUtils;
import com.doubeye.commons.utils.net.UrlContentGetter;
import net.sf.json.JSONObject;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.search.SearchHits;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * @author doubeye
 * 提交日志回放
 */
public class LogPlayer {
    /**
     * 接口地址
     */
    private String apiUrl;

    /**
     * 参数列表
     */
    private JSONObject parameters;

    /**
     * HTTP方法名
     */
    private String method;

    public String getApiUrl() {
        return apiUrl;
    }

    public void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }

    public JSONObject getParameters() {
        return parameters;
    }

    public void setParameters(JSONObject parameters) {
        this.parameters = parameters;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String play() throws IOException {
        if (CommonConstant.HTTP_METHOD.GET.toString().equalsIgnoreCase(method)) {
            return doGet();
        } else if (CommonConstant.HTTP_METHOD.POST.toString().equalsIgnoreCase(method)) {
            return doPost();
        } else {
            return "";
        }
    }

    private String doGet() throws IOException {
        String url = apiUrl + JSONUtils.toUrl(parameters);
        Map<String, String> header = UrlContentGetter.FIREFOX_HEADER;

        header.put("not_save_logs", "true");
        System.out.println(url);
        return UrlContentGetter.getHtml(url, header);
    }

    private String doPost() throws IOException {
        return UrlContentGetter.doPost(apiUrl, parameters);
    }

    public static void main(String[] args) throws IOException {


        JSONObject parameters = JSONObject.fromObject("{\"_eduUserId\":\"4363\",\"_eduUserName\":\"李昊博\",\"_expiredDate\":\"2018-01-26 11:27:20\",\"_token\":\"44DDE6506B2DA04F800DC50BB4B5C490A945CD3B\",\"_userId\":\"lihaobo@hxsd.local\",\"campus_id\":\"28\",\"end_time\":\"2018-01-25\",\"start_time\":\"2018-01-25\"}");
        LogPlayer player = new LogPlayer();
        player.setApiUrl("http://record.hxsd.local/api/record/report/get_personal_contribute_es");
        player.setMethod("GET");
        player.setParameters(parameters);
        System.out.println(player.play());

    }
}
