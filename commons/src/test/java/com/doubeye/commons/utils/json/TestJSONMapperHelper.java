package com.doubeye.commons.utils.json;

import com.doubeye.commons.utils.json.mapper.JSONMapperHelper;
import com.doubeye.commons.utils.json.mapper.NameMapConfig;
import junit.framework.TestCase;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.junit.Assert;

public class TestJSONMapperHelper extends TestCase{
    public void testAll() {
        JSONArray array = JSONArray.fromObject("[{\"identifier\":\"ZHAOPIN_GETTER_OF_THE_DAY\",\"schedule_type\":\"0\",\"name\":\"智联招聘当天数据抓取\",\"singleton_type\":\"0\",\"id\":\"1\",\"run_config\":\"0\"}]");
        if (array.size() > 0) {
            //通过JSONObject生成Config
            NameMapConfig config = JSONMapperHelper.getToCamelMapper(array.getJSONObject(0));
            //通过ScheduleBean生成config
            //NameMapConfig config = JSONMapperHelper.getFromClass(ScheduleBean.class);
            config.getNameMappers().remove("singleton_type");
            config.getNameMappers().put("singleton_type", "type");
            array = JSONMapperHelper.doNameMapper(array, config);
            System.out.println(array.toString());
            Assert.assertTrue(array.getJSONObject(0).containsKey("scheduleType"));
            NameMapConfig reversedConfig = new NameMapConfig();
            reversedConfig.setNameMappers(config.getReversedNameMapper());
            array = JSONMapperHelper.doNameMapper(array, reversedConfig);
            System.out.println(array.toString());
            Assert.assertTrue(array.getJSONObject(0).containsKey("schedule_type"));
        }
    }
    @SuppressWarnings("unused")
    private class ScheduleBean {
        /**
         * 唯一编号
         */
        private int id;
        /**
         * 名称
         */
        private String name;
        /**
         * 标示符
         */
        private String identifier;
        /**
         * 任务类型，具体字典参照dict_schedule_type
         */
        private int scheduleType;
        /**
         * 配置，具体解释需参考实际的任务类
         */
        private JSONObject config;
        /**
         * 运行配置，具体解释需参考实际的任务类
         */
        private JSONObject runConfig;
        /**
         * 单例类型，具体类型需参考dict_singleton_type
         */
        private int singletonType;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getIdentifier() {
            return identifier;
        }

        public void setIdentifier(String identifier) {
            this.identifier = identifier;
        }

        public int getScheduleType() {
            return scheduleType;
        }

        public void setScheduleType(int scheduleType) {
            this.scheduleType = scheduleType;
        }

        public JSONObject getConfig() {
            return config;
        }

        public void setConfig(JSONObject config) {
            this.config = config;
        }

        public JSONObject getRunConfig() {
            return runConfig;
        }

        public void setRunConfig(JSONObject runConfig) {
            this.runConfig = runConfig;
        }

        public int getSingletonType() {
            return singletonType;
        }

        public void setSingletonType(int singletonType) {
            this.singletonType = singletonType;
        }
    }
}
