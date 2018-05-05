package com.doubeye.record.recognition.task.constant;

import com.doubeye.record.recognition.task.retrieve.TencentAsrResultSaver;

public class PropertyNameConstants {
    public enum PROPERTY_NAME {
        /**
         * 结果属性，用来在运行结果对象中保存获取到的电话记录，类型为JSONArray
         */
        RECORD("record"),
        /**
         * 取数据中录音时间开始条件，格式为YYYY-MM-DD
         */
        START_TIME("startTime"),
        /**
         * 最小通话时长，单位秒
         */
        MIN_CALL_LENGTH("minCallLength"),
        /**
         * 最大通话时长，单位秒
         */
        MAX_CALL_LENGTH("maxCallLength"),
        /**
         * 获得记录最大条数
         */
        RECORD_COUNT("recordCount"),
        /**
         * oss路径
         */
        OSS_PATH("oss_path"),

        /**
         * 阿里云oss路径前缀
         */
        OSS_PATH_PREFIX("ossPathPrefix"),
        /**
         * 上传成果列表在sharedResult中的保存属性值
         */
        UPLOADED("uploaded"),
        /**
         * 成功上传任务中的请求id的属性名
         */
        REQUEST_ID("requestId"),
        /**
         * 成功上传任务中的录音id的属性名
         */
        RECORD_ID("recordId"),
        /**
         * 提交结果的返回码
         */
        CODE("code"),
        /**
         * 模板共享结果保存获得的识别结果的属性名
         */
        RESULTS("results"),
        /**
         * 保存所有成果存储到数据库的请求ID
         */
        SAVED_REQUEST_IDS("savedRequestIds"),
        /**
         * 对话内容
         */
        CONVERSATION("text"),
        /**
         * 开始时间属性名
         */
        BEGIN_TIME("begin_time"),
        /**
         * 结束时间
         */
        END_TIME("end_time"),
        /**
         * 声道对应角色id
         */
        CHANNEL_ID("channel_id"),
        /**
         * 情感值
         */
        EMOTION_VALUE("emotion_value"),
        /**
         * 静音时长
         */
        SILENCE_DURATION("silence_duration"),
        /**
         * 语速
         */
        SPEECH_RATE("speech_rate"),
        /**
         * 识别服务appId
         */
        APP_ID("appid"),
        /**
         * 识别服务projectId
         */
        PROJECT_ID("projectid"),
        /**
         * 录音地址
         */
        AUDIO_URL("audioUrl"),
        /**
         * 录音时长
         */
        AUDIO_TIME("audioTime"),
        /**
         * 识别服务返回信息
         */
        MESSAGE("message"),
        /**
         * 返回结果中数据节点的属性
         */
        DATA("DATA"),
        /**
         * ES录音id属性名
         */
        ES_RECORD_ID("record_id"),
        /**
         * ES中标记记录存在腾讯识别结果的属性
         */
        ES_TENCENT_RECORD("tencent_record"),
        /**
         * ES保存腾讯云识别结果的属性
         */
        ES_TENCENT_RECORD_INFO("tencent_record_info"),
        ;
        private String propertyName;
        PROPERTY_NAME(String propertyName) {
            this.propertyName = propertyName;
        }

        public String getPropertyName() {
            return propertyName;
        }
        @Override
        public String toString() {
            return getPropertyName();
        }
    }
}
