package com.doubeye.log.replay.result.formatter;

import com.doubeye.commons.utils.constant.CommonConstant;
import com.doubeye.commons.utils.json.JSONUtils;
import com.doubeye.core.log.replay.result.formatter.ResultFormatter;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.io.File;
import java.util.Set;


/**
 * @author doubeye
 * 个人电话贡献度结果格式化工具
 */
public class AdaContributionResultFormatter implements ResultFormatter{
    @Override
    public JSONObject format(JSONObject source) {
        JSONObject result = getEmptyFormattedObject();
        /*
        setValue(source, result, PROPERTY_NAME.SUCCESS.toString());
        setValue(source, result, PROPERTY_NAME.ERROR_CODE.toString());
        setValue(source, result, PROPERTY_NAME.ERROR_MESSAGE.toString());
        setValue(source, result, PROPERTY_NAME.ID.toString());
        */
        JSONArray unformattedAdaData = source.getJSONArray(PROPERTY_NAME.DATA.toString());
        JSONArray formattedAdaData = result.getJSONArray(PROPERTY_NAME.DATA.toString());
        for (int i = 0; i < unformattedAdaData.size(); i ++) {
            JSONObject adaDataSource = unformattedAdaData.getJSONObject(i);
            formattedAdaData.add(getAdaData(adaDataSource));
        }
        result.put(PROPERTY_NAME.DATA.toString(), formattedAdaData);
        return result;
    }

    private JSONObject getAdaData(JSONObject adaDataSource) {
        JSONObject adaData = new JSONObject();
        setValue(adaDataSource, adaData, PROPERTY_NAME.ID.toString());
        setValue(adaDataSource, adaData, PROPERTY_NAME.NAME.toString());
        setValue(adaDataSource, adaData, PROPERTY_NAME.USER_ID.toString());
        setValue(adaDataSource, adaData, PROPERTY_NAME.USERNAME.toString());
        JSONObject unformattedStatData = adaDataSource.getJSONObject(PROPERTY_NAME.DATA.toString());
        //通话次数统计
        JSONArray unformattedPersonCallNumber = unformattedStatData.getJSONArray(PROPERTY_NAME.PERSON_CALL_NUMBER.toString());
        JSONArray formattedPersonCallNumber = formatStatData(unformattedPersonCallNumber);
        adaData.put(PROPERTY_NAME.PERSON_CALL_NUMBER.toString(), formattedPersonCallNumber);
        //通话时长统计
        JSONArray unformattedPersonCallLength = unformattedStatData.getJSONArray(PROPERTY_NAME.PERSON_CALL_LENGTH.toString());
        JSONArray formattedPersonCallLength = formatStatData(unformattedPersonCallLength);
        adaData.put(PROPERTY_NAME.PERSON_CALL_LENGTH.toString(), formattedPersonCallLength);
        //通话时段统计
        JSONArray unformattedPersonCallInterval = unformattedStatData.getJSONArray(PROPERTY_NAME.PERSON_CALL_INTERVAL.toString());
        JSONArray formattedPersonCallInterval = formatStatData(unformattedPersonCallInterval);
        adaData.put(PROPERTY_NAME.PERSON_CALL_INTERVAL.toString(), formattedPersonCallInterval);
        return adaData;
    }

    private JSONArray formatStatData(JSONArray unformattedData) {
        JSONArray result = new JSONArray();
        for (int i = 0; i < unformattedData.size(); i ++) {
            JSONObject unformattedObject = unformattedData.getJSONObject(i);
            JSONObject formattedObject = new JSONObject();
            Set<?> keys = unformattedObject.keySet();
            keys.forEach(key -> {
                setValue(unformattedObject, formattedObject, key.toString());
            });
            result.add(formattedObject);
        }
        return result;
    }

    private static JSONObject getEmptyFormattedObject() {
        JSONObject formattedObject = new JSONObject();
        formattedObject.put(PROPERTY_NAME.DATA.toString(), new JSONArray());
        return formattedObject;
    }

    private static JSONObject getEmptyAdaFormattedObject() {
        JSONObject adaFormattedObject = new JSONObject();
        JSONObject dataObject = new JSONObject();
        dataObject.put(PROPERTY_NAME.PERSON_CALL_NUMBER.toString(), new JSONArray());
        dataObject.put(PROPERTY_NAME.PERSON_CALL_LENGTH.toString(), new JSONArray());
        dataObject.put(PROPERTY_NAME.PERSON_CALL_INTERVAL.toString(), new JSONArray());
        adaFormattedObject.put(PROPERTY_NAME.DATA.toString(), dataObject);
        return adaFormattedObject;
    }

    /**
     * 获得格式转化的配置
     * @return 转化配置
     */
    private static JSONObject getFormatConfig() {
        JSONObject config = new JSONObject();
        getConfig(config, PROPERTY_NAME.SUCCESS.toString(), TYPE.STRING.toString());
        getConfig(config, PROPERTY_NAME.ERROR_CODE.toString(), TYPE.INT.toString());
        getConfig(config, PROPERTY_NAME.ERROR_MESSAGE.toString(), TYPE.INT.toString());
        getConfig(config, PROPERTY_NAME.ID.toString(), TYPE.TRY_INT.toString());
        getConfig(config, PROPERTY_NAME.NAME.toString(), TYPE.STRING.toString());
        getConfig(config, PROPERTY_NAME.USER_ID.toString(), TYPE.IGNORE.toString());
        getConfig(config, PROPERTY_NAME.USERNAME.toString(), TYPE.IGNORE.toString());
        getConfig(config, PROPERTY_NAME.STUDENT_ID.toString(), TYPE.INT.toString());
        getConfig(config, PROPERTY_NAME.STUDENT_NAME.toString(), TYPE.STRING.toString());
        getConfig(config, PROPERTY_NAME.STUDENT_MOBILE.toString(), TYPE.STRING.toString());
        getConfig(config, PROPERTY_NAME.CALL_COUNT.toString(), TYPE.NUMBER.toString());
        getConfig(config, PROPERTY_NAME.CALL_COUNT_TOTAL.toString(), TYPE.NUMBER_IGNORE_0.toString());
        getConfig(config, PROPERTY_NAME.CALL_COUNT_SECOND.toString(), TYPE.INT.toString());
        getConfig(config, PROPERTY_NAME.CALL_COUNT_TOTAL_SECOND.toString(), TYPE.INT.toString());
        getConfig(config, PROPERTY_NAME.HOUR.toString(), TYPE.STRING.toString());
        getConfig(config, PROPERTY_NAME.CALL_LENGTH.toString(), TYPE.NUMBER.toString());
        getConfig(config, PROPERTY_NAME.CALL_LENGTH_SECOND.toString(), TYPE.INT_IGNORE_0.toString());
        getConfig(config, PROPERTY_NAME.CALL_LENGTH_TOTAL.toString(), TYPE.NUMBER_IGNORE_0.toString());
        getConfig(config, PROPERTY_NAME.CALL_LENGTH_TOTAL_SECOND.toString(), TYPE.INT_IGNORE_0.toString());
        return config;
    }

    private static JSONObject config = getFormatConfig();

    private static void setValue(JSONObject source, JSONObject target, String propertyName) {
        String type = config.getString(propertyName);
        if (!TYPE.IGNORE.toString().equals(type)) {
            if (TYPE.STRING.toString().equals(type)) {
                target.put(propertyName, source.getString(propertyName));
            } else if (type.startsWith(TYPE.INT.toString())) {
                target.put(propertyName, Integer.valueOf(source.getString(propertyName)));
            } else if (type.equals(TYPE.TRY_INT.toString())) {
                try {
                    target.put(propertyName, Integer.valueOf(source.getString(propertyName)));
                } catch (Exception e) {
                    target.put(propertyName, source.getString(propertyName));
                }
            } else if (type.startsWith(TYPE.NUMBER.toString())) {
                String value = source.getString(propertyName);
                if (value.contains(CommonConstant.SEPARATOR.DOT.toString())) {
                    target.put(propertyName, source.getDouble(propertyName));
                } else {
                    target.put(propertyName, source.getInt(propertyName));
                }
            }
            if (type.contains("ignore0") && target.containsKey(propertyName) && "0".equals(target.getString(propertyName))) {
                target.remove(propertyName);
            }
        }
    }

    private static void getConfig(JSONObject config, String fieldName, String type) {
        config.put(fieldName, type);
    }

    enum PROPERTY_NAME {
        /**
         * 成功标志
         */
        SUCCESS("success"),
        /**
         * 返回状态码
         */
        ERROR_CODE("errorCode"),
        /**
         * 返回信息
         */
        ERROR_MESSAGE("errorMessage"),
        /**
         * 数据
         */
        DATA("data"),
        /**
         * 编号
         */
        ID("id"),
        /**
         * 姓名
         */
        NAME("name"),
        /**
         * 用户编号
         */
        USER_ID("userid"),
        /**
         * 用户姓名
         */
        USERNAME("username"),
        /**
         * 联系人联系次数
         */
        PERSON_CALL_NUMBER("person_call_number"),
        /**
         * 联系人联系时长
         */
        PERSON_CALL_LENGTH("person_call_length"),
        /**
         * 时段统计
         */
        PERSON_CALL_INTERVAL("person_call_interval"),
        /*
        学生ID
         */
        STUDENT_ID("student_id"),
        /**
         * 学生姓名
         */
        STUDENT_NAME("student_name"),
        /**
         * 学生手机号
         */
        STUDENT_MOBILE("student_mobile"),
        /**
         * 有效联系次数（时长）
         */
        CALL_COUNT("call_count"),
        /**
         * 总联系次数（时长）
         */
        CALL_COUNT_TOTAL("call_count_total"),
        /**
         * 有效联系时长，单位秒
         */
        CALL_COUNT_SECOND("call_count_second"),
        /**
         * 总联系时长，单位秒
         */
        CALL_COUNT_TOTAL_SECOND("call_count_total_second"),
        /**
         * 时段
         */
        HOUR("hour"),
        /**
         * 时段内有效呼叫时长
         */
        CALL_LENGTH("call_length"),
        /**
         * 呼叫时长，单位秒
         */
        CALL_LENGTH_SECOND("call_length_second"),
        CALL_LENGTH_TOTAL("call_length_total"),
        CALL_LENGTH_TOTAL_SECOND("call_length_total_second"),
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

    public static void main(String[] args) {
        JSONObject source = JSONUtils.getJsonObjectFromFile(new File("D:/result/get_personal_contribute_AWE_qKhHDasakSi59ryZ_es.txt"));
        AdaContributionResultFormatter formatter = new AdaContributionResultFormatter();
        System.out.println(formatter.format(JSONObject.fromObject(source)));
    }
}
