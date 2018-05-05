package com.doubeye.core.log.replay.result.formatter;


import net.sf.json.JSONObject;

/**
 * @author doubeye
 * 返回结果格式化接口
 */
public interface ResultFormatter {
    enum TYPE {
        /**
         * 整形
         */
        INT("int"),
        /*
        字符串
         */
        STRING("string"),
        /**
         * 数字，如果存在小数点，则转化为double，否则转化为int
         */
        NUMBER("number"),
        /**
         * 尝试转化为int，如果失败转化为字符串
         */
        TRY_INT("tryInt"),
        /**
         * 忽略
         */
        IGNORE("ignore"),
        /**
         * 数值型，如果为0，则忽略 @see TYPE.NUMBER
         */
        NUMBER_IGNORE_0("number|ignore0"),
        /**
         * 整形，，如果为0，则忽略 @see TYPE.INT
         */
        INT_IGNORE_0("int|ignore0"),;
        private String type;
        TYPE(String type) {
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
     * 对返回结果进行格式化
     * @param source 原结果
     * @return 格式化后的结果
     */
    JSONObject format(JSONObject source);
}
