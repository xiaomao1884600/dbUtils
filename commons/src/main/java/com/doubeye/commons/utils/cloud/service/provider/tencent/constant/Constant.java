package com.doubeye.commons.utils.cloud.service.provider.tencent.constant;

/**
 * @author doubeye
 * 腾讯云所使用的常量对象
 */
public class Constant {
    /**
     * 语音数据来源
     */
    public enum SOURCE_TYPE {
        /**
         * 语音 URL
         */
        THROUGH_URL(0),
        /**
         * 语音数据（post body）
         */
        THROUGH_POST_BODY(1),
        ;
        private int type;
        SOURCE_TYPE(int type) {
            this.type = type;
        }

        public int getType() {
            return type;
        }
        @Override
        public String toString() {
            return getType() + "";
        }
    }

    /**
     * 	结果返回方式
     */
    public enum RESULT_TYPE {
        /**
         * 同步
         */
        SYNCHRONOUS(0),
        /**
         * 异步
         */
        ASYNCHRONOUS(1),
        ;
        private int type;
        RESULT_TYPE(int type) {
            this.type = type;
        }

        public int getType() {
            return type;
        }
        @Override
        public String toString() {
            return getType() + "";
        }
    }

    /**
     * 识别结果文本编码方式
     */
    public enum RESULT_TEXT_FORMAT {
        /**
         * UTF-8
         */
        UTF_8(0),
        /**
         * GB2312
         */
        GB2312(1),
        /**
         * GBK
         */
        GBK(2),
        /**
         * BIG5
         */
        BIG5(3),
        ;
        private int charset;
        RESULT_TEXT_FORMAT(int charset) {
            this.charset = charset;
        }

        public int getCharset() {
            return charset;
        }
        @Override
        public String toString() {
            return getCharset() + "";
        }
    }

    /**
     * 识别引擎类型
     */
    public enum ENGINE_MODEL_TYPE {
        /**
         * 电话 8k 通用模型
         */
        TYPE_8K("8k_0"),
        /**
         * 电话 8k 通用分角色
         */
        TYPE_8K_6("8k_6"),
        /**
         * 16k 通用模型
         */
        TYPE_16K("16k_0"),
        ;
        private String type;
        ENGINE_MODEL_TYPE(String type) {
            this.type = type;
        }

        public String getType() {
            return type;
        }
        @Override
        public String toString() {
            return getType();
        }
    }

    /**
     * 提交服务类型枚举
     */
    public enum SUB_SERVICE_TYPE {
        /**
         * 离线语音识别
         */
        OFFLINE(0),
        /**
         * 实时流式识别
         */
        REAL_TIME_SREAM(1),
        ;
        private int type;
        SUB_SERVICE_TYPE(int type) {
            this.type = type;
        }

        public int getType() {
            return type;
        }
        @Override
        public String toString() {
            return getType() + "";
        }
    }

    /**
     * 提交ASR识别接口的参数名称
     */
    public enum PROPERTY_NAME {
        /**
         * 项目编号
         */
        PROJECT_ID("projectid"),
        /**
         *  提交服务类型
         */
        SUB_SERVICE_TYPE("sub_service_type"),
        /**
         * 识别引擎类型
         */
        ENGINE_MODEL_TYPE("engine_model_type"),
        /**
         * url
         */
        URL("url"),
        /**
         * 返回结果格式
         */
        RESULT_TEXT_FORMAT("res_text_format"),
        /**
         * 返回结果类型
         */
        RESULT_TYPE("res_type"),
        /**
         * 返回回调url
         */
        CALLBACK_URL("callback_url"),
        /**
         * 语音数据来源
         */
        SOURCE_TYPE("source_type"),
        /**
         * 访问密钥
         */
        SECRET_ID("secretid"),
        /**
         * 时间戳
         */
        TIMESTAMP("timestamp"),
        /**
         * 过期时间戳
         */
        EXPIRED("expired"),
        /**
         * 随机数
         */
        NONCE("nonce"),
        /**
         * 声道数量
         */
        CHANNEL_NEMBER("channel_num")
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
