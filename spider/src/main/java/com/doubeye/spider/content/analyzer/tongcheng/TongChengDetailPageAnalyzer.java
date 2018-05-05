package com.doubeye.spider.content.analyzer.tongcheng;

import com.doubeye.commons.utils.net.UrlContentGetter;
import com.doubeye.spider.content.analyzer.AbstractContentAnalyzer;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class TongChengDetailPageAnalyzer extends AbstractContentAnalyzer{
    @Override
    public JSONArray doAnalyze() {
        JSONArray result = new JSONArray();
        JSONObject jobDetailObject = new JSONObject();
        Elements jobBaseInfoElements = getDocument().getElementsByClass(CLASS_NAME_BASE_CONDITION);
        if (jobBaseInfoElements != null && jobBaseInfoElements.size() > 0) {
            Element jobBaseInfoElement = jobBaseInfoElements.first();
            if (jobBaseInfoElement != null) {
                Element element = jobBaseInfoElement.getElementsByClass(CLASS_NAME_COUNT).first();
                String count = element.html().replace("招", "").replace("人", "");
                jobDetailObject.put("count", count);
            }
        }
        result.add(jobDetailObject);
        return result;
    }

    @Override
    public int getCurrentPage() {
        return 1;
    }

    private static final String CLASS_NAME_BASE_CONDITION = "pos_base_condition";
    private static final String CLASS_NAME_COUNT = "pad_left_none";

    @Override
    public boolean needCaptcha(Document document) {
        return document.toString().contains(CAPTCHA_TEMPLATE);
    }

    @Override
    public String getCaptchaContentUrl(Document document) {
        return URL_CAPTCHA_DOMAIN +
                document.getElementById(ID_CAPTCHA_IMAGE_ELEMENT).attr("src").replace("..", "");
    }

    private static final String CAPTCHA_TEMPLATE = "访问过于频繁，本次访问需要输入验证码";
    private static final String ID_CAPTCHA_IMAGE_ELEMENT = "verify_img";
    private static final String URL_CAPTCHA_DOMAIN = "http://callback.58.com/firewall";

    public static void main(String[] args) {
        System.out.println(UrlContentGetter.getHtmlCode("http://callback.58.com/firewall/valid/1928850476.do?namespace=infodetailweb&url=http%3A%2F%2Fdongyang.58.com%2Ftech%2F24672055968069x.shtml%3Fpsid%3D106529422197166654350408539%26entinfo%3D24672055968069_b%26tjfrom%3Dpc_list_complement__108608834197166654352055536____%26finalCp%3D0009001300000000000000000000_108608834197166654352055536%26infoid%3D24672055968069%26ytdzwdetaildj%3D0", "utf-8"));
    }
}
