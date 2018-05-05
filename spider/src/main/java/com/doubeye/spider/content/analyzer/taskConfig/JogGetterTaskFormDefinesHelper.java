package com.doubeye.spider.content.analyzer.taskConfig;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;

public class JogGetterTaskFormDefinesHelper {

    public static JSONArray getFormDefines() {
        JSONArray result = new JSONArray();


        result.add(getInputDefine("名称", "schedule.name", ""));
        result.add(getInputDefine("标示符", "schedule.identifier", ""));
        result.add(getInputDefine("任务类型", "schedule.scheduleType", "1"));
        result.add(getInputDefine("单例控制类型", "schedule.singletonType", "2"));

        result.add(getInputDefine("运行入口类", "schedule.initConfig.mainClassName", ""));
        result.add(getInputDefine("结果文件存放目录", "schedule.initConfig.resultFileDirectory", ""));
        result.add(getInputDefine("抓取结果文件名模板", "schedule.initConfig.originFileNameTemplate", "jobs_%s"));
        result.add(getInputDefine("处理后的文件名模板", "schedule.initConfig.processedFileNameTemplate", "processedJobs_%s"));
        //生成options
        result.add(getDMOptionDefine("数据源", "schedule.initConfig.dataSourceId", "", "dataSources", "name"));
        result.add(getInputDefine("结果表名模板", "schedule.initConfig.tableNameTemplate", "spider_job_types_%s_%s"));
        return result;
    }

    private static JSONObject getInputDefine(String label, String dataId, String defaultValue) {
        JSONObject result = new JSONObject();
        result.put("component", "md-input");
        result.put("label", label);
        result.put("dataId", dataId);
        result.put("defaultValue", defaultValue);
        return result;
    }

    private static JSONObject getDMOptionDefine(String label, String dataId, String defaultValue, String optionsPropertyName, String displayPropertyName) {
        JSONObject result = new JSONObject();
        result.put("component", "md-option");
        result.put("label", label);
        result.put("dataId", dataId);
        result.put("defaultValue", defaultValue);
        result.put("optionsPropertyName", optionsPropertyName);
        result.put("displayPropertyName", displayPropertyName);
        return result;
    }

    private static String generateAngularTemplate(JSONArray formDefiles) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < formDefiles.size(); i ++) {
            builder.append("<div>\n\t");
            JSONObject define = formDefiles.getJSONObject(i);
            String type = define.getString("component");
            if ("input".equals(type)) {
                builder.append(generateInput(define)).append("\n");
            }else if ("md-input".equals(type)) {
                builder.append(generateMDInput(define)).append("\n");
            }else if ("md-option".equals(type)){
                builder.append(generateMDOption(define)).append("\n");
            }
            builder.append("</div>\n");
        }
        return builder.toString();
    }

    private static String generateMDInput(JSONObject define) {
        return com.doubeye.commons.utils.string.StringUtils.format(MD_INPUT_TEMPLATE, define);
    }

    private static String generateMDOption(JSONObject define) {
        return com.doubeye.commons.utils.string.StringUtils.format(TODO_ADD_JS_DATA_WARNING_MESSAGE, define) + "\n" +
                com.doubeye.commons.utils.string.StringUtils.format(MD_OPTION_TEMPLATE, define);
    }

    private static String generateInput(JSONObject define) {

        return com.doubeye.commons.utils.string.StringUtils.format(LABEL_TEMPLATE, define) +
                com.doubeye.commons.utils.string.StringUtils.format(INPUT_TEMPLATE, define);
    }

    private static final String LABEL_TEMPLATE = "<label for=\"([{dataId}])\">([{label}])</label>";
    private static final String INPUT_TEMPLATE = "<div><input type=\"text\" id=\"([{dataId}])\" ng-model=\"$ctrl.([{dataId}])\"/></div>";

    private static final String TODO_ADD_JS_DATA_WARNING_MESSAGE = "<!-- TODO you should add this.([{dataId}]) in the controller`s js file-->";
    private static final String MD_OPTION_TEMPLATE = "<md-input-container>\n" +
            "\t<md-select ng-model=\"$ctrl.([{dataId}])\" placeholder=\"([{label}])\">\n" +
            "\t\t<md-option ng-value=\"item\" ng-repeat=\"item in $ctrl.([{optionsPropertyName}])\">{{ item.([{displayPropertyName}]) }}</md-option>\n" +
            "\t</md-select>\n" +
            "</md-input-container>";

    private static final String MD_INPUT_TEMPLATE = "<md-input-container>\n" +
            "\t<label>([{label}])</label>\n" +
            "\t<input ng-model=\"$ctrl.([{dataId}])\">\n" +
            "</md-input-container>";

    public static void main(String[] args) {
        JSONArray formDefiles = getFormDefines();
        String angularJsTemplate = generateAngularTemplate(formDefiles);
        System.out.println(angularJsTemplate);
    }
}
