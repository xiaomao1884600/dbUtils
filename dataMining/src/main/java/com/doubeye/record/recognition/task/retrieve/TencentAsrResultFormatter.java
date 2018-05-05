package com.doubeye.record.recognition.task.retrieve;

import com.doubeye.commons.utils.constant.CommonConstant;
import com.doubeye.core.opration.template.AbstractOperation;
import com.doubeye.core.opration.template.Operation;
import com.doubeye.record.recognition.task.constant.PropertyNameConstants;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;


/**
 * @author doubeye
 * 将腾讯云的ASR识别结果转换为阿里云ASR的识别结果格式，
 * 该类从模板的共享结果中的records属性取得要处理的结果集，并用处理后的结果集替换原来的结果集
 */
public class TencentAsrResultFormatter extends AbstractOperation{
    @Override
    public void run() {
        JSONObject sharedResult = getSharedResult();
        if (sharedResult.containsKey(PropertyNameConstants.PROPERTY_NAME.RESULTS.toString())) {
            JSONArray records = getSharedResult().getJSONArray(PropertyNameConstants.PROPERTY_NAME.RESULTS.toString());
            JSONArray formattedRecords = new JSONArray();
            for (int i = 0; i < records.size(); i ++)  {
                formattedRecords.add(doFormat(records.getJSONObject(i)));
            }
            sharedResult.put(PropertyNameConstants.PROPERTY_NAME.RESULTS.toString(), formattedRecords);
        }
    }

    /**
     * 对话处理
     * @param record 对话的JSON对象
     * @return 格式化好的一通电话对话
     * 对话中每句话以\n分隔，每句话遵守以下格式（其中_实际为空格）
     * [开始时间,结束时间,声道编号]__对话文本
     * 示例：
     * [0:1.330,0:4.120,0]  喂你好崔勇
     * 开始时间与结束时间m:s.ms 即 分钟:秒:毫秒，阿里云开始时间和结束时间为毫秒，因此需要做一次计算 m * 60 * 1000 + s.ms * 1000
     * 腾讯云与阿里云识别结果相比，还缺少以下内容，可以暂时统一补0
     * emotion_value 情绪值
     * silence_duration 静音时间
     * speech_rate 语速
     */
    private JSONObject doFormat(JSONObject record) {
        if (record.containsKey(PropertyNameConstants.PROPERTY_NAME.CONVERSATION.toString())) {
            String conversation = record.getString(PropertyNameConstants.PROPERTY_NAME.CONVERSATION.toString());
            JSONArray formattedConversation = new JSONArray();

            String[] splitedConversation = conversation.split(SEPARATOR.CONVERSATION_SEPARATOR.toString());
            for (String entry : splitedConversation) {
                formattedConversation.add(doFormatSentence(entry));
            }
            record.put(PropertyNameConstants.PROPERTY_NAME.CONVERSATION.toString(), formattedConversation);
        } else {
            System.out.println("RESULT IS MALFORMED");
            System.out.println(record.toString());
        }
        return record;
    }


    private JSONObject doFormatSentence(String sentence) {
        String timePart = StringUtils.substringBefore(sentence, SEPARATOR.TIME_TEXT.toString());
        String content = StringUtils.substringAfter(sentence, SEPARATOR.TIME_TEXT.toString());
        JSONObject result = doFormatTimePart(timePart);
        result.put(PropertyNameConstants.PROPERTY_NAME.CONVERSATION.toString(), content);
        return result;
    }

    private JSONObject doFormatTimePart(String source) {
        JSONObject timePart = new JSONObject();
        source = StringUtils.replace(source,
                CommonConstant.SEPARATOR.LEFT_BRACKET.toString(), "").replace(
                        CommonConstant.SEPARATOR.RIGHT_BRACKET.toString(), "");
        String[] parts = StringUtils.split(source, CommonConstant.SEPARATOR.COMMA.toString());
        if (parts.length == 3) {
            double startTime = doFormatTime(parts[0]);
            double endTime = doFormatTime(parts[1]);
            int channelId = Integer.parseInt(parts[2]);
            timePart.put(PropertyNameConstants.PROPERTY_NAME.BEGIN_TIME.toString(), startTime);
            timePart.put(PropertyNameConstants.PROPERTY_NAME.END_TIME.toString(), endTime);
            timePart.put(PropertyNameConstants.PROPERTY_NAME.CHANNEL_ID.toString(), channelId);
            timePart.put(PropertyNameConstants.PROPERTY_NAME.EMOTION_VALUE.toString(), 0);
            timePart.put(PropertyNameConstants.PROPERTY_NAME.SILENCE_DURATION.toString(), 0);
            timePart.put(PropertyNameConstants.PROPERTY_NAME.SPEECH_RATE.toString(), 0);
        } else {
            //TODO log malformation time part
        }
        return timePart;
    }

    private double doFormatTime(String time) {
        String[] splited = StringUtils.split(time, CommonConstant.SEPARATOR.COLON.toString());
        if (splited.length == 2) {
            return Long.parseLong(splited[0]) * 1000D * 60D + Math.ceil(Double.parseDouble(splited[1]) * 1000D);
        }  else {
            //TODO log malformation time part
            return 0;
        }
    }


    /**
     * 各种分隔符
     */
    public enum SEPARATOR {
        /**
         * 模板共享结果保存获得的识别结果的属性名
         */
        CONVERSATION_SEPARATOR("\n"),
        /**
         * 录音识别结果中对话属性名
         */
        TIME_TEXT("] "),
        ;
        private String separator;
        SEPARATOR(String propertyName) {
            this.separator = propertyName;
        }

        public String getSeparator() {
            return separator;
        }
        @Override
        public String toString() {
            return getSeparator();
        }
    }

    public static Operation getInstance() {
        return new TencentAsrResultFormatter();
    }

    public static void main(String[] args) {
        JSONArray testObject = JSONArray.fromObject("[{\"code\":\"0\",\"requestId\":\"98768370\",\"appid\":\"1255612177\",\"projectid\":\"1091843\",\"text\":\"[0:1.430,0:6.980,0]  哎你好我是那个火星时代网校需要时间跟你联系过有印象吧\\n[0:6.980,0:8.860,1]  哦哦\\n[0:8.860,0:12.915,0]  噢上次邀约您看那只播客不是有没有看了呀\\n[0:12.915,0:15.340,1]  嗯嗯\\n[0:15.340,0:16.840,0]  看了是吧\\n[0:16.840,0:20.270,0]  呃呃感觉怎么样呢\\n[0:20.270,0:24.030,1]  呃呃呃呃还好吗\\n[0:24.030,0:28.955,0]  哦行那您就是有没有遇到一些问题或者听不明白的地方呢\\n[0:28.955,0:33.400,1]  嗯嗯嗯清风吧\\n[0:33.400,0:41.270,0]  他们听得懂是吧那您就觉得您这个就说评价方面的地方有提升的一个帮助吗\\n[0:41.270,0:47.760,1]  哦我只是说看一下吧你干啥呢\\n[0:47.760,0:51.370,1]  感觉好像不太知道吧\\n[0:51.370,0:54.830,0]  你是想了解哪一块是平面广告是吗\\n[0:54.830,0:57.825,1]  哦是\\n[0:57.825,1:17.539,0]  嗯咱们之前是沟通的您是做平面儿设计方面的工作对吧呃我们这边地平线的话他有些悲凉的一个是平面广告像这个书籍装帧画册海报易拉宝这种一个呢是像咱们的就是呃电脑端的就网页呀还有就是app端呀还有界面设计的\\n[1:17.539,1:19.350,1]  对对对\\n[1:19.350,1:21.375,0]  也是就偏向是哪一块呢\\n[1:21.375,1:24.310,1]  哦嘿嘿网\\n[1:24.310,1:27.140,1]  是不是电话号码\\n[1:27.140,1:29.630,0]  呃没听清呵呵\\n[1:29.630,1:32.465,1]  现在做电商方面的\\n[1:32.465,1:35.290,0]  伤伤伤测试嘛\\n[1:35.290,1:38.010,1]  这个三好学生啊\\n[1:38.010,2:3.909,0]  哦电商方向ok我理解啊那天发的话电视上放到油箱情愿呀专题呀这种啊因为你看的那个他肯定是没有讲到这一块儿因为我们这边也会有一个就是专题二厢情愿的一个课程就是您正好那天的事没有对到呃应该在下周也会有可能会安排就是强调啊因为我不知道上次有没有就在你qq微信把这个电商这方面只不过链接给您发过去\\n[2:3.909,2:5.620,1]  没有\\n[2:5.620,2:7.800,0]  把那个课程介绍的链接\\n[2:7.800,2:9.690,1]  哎哎\\n[2:9.690,2:10.330,0]  没有事\\n[2:10.330,2:11.990,1]  嗯嗯\\n[2:11.990,2:14.805,0]  您这是谁的手机号了吧对吧\\n[2:14.805,2:17.320,0]  啊您这微信是这个手机好了吧\\n[2:17.320,2:19.920,1]  哦哦\\n[2:19.920,2:26.220,0]  记得当时是有加你微信我看下有没有发现咱们这边你现在是在上班吗\\n[2:26.220,2:29.810,0]  吧电脑方便吧\\n[2:29.810,2:34.690,1]  嗯嗯嗯嗯嗯不错\\n[2:34.690,2:36.010,0]  你在电脑旁边\\n[2:36.010,2:37.880,1]  啊哦\\n[2:37.880,2:43.675,0]  那我就是把那个呃那你电脑上应该有这个电脑版的微信\\n[2:43.675,2:47.120,1]  噢六八哦\\n[2:47.120,2:57.955,0]  行那我直接给你发过去你可以就打开看看然后给详细的说一下这个课程他叫就是高端电商视觉设计师\\n[2:57.955,3:2.445,0]  因为这个电商他也是基于频发的基础上\\n[3:2.445,3:5.955,0]  但有打开吗你可以稍后打开一下\\n[3:5.955,3:10.000,1]  呃呃呃玩玩嘿嘿呃我哇\\n[3:10.000,3:10.970,0]  哦晚上回去是吧\\n[3:10.970,3:14.870,1]  有对那方面是吗呃呃\\n[3:14.870,3:34.099,0]  okok行理解理解然后这样就是刚才把这个人介绍给您发了今天还是有这个图标这可能你还要去看吗因为长得还不是就是说电商这一块因为你要向电商的话他就是有偏向性的了咱们这个左右的这个电商啊还有就是见面他都是基于在平面的基础上然后在星梦一个就是\\n[3:34.099,3:37.180,1]  哦哦\\n[3:37.180,3:40.990,0]  就这种那您今天晚上还要去看看吗\\n[3:40.990,3:43.950,1]  哦今天有事啊\\n[3:43.950,3:45.950,0]  哦今天有时间就去看看是吧\\n[3:45.950,3:47.530,1]  嗯嗯\\n[3:47.530,4:1.869,0]  行那当然啦我就在把这边房子不和链接给您发一下大家看看呃把那个如果有问题的话您就是说如果想了解电商这一块也可以就在这只不过到时候去跟老师互动的这个您知道吧嗯\\n[4:1.869,4:19.139,0]  您到时候就可以去直接去互动然后呢有问题可以直飞的去交流啊沟通然后包括就是说咱们打进了一些问题或者说你就问一下老师咱们这边什么时候这个会有电商直播的这块什么时候有这个直播是吧然后到时候可以就及时通知给你\\n[4:19.139,4:25.730,0]  然后呢咱们这边还有一个学习交流权的那个知道吗另外的一个要求全嗯\\n[4:25.730,4:28.190,1]  知道啥啊\\n[4:28.190,4:29.970,0]  已经加了是吗\\n[4:29.970,4:44.539,0]  嗯嗯你要有问题的话你也可以在群里边提问包括正在全力为你说大概什么时间会有这个关于电商这方面直播都可以去问的行那是这样啊有问题的话咱那个网上看直播有问题的都是联系好吧\\n[4:44.539,4:46.120,1]  哦行行\\n[4:46.120,4:48.720,0]  哎好那先这样那你先忙嗯好拜拜\\n[4:48.720,4:51.257,1]  嗯拜拜\\n\",\"audioUrl\":\"http://hxsd-backup.oss-cn-beijing.aliyuncs.com/recordAnalyze/2018/01/18/bei_jing/20180118151555-Outbound-E1Trunk1-3319-018262885076.wav\",\"audioTime\":\"292.845000\",\"message\":\"成功\"}]");
        TencentAsrResultFormatter formatter = new TencentAsrResultFormatter();
        formatter.getSharedResult().put(PropertyNameConstants.PROPERTY_NAME.RESULTS.toString(), testObject.toString());
        formatter.run();
        System.out.println(formatter.getSharedResult().toString());
    }

}
