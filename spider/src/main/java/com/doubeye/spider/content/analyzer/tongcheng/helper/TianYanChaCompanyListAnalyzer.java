package com.doubeye.spider.content.analyzer.tongcheng.helper;

import com.doubeye.commons.utils.net.UrlContentGetter;
import com.doubeye.commons.utils.net.UrlContentPhantomJsGetter;
import com.doubeye.spider.content.analyzer.AbstractContentAnalyzer;
import com.doubeye.spider.content.analyzer.ContentAnalyzer;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Map;

public class TianYanChaCompanyListAnalyzer extends AbstractContentAnalyzer{
    private String cookie;

    public void setCookie(String cookie) {
        this.cookie = cookie;
    }

    @Override
    public JSONArray doAnalyze() throws IOException {
        JSONArray result = new JSONArray();
        Elements companyElements = getDocument().getElementsByClass(CLASS_NAME_COMPANY_ROOT_ELEMENT);
        if (companyElements.size() > 0) {
            Element companyElement = companyElements.first();
            Elements companyUrlElements = companyElement.getElementsByClass(CLASS_NAME_COMPANY_PAGE_URL_ELEMENT);
            if (companyUrlElements.size() > 0) {
                Element companyUrlElement = companyUrlElements.first();
                String url = companyUrlElement.attr("href");
                JSONObject companyInfo = getCompanyInfo(url);
                result.add(companyInfo);
            }
        }
        return result;
    }

    private JSONObject getCompanyInfo(String url) throws IOException {
        //String cookie = "aliyungf_tc=AQAAABNM2y9KHAAALOz3ckcMlLfjnbUj; csrfToken=NY-bGj-ApG8BrXzjXo27a5aP; TYCID=39cd51008ef811e7a257f9305d665c22; uccid=50b4cf4ccc79d508d43a70acd0a633c8; ssuid=3992077791; jsid=SEM-BAIDU-JP-SY-000035; bannerFlag=true; tyc-user-info=%257B%2522token%2522%253A%2522eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxMzU4MTU3MjU0MyIsImlhdCI6MTUwNDg0OTUzOCwiZXhwIjoxNTIwNDAxNTM4fQ.Gs6ovGMlkwMiQJEd2667JJHXuZWEpJUlDZKNHdg6lj4rTXkFl2m-KKh5b7uSF7ZSKRLstrOlwZJIR7RYjwzhZA%2522%252C%2522integrity%2522%253A%25220%2525%2522%252C%2522state%2522%253A%25220%2522%252C%2522vnum%2522%253A%25220%2522%252C%2522onum%2522%253A%25220%2522%252C%2522mobile%2522%253A%252213581572543%2522%257D; auth_token=eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxMzU4MTU3MjU0MyIsImlhdCI6MTUwNDg0OTUzOCwiZXhwIjoxNTIwNDAxNTM4fQ.Gs6ovGMlkwMiQJEd2667JJHXuZWEpJUlDZKNHdg6lj4rTXkFl2m-KKh5b7uSF7ZSKRLstrOlwZJIR7RYjwzhZA; _csrf=sfjL2POzxmpwMFpW/9x/gg==; OA=LeaQu/qj8crONtTHBYNcloB1LTtEjFJXJlYzXA3eTX/Xkdv1uTDSy72IFVxhC4Dv; _csrf_bk=b8bfe68451da588c450943254b895535; Hm_lvt_e92c8d65d92d534b0fc290df538b4758=1504258287,1504606052,1504675396; Hm_lpvt_e92c8d65d92d534b0fc290df538b4758=1504852397; token=cb788c285c5646f99b6ac0a2459e4414; _utm=848447d7993042b2b539278806dd3cb6";
        //System.out.println(" in getCompanyInfo " + cookie);
        Map<String, String> header = UrlContentGetter.getTianYanChaHeader(cookie);
        String infoContent = UrlContentGetter.getHtml(url, header);
        ContentAnalyzer analyzer = new CompanyPageAnalyzer();
        analyzer.setContent(infoContent);
        JSONArray info = analyzer.doAnalyze();
        if (info.size() > 0) {
            return info.getJSONObject(0);
        } else {
            return new JSONObject();
        }
    }

    @Override
    public int getCurrentPage() {
        return 1;
    }

    /**
     * 结果中每个公司的根元素
     */
    private static final String CLASS_NAME_COMPANY_ROOT_ELEMENT = "search_result_single";
    private static final String CLASS_NAME_COMPANY_PAGE_URL_ELEMENT = "sv-search-company";

    /**
     * @author doubeye
     * @version 1.0.0
     * 天眼查公司页面分析器
     */
    class CompanyPageAnalyzer extends AbstractContentAnalyzer {

        @Override
        public JSONArray doAnalyze() throws IOException {
            JSONArray result = new JSONArray();
            JSONObject companyObject = new JSONObject();
            Elements companyRootElements = getDocument().getElementsByClass(CLASS_NAME_COMPANY_NAME_ROOT_ELEMENT);
            if (companyRootElements.size() > 0) {
                Element companyRootElement = companyRootElements.first();
                Elements companyNameElements = companyRootElement.getElementsByClass(CLASS_NAME_COMPANY_NAME_ELEMENT);
                if (companyNameElements.size() > 0) {
                    Element companyNameElement = companyNameElements.first();
                    companyObject.put("companyName", Jsoup.parse(companyNameElement.html()).text());
                }
            }
            Elements companyBaseInfoRootElements = getDocument().getElementsByClass(CLASS_NAME_COMPANY_BASE_INFO_ROOT_ELEMENT);
            if (companyBaseInfoRootElements.size() > 0) {
                Elements infoElements = companyBaseInfoRootElements.first().select("td.basic-td");
                String organizationCode = "", registrationNumber = "";
                for (Element element : infoElements) {
                    if (element.html().contains("组织机构代码：")) {
                        organizationCode = Jsoup.parse(element.getElementsByTag("span").html()).text();
                    }
                    if (element.html().contains("工商注册号：")) {
                        registrationNumber = Jsoup.parse(element.getElementsByTag("span").html()).text();
                    }
                }
                organizationCode = VALUE_NOT_PUBLIC.equals(organizationCode) ? registrationNumber : organizationCode;
                companyObject.put("organizationCode",
                        organizationCode);
            }
            result.add(companyObject);
            return result;
        }

        @Override
        public int getCurrentPage() {
            return 1;
        }

        private static final String CLASS_NAME_COMPANY_NAME_ROOT_ELEMENT = "companyTitleBox55";
        private static final String CLASS_NAME_COMPANY_NAME_ELEMENT = "f18 in-block vertival-middle";
        private static final String CLASS_NAME_COMPANY_BASE_INFO_ROOT_ELEMENT = "row b-c-white base2017";
        private static final String VALUE_NOT_PUBLIC = "未公开";


    }

    public static void main(String[] args) throws IOException, InterruptedException {
        doingTest();
    }

    private static final String FILE_NAME_TIANYANCHA_LOGIN = "D:/phantomjs-2.1.1-windows/code/tianYanChaLogin.js";
    //private static final String FILE_NAME_TIANYANCHA_LOGIN = "D:/phantomjs-2.1.1-windows/code/ccc.js";
    static final String CLASS_NAME_LOGIN_BUTTON = "modulein modulein1";


    static String login() throws IOException, InterruptedException {
        String loginResult = UrlContentPhantomJsGetter.executeCode(FILE_NAME_TIANYANCHA_LOGIN);
        //System.out.println(loginResult);
        JSONObject authToken = JSONObject.fromObject(loginResult);

        /*
        List<String> cookieNames = UrlContentGetter.TIANYANCHA_USED_COOKIE_NAMES;
        cookieNames.forEach(name -> {
            JSONObject cookie = getCookieEntry(loginCookies, name);
            if (cookie != null) {
                builder.append(cookie.getString("name")).append("=").append(cookie.getString("value")).append("; ");
            }
        });
        */

        String cookieString = authToken.getString("auth_token");
        //UrlContentGetter.setTianYanChaCookie("auth_token=" + cookieString);
        return "auth_token=" + cookieString;
    }

    private static JSONObject getCookieEntry(JSONArray allCookies, String cookieName) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < allCookies.size(); i ++) {
            JSONObject cookie = allCookies.getJSONObject(i);
            if (cookieName.contains("?")) {
                if (cookie.getString("name").contains(StringUtils.substringBefore(cookieName,"?"))) {
                    return cookie;
                }
            } else {
                if (cookieName.equals(cookie.getString("name"))) {
                    return cookie;
                }
            }
        }
        return null;
    }

    private static void doingTest() throws IOException, InterruptedException {
        try {
            Map<String, String> header = UrlContentGetter.getTianYanChaHeader("aliyungf_tc=AQAAABNM2y9KHAAALOz3ckcMlLfjnbUj; csrfToken=NY-bGj-ApG8BrXzjXo27a5aP; TYCID=39cd51008ef811e7a257f9305d665c22; uccid=50b4cf4ccc79d508d43a70acd0a633c8; ssuid=3992077791; jsid=SEM-BAIDU-JP-SY-000035; bannerFlag=true; token=597a93261e5b44cb80eacc4bcadf911a; _utm=c8e32498c0164ddbaa79d8262bb6f708; tyc-user-info=%257B%2522token%2522%253A%2522eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxMzU4MTU3MjU0MyIsImlhdCI6MTUwNTEyNzg3OSwiZXhwIjoxNTIwNjc5ODc5fQ.lw8Xod7Cw6pOrf0oI-SrGEqnGZyP8AgAzlwk9Cp3BNTXsN0_Bh21k0ap-TuXRezAvO5Fq-nR6pdCTJREyYt5UQ%2522%252C%2522integrity%2522%253A%25220%2525%2522%252C%2522state%2522%253A%25220%2522%252C%2522vnum%2522%253A%25220%2522%252C%2522onum%2522%253A%25220%2522%252C%2522mobile%2522%253A%252213581572543%2522%257D; auth_token=eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxMzU4MTU3MjU0MyIsImlhdCI6MTUwNTEyNzg3OSwiZXhwIjoxNTIwNjc5ODc5fQ.lw8Xod7Cw6pOrf0oI-SrGEqnGZyP8AgAzlwk9Cp3BNTXsN0_Bh21k0ap-TuXRezAvO5Fq-nR6pdCTJREyYt5UQ; OA=LeaQu/qj8crONtTHBYNcls4jXbw5H5IcUJJzSOZkvKU=; _csrf=n5RQuN2eI0pSFZR2ARopsA==; _csrf_bk=342b0e3d9ce3bf99a4853fbd4be081e9; Hm_lvt_e92c8d65d92d534b0fc290df538b4758=1504258287,1504606052,1504675396; Hm_lpvt_e92c8d65d92d534b0fc290df538b4758=1505127930");
            Document doc = Jsoup.connect("https://www.tianyancha.com/search?key=Klein&checkFrom=searchBox&rnd=").headers(header).timeout(120000).followRedirects(true).execute().parse();
            System.out.println(doc.location());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    /*
    天眼查人机验证相关信息
    method get
    url:https://antirobot.tianyancha.com/captcha/checkCaptcha.json?captchaId=5e6b2013-1855-496e-a4a2-5664a5a3e793&clickLocs=[{%22x%22:281,%22y%22:54},{%22x%22:248,%22y%22:43},{%22x%22:54,%22y%22:51},{%22x%22:133,%22y%22:42}]&t=1505116799882&_=1505116787587
    仍然需要anth_token
    参数：
    captchaId:5e6b2013-1855-496e-a4a2-5664a5a3e793
    clickLocs:[{"x":281,"y":54},{"x":248,"y":43},{"x":54,"y":51},{"x":133,"y":42}]
    t:1505116799882
    _:1505116787587
     */
}
