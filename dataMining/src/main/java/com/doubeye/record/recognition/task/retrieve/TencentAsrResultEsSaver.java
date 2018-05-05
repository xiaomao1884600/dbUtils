package com.doubeye.record.recognition.task.retrieve;

import com.doubeye.commons.utils.elasticsearch.*;
import com.doubeye.core.opration.template.AbstractOperation;
import com.doubeye.core.opration.template.Operation;
import com.doubeye.record.recognition.task.constant.PropertyNameConstants;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * @author doubeye
 * 保存腾讯能识别结果，该类直接依赖与TencentAsrResultFormatter,
 * 该类读取模板中的results属性，并将其中的识别结果保存到数据表中（record_analyze_tencent）
 * 该类将所有保存成功的请求id保存在模板共享结果集中的savedRequestIds属性，方便后续处理
 * @see TencentAsrResultFormatter
 */
@SuppressWarnings("unused | WeakerAccess")
public class TencentAsrResultEsSaver extends AbstractOperation{
    /**
     * ES客户端助手
     */
    private DocumentHelper documentHelper;
    private CustomSearchHelper searchHelper;
    private static final String[] INDEX_NAMES = {"record_alias_heat", "record_alias_cold"};
    @Override
    public void run() {
        Connection conn = getConnection();
        List<String> requestIds = new ArrayList<>();
        JSONObject sharedResult = getSharedResult();
        JSONArray results = sharedResult.getJSONArray(PropertyNameConstants.PROPERTY_NAME.RESULTS.toString());
        for (int i = 0; i < results.size(); i ++) {
            try {
                JSONObject record = results.getJSONObject(i);
                if (record.containsKey(PropertyNameConstants.PROPERTY_NAME.RECORD_ID.toString())) {
                    String recordId = record.getString(PropertyNameConstants.PROPERTY_NAME.RECORD_ID.toString());
                    String requestId = record.getString(PropertyNameConstants.PROPERTY_NAME.REQUEST_ID.toString());
                    JSONObject content = getContentObject(record);
                    mergeIndex(recordId, content);
                    requestIds.add(requestId);
                }
            } catch (InterruptedException | ExecutionException | IOException e) {
                //todo log SQL Exception
            }

        }
        sharedResult.put(PropertyNameConstants.PROPERTY_NAME.SAVED_REQUEST_IDS.toString(),
                JSONArray.fromObject(requestIds));
        System.out.println(requestIds);
    }

    private static JSONObject getContentObject(JSONObject source) {
        JSONObject result = new JSONObject();
        if (source.containsKey(PropertyNameConstants.PROPERTY_NAME.CONVERSATION.toString())) {
            result.put(PropertyNameConstants.PROPERTY_NAME.ES_TENCENT_RECORD.toString(), 1);
            result.put(PropertyNameConstants.PROPERTY_NAME.ES_TENCENT_RECORD_INFO.toString(),
                    source.getJSONArray(PropertyNameConstants.PROPERTY_NAME.CONVERSATION.toString()));
        }
        return result;
    }

    private void mergeIndex(String recordId, JSONObject content) throws InterruptedException, ExecutionException, IOException {
        for (String indexName :INDEX_NAMES) {
            searchHelper.setIndexName(indexName);
            searchHelper.addMatchPhase(PropertyNameConstants.PROPERTY_NAME.ES_RECORD_ID.toString(), recordId);
            JSONArray hits = searchHelper.getSearchResult(searchHelper.getQueryBuilder());
            if (hits.size() > 0) {
                documentHelper.setIndexName(indexName);
                documentHelper.mergeDocuments(recordId, content);
            }
        }
    }

    public DocumentHelper getDocumentHelper() {
        return documentHelper;
    }

    public void setDocumentHelper(DocumentHelper documentHelper) {
        this.documentHelper = documentHelper;
    }

    public CustomSearchHelper getSearchHelper() {
        return searchHelper;
    }

    public void setSearchHelper(CustomSearchHelper searchHelper) {
        this.searchHelper = searchHelper;
    }

    public static Operation getInstance(DocumentHelper documentHelper) {
        TencentAsrResultEsSaver instance = new TencentAsrResultEsSaver();
        instance.setDocumentHelper(documentHelper);
        CustomSearchHelper searchHelper = new CustomSearchHelper();
        searchHelper.setClient(documentHelper.getIndexHelper().getClient());
        searchHelper.setTypeName(documentHelper.getTypeName());
        instance.setSearchHelper(searchHelper);
        return instance;
    }

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
        ClientHelper clientHelper = new ClientHelper();
        try {
            clientHelper.setClusterName("hxsd-bd");
            clientHelper.addNode("10.2.24.57:9300");
            IndexHelper indexHelper = new IndexHelper();
            indexHelper.setClient(clientHelper.getClient());
            DocumentHelper documentHelper = new DocumentHelper();
            documentHelper.setTypeName("analyze");
            documentHelper.setIndexHelper(indexHelper);
            TencentAsrResultEsSaver instance = (TencentAsrResultEsSaver) getInstance(documentHelper);
            String id = "FF9D4B7C-79EF-7770-8203-587DC666C8F9";
            JSONObject content = JSONObject.fromObject("{\"text\":[{\"begin_time\":1430,\"end_time\":6980,\"channel_id\":0,\"emotion_value\":0,\"silence_duration\":0,\"speech_rate\":0,\"text\":\" 哎你好我是那个火星时代网校需要时间跟你联系过有印象吧\"},{\"begin_time\":6980,\"end_time\":8860,\"channel_id\":1,\"emotion_value\":0,\"silence_duration\":0,\"speech_rate\":0,\"text\":\" 哦哦\"},{\"begin_time\":8860,\"end_time\":12915,\"channel_id\":0,\"emotion_value\":0,\"silence_duration\":0,\"speech_rate\":0,\"text\":\" 噢上次邀约您看那只播客不是有没有看了呀\"},{\"begin_time\":12915,\"end_time\":15340,\"channel_id\":1,\"emotion_value\":0,\"silence_duration\":0,\"speech_rate\":0,\"text\":\" 嗯嗯\"},{\"begin_time\":15340,\"end_time\":16840,\"channel_id\":0,\"emotion_value\":0,\"silence_duration\":0,\"speech_rate\":0,\"text\":\" 看了是吧\"},{\"begin_time\":16840,\"end_time\":20270,\"channel_id\":0,\"emotion_value\":0,\"silence_duration\":0,\"speech_rate\":0,\"text\":\" 呃呃感觉怎么样呢\"},{\"begin_time\":20270,\"end_time\":24030,\"channel_id\":1,\"emotion_value\":0,\"silence_duration\":0,\"speech_rate\":0,\"text\":\" 呃呃呃呃还好吗\"},{\"begin_time\":24030,\"end_time\":28955,\"channel_id\":0,\"emotion_value\":0,\"silence_duration\":0,\"speech_rate\":0,\"text\":\" 哦行那您就是有没有遇到一些问题或者听不明白的地方呢\"},{\"begin_time\":28955,\"end_time\":33400,\"channel_id\":1,\"emotion_value\":0,\"silence_duration\":0,\"speech_rate\":0,\"text\":\" 嗯嗯嗯清风吧\"},{\"begin_time\":33400,\"end_time\":41270,\"channel_id\":0,\"emotion_value\":0,\"silence_duration\":0,\"speech_rate\":0,\"text\":\" 他们听得懂是吧那您就觉得您这个就说评价方面的地方有提升的一个帮助吗\"},{\"begin_time\":41270,\"end_time\":47760,\"channel_id\":1,\"emotion_value\":0,\"silence_duration\":0,\"speech_rate\":0,\"text\":\" 哦我只是说看一下吧你干啥呢\"},{\"begin_time\":47760,\"end_time\":51370,\"channel_id\":1,\"emotion_value\":0,\"silence_duration\":0,\"speech_rate\":0,\"text\":\" 感觉好像不太知道吧\"},{\"begin_time\":51370,\"end_time\":54830,\"channel_id\":0,\"emotion_value\":0,\"silence_duration\":0,\"speech_rate\":0,\"text\":\" 你是想了解哪一块是平面广告是吗\"},{\"begin_time\":54830,\"end_time\":57825,\"channel_id\":1,\"emotion_value\":0,\"silence_duration\":0,\"speech_rate\":0,\"text\":\" 哦是\"},{\"begin_time\":57825,\"end_time\":77539,\"channel_id\":0,\"emotion_value\":0,\"silence_duration\":0,\"speech_rate\":0,\"text\":\" 嗯咱们之前是沟通的您是做平面儿设计方面的工作对吧呃我们这边地平线的话他有些悲凉的一个是平面广告像这个书籍装帧画册海报易拉宝这种一个呢是像咱们的就是呃电脑端的就网页呀还有就是app端呀还有界面设计的\"},{\"begin_time\":77539,\"end_time\":79350,\"channel_id\":1,\"emotion_value\":0,\"silence_duration\":0,\"speech_rate\":0,\"text\":\" 对对对\"},{\"begin_time\":79350,\"end_time\":81375,\"channel_id\":0,\"emotion_value\":0,\"silence_duration\":0,\"speech_rate\":0,\"text\":\" 也是就偏向是哪一块呢\"},{\"begin_time\":81375,\"end_time\":84310,\"channel_id\":1,\"emotion_value\":0,\"silence_duration\":0,\"speech_rate\":0,\"text\":\" 哦嘿嘿网\"},{\"begin_time\":84310,\"end_time\":87140,\"channel_id\":1,\"emotion_value\":0,\"silence_duration\":0,\"speech_rate\":0,\"text\":\" 是不是电话号码\"},{\"begin_time\":87140,\"end_time\":89630,\"channel_id\":0,\"emotion_value\":0,\"silence_duration\":0,\"speech_rate\":0,\"text\":\" 呃没听清呵呵\"},{\"begin_time\":89630,\"end_time\":92466,\"channel_id\":1,\"emotion_value\":0,\"silence_duration\":0,\"speech_rate\":0,\"text\":\" 现在做电商方面的\"},{\"begin_time\":92466,\"end_time\":95290,\"channel_id\":0,\"emotion_value\":0,\"silence_duration\":0,\"speech_rate\":0,\"text\":\" 伤伤伤测试嘛\"},{\"begin_time\":95290,\"end_time\":98010,\"channel_id\":1,\"emotion_value\":0,\"silence_duration\":0,\"speech_rate\":0,\"text\":\" 这个三好学生啊\"},{\"begin_time\":98010,\"end_time\":123909,\"channel_id\":0,\"emotion_value\":0,\"silence_duration\":0,\"speech_rate\":0,\"text\":\" 哦电商方向ok我理解啊那天发的话电视上放到油箱情愿呀专题呀这种啊因为你看的那个他肯定是没有讲到这一块儿因为我们这边也会有一个就是专题二厢情愿的一个课程就是您正好那天的事没有对到呃应该在下周也会有可能会安排就是强调啊因为我不知道上次有没有就在你qq微信把这个电商这方面只不过链接给您发过去\"},{\"begin_time\":123909,\"end_time\":125620,\"channel_id\":1,\"emotion_value\":0,\"silence_duration\":0,\"speech_rate\":0,\"text\":\" 没有\"},{\"begin_time\":125620,\"end_time\":127800,\"channel_id\":0,\"emotion_value\":0,\"silence_duration\":0,\"speech_rate\":0,\"text\":\" 把那个课程介绍的链接\"},{\"begin_time\":127800,\"end_time\":129690,\"channel_id\":1,\"emotion_value\":0,\"silence_duration\":0,\"speech_rate\":0,\"text\":\" 哎哎\"},{\"begin_time\":129690,\"end_time\":130330,\"channel_id\":0,\"emotion_value\":0,\"silence_duration\":0,\"speech_rate\":0,\"text\":\" 没有事\"},{\"begin_time\":130330,\"end_time\":131990,\"channel_id\":1,\"emotion_value\":0,\"silence_duration\":0,\"speech_rate\":0,\"text\":\" 嗯嗯\"},{\"begin_time\":131990,\"end_time\":134805,\"channel_id\":0,\"emotion_value\":0,\"silence_duration\":0,\"speech_rate\":0,\"text\":\" 您这是谁的手机号了吧对吧\"},{\"begin_time\":134805,\"end_time\":137320,\"channel_id\":0,\"emotion_value\":0,\"silence_duration\":0,\"speech_rate\":0,\"text\":\" 啊您这微信是这个手机好了吧\"},{\"begin_time\":137320,\"end_time\":139920,\"channel_id\":1,\"emotion_value\":0,\"silence_duration\":0,\"speech_rate\":0,\"text\":\" 哦哦\"},{\"begin_time\":139920,\"end_time\":146220,\"channel_id\":0,\"emotion_value\":0,\"silence_duration\":0,\"speech_rate\":0,\"text\":\" 记得当时是有加你微信我看下有没有发现咱们这边你现在是在上班吗\"},{\"begin_time\":146220,\"end_time\":149810,\"channel_id\":0,\"emotion_value\":0,\"silence_duration\":0,\"speech_rate\":0,\"text\":\" 吧电脑方便吧\"},{\"begin_time\":149810,\"end_time\":154690,\"channel_id\":1,\"emotion_value\":0,\"silence_duration\":0,\"speech_rate\":0,\"text\":\" 嗯嗯嗯嗯嗯不错\"},{\"begin_time\":154690,\"end_time\":156010,\"channel_id\":0,\"emotion_value\":0,\"silence_duration\":0,\"speech_rate\":0,\"text\":\" 你在电脑旁边\"},{\"begin_time\":156010,\"end_time\":157880,\"channel_id\":1,\"emotion_value\":0,\"silence_duration\":0,\"speech_rate\":0,\"text\":\" 啊哦\"},{\"begin_time\":157880,\"end_time\":163675,\"channel_id\":0,\"emotion_value\":0,\"silence_duration\":0,\"speech_rate\":0,\"text\":\" 那我就是把那个呃那你电脑上应该有这个电脑版的微信\"},{\"begin_time\":163675,\"end_time\":167120,\"channel_id\":1,\"emotion_value\":0,\"silence_duration\":0,\"speech_rate\":0,\"text\":\" 噢六八哦\"},{\"begin_time\":167120,\"end_time\":177955,\"channel_id\":0,\"emotion_value\":0,\"silence_duration\":0,\"speech_rate\":0,\"text\":\" 行那我直接给你发过去你可以就打开看看然后给详细的说一下这个课程他叫就是高端电商视觉设计师\"},{\"begin_time\":177955,\"end_time\":182445,\"channel_id\":0,\"emotion_value\":0,\"silence_duration\":0,\"speech_rate\":0,\"text\":\" 因为这个电商他也是基于频发的基础上\"},{\"begin_time\":182445,\"end_time\":185955,\"channel_id\":0,\"emotion_value\":0,\"silence_duration\":0,\"speech_rate\":0,\"text\":\" 但有打开吗你可以稍后打开一下\"},{\"begin_time\":185955,\"end_time\":190000,\"channel_id\":1,\"emotion_value\":0,\"silence_duration\":0,\"speech_rate\":0,\"text\":\" 呃呃呃玩玩嘿嘿呃我哇\"},{\"begin_time\":190000,\"end_time\":190970,\"channel_id\":0,\"emotion_value\":0,\"silence_duration\":0,\"speech_rate\":0,\"text\":\" 哦晚上回去是吧\"},{\"begin_time\":190970,\"end_time\":194870,\"channel_id\":1,\"emotion_value\":0,\"silence_duration\":0,\"speech_rate\":0,\"text\":\" 有对那方面是吗呃呃\"},{\"begin_time\":194870,\"end_time\":214099,\"channel_id\":0,\"emotion_value\":0,\"silence_duration\":0,\"speech_rate\":0,\"text\":\" okok行理解理解然后这样就是刚才把这个人介绍给您发了今天还是有这个图标这可能你还要去看吗因为长得还不是就是说电商这一块因为你要向电商的话他就是有偏向性的了咱们这个左右的这个电商啊还有就是见面他都是基于在平面的基础上然后在星梦一个就是\"},{\"begin_time\":214099,\"end_time\":217180,\"channel_id\":1,\"emotion_value\":0,\"silence_duration\":0,\"speech_rate\":0,\"text\":\" 哦哦\"},{\"begin_time\":217180,\"end_time\":220990,\"channel_id\":0,\"emotion_value\":0,\"silence_duration\":0,\"speech_rate\":0,\"text\":\" 就这种那您今天晚上还要去看看吗\"},{\"begin_time\":220990,\"end_time\":223950,\"channel_id\":1,\"emotion_value\":0,\"silence_duration\":0,\"speech_rate\":0,\"text\":\" 哦今天有事啊\"},{\"begin_time\":223950,\"end_time\":225950,\"channel_id\":0,\"emotion_value\":0,\"silence_duration\":0,\"speech_rate\":0,\"text\":\" 哦今天有时间就去看看是吧\"},{\"begin_time\":225950,\"end_time\":227530,\"channel_id\":1,\"emotion_value\":0,\"silence_duration\":0,\"speech_rate\":0,\"text\":\" 嗯嗯\"},{\"begin_time\":227530,\"end_time\":241869,\"channel_id\":0,\"emotion_value\":0,\"silence_duration\":0,\"speech_rate\":0,\"text\":\" 行那当然啦我就在把这边房子不和链接给您发一下大家看看呃把那个如果有问题的话您就是说如果想了解电商这一块也可以就在这只不过到时候去跟老师互动的这个您知道吧嗯\"},{\"begin_time\":241869,\"end_time\":259139,\"channel_id\":0,\"emotion_value\":0,\"silence_duration\":0,\"speech_rate\":0,\"text\":\" 您到时候就可以去直接去互动然后呢有问题可以直飞的去交流啊沟通然后包括就是说咱们打进了一些问题或者说你就问一下老师咱们这边什么时候这个会有电商直播的这块什么时候有这个直播是吧然后到时候可以就及时通知给你\"},{\"begin_time\":259139,\"end_time\":265730,\"channel_id\":0,\"emotion_value\":0,\"silence_duration\":0,\"speech_rate\":0,\"text\":\" 然后呢咱们这边还有一个学习交流权的那个知道吗另外的一个要求全嗯\"},{\"begin_time\":265730,\"end_time\":268190,\"channel_id\":1,\"emotion_value\":0,\"silence_duration\":0,\"speech_rate\":0,\"text\":\" 知道啥啊\"},{\"begin_time\":268190,\"end_time\":269970,\"channel_id\":0,\"emotion_value\":0,\"silence_duration\":0,\"speech_rate\":0,\"text\":\" 已经加了是吗\"},{\"begin_time\":269970,\"end_time\":284539,\"channel_id\":0,\"emotion_value\":0,\"silence_duration\":0,\"speech_rate\":0,\"text\":\" 嗯嗯你要有问题的话你也可以在群里边提问包括正在全力为你说大概什么时间会有这个关于电商这方面直播都可以去问的行那是这样啊有问题的话咱那个网上看直播有问题的都是联系好吧\"},{\"begin_time\":284539,\"end_time\":286120,\"channel_id\":1,\"emotion_value\":0,\"silence_duration\":0,\"speech_rate\":0,\"text\":\" 哦行行\"},{\"begin_time\":286120,\"end_time\":288720,\"channel_id\":0,\"emotion_value\":0,\"silence_duration\":0,\"speech_rate\":0,\"text\":\" 哎好那先这样那你先忙嗯好拜拜\"},{\"begin_time\":288720,\"end_time\":291257,\"channel_id\":1,\"emotion_value\":0,\"silence_duration\":0,\"speech_rate\":0,\"text\":\" 嗯拜拜\"}]}");
            instance.mergeIndex(id, content);
        } finally {
            clientHelper.getClient().close();
        }

    }
}
