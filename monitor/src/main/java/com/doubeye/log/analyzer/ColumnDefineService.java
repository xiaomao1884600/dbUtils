package com.doubeye.log.analyzer;

import net.sf.json.JSONArray;

import java.util.Map;

public class ColumnDefineService {
    public JSONArray getEControllerDefines(Map<String, String[]> parameters) {
        return JSONArray.fromObject("[{\n" +
                "            dataId: 'controllerName',\n" +
                "            label: '控制器',\n" +
                "            canSort: true\n" +
                "        }, {\n" +
                "            dataId: 'times',\n" +
                "            label: '调用次数',\n" +
                "            canSort: true\n" +
                "        }, {\n" +
                "            dataId: 'min',\n" +
                "            label: '最小花费时间',\n" +
                "            canSort: true\n" +
                "        }, {\n" +
                "            dataId: 'max',\n" +
                "            label: '最大花费时间',\n" +
                "            canSort: true\n" +
                "        }, {\n" +
                "            dataId: 'mean',\n" +
                "            label: '平均花费时间',\n" +
                "            canSort: true\n" +
                "        }, {\n" +
                "            dataId: 'stdev',\n" +
                "            label: '标准差',\n" +
                "            canSort: true\n" +
                "        }]");
    }
}
