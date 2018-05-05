package com.doubeye.commons.utils.constant;

/**
 * @author doubeye
 * 常用的常量
 */
public class CommonConstant {
    /**
     * HTTP Method
     */
    public enum HTTP_METHOD {
        /**
         * POST方法
         */
        POST("POST"),
        /**
         * GET方法
         */
        GET("GET"),
        /**
         * DELETE方法
         */
        DELETE("DELETE"),
        /**
         * PUT方法
         */
        PUT("PUT"), ;

        private String method;

        HTTP_METHOD(String method) {
            this.method = method;
        }

        public String getMethod() {
            return this.method;
        }
    }

    /**
     * HTTP HEADER 中的属性名
     */
    public enum HEADER_PROPERTY_NAME {
        /**
         * ACCEPT属性
         */
        ACCEPT("accept"),
        /**
         * CONTENT TYPE属性
         */
        CONTENT_TYPE ("content-type"),
        /**
         * contentType属性
         */
        CONTENTYYE("contentType"),
        /**
         * date属性
         */
        DATE("date"),
        /**
         * Authorization属性
         */
        AUTHORIZATION("Authorization"),
        /**
         * Accept Charset 接受的字符集
         */
        ACCEPT_CHARSET("Accept-Charset"),
        /**
         * host
         */
        HOST("Host"),
        ;

        private String property;

        HEADER_PROPERTY_NAME(String property) {
            this.property = property;
        }

        public String getProperty() {
            return property;
        }

        @Override
        public String toString() {
            return getProperty();
        }
    }

    /**
     * 常用的Header属性值
     */
    public enum HEADER_PROPERTY_VALUE {
        /**
         * 默认的ACCEPT值，application/json
         */
        ACCEPT("application/json"),
        /**
         * 默认CONTENT TYPE的值，application/json
         */
        CONTENT_TYPE("application/json")
        ;


        private String value;
        HEADER_PROPERTY_VALUE(String value) {
            this.value = value;
        }
        public String getValue() {
            return this.value;
        }
        @Override
        public String toString() {
            return getValue();
        }
    }

    /**
     * http 中的Content-Type
     */
    public enum CONTENT_TYPE {
        /**
         * application/json
         */
        APPLICATION_JSON("application/json"),
        /**
         * application/octet-stream
         */
        APPLICATION_OCTET_STREAM("application/octet-stream"),
        ;
        private String type;
        CONTENT_TYPE(String type) {
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
     * 字符集
     */
    public enum CHARSETS {
        /**
         * utf-8字符集
         */
        UTF_8("utf-8"),
        ;
        private String charset;
        CHARSETS(String charset) {
            this.charset = charset;
        }
        public String getCharset() {
            return this.charset;
        }
        @Override
        public String toString() {
            return getCharset();
        }
    }

    /**
     * 加密算法
     */
    public enum CIPHERS {
        /**
         * HmacSHA1加密算法
         */
        Hmac_SHA1("HmacSHA1"),
        /**
         * MD5算法
         */
        MD5("MD5"),
        ;

        private String cipher;
        CIPHERS(String cipher) {
            this.cipher = cipher;
        }

        public String getCipher() {
            return cipher;
        }

        @Override
        public String toString() {
            return getCipher();
        }
    }

    /**
     * 常用分隔符
     */
    public enum SEPARATOR {
        /**
         * 点号
         */
        DOT("."),
        /**
         * 逗号
         */
        COMMA(","),
        /**
         * 冒号
         */
        COLON(":"),
        /**
         * 左中括号
         */
        LEFT_BRACKET("["),
        /**
         * 右中括号
         */
        RIGHT_BRACKET("]"),
        /**
         * 单引号
         */
        SINGLE_QUOTE_MARK("'"),;
        private String separator;
        SEPARATOR(String separator) {
            this.separator = separator;
        }

        public String getSeparator() {
            return separator;
        }
        @Override
        public String toString() {
            return getSeparator();
        }
    }
}
