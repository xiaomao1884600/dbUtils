package com.doubeye.record.quality.inspection.speachcraft.loader;

import com.doubeye.commons.utils.elasticsearch.ClientHelper;
import com.doubeye.commons.utils.elasticsearch.CustomSearchHelper;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.elasticsearch.client.transport.TransportClient;

import java.net.UnknownHostException;

/**
 * @author doubeye
 * 录音分析结果载入器
 */
@SuppressWarnings("unused | WeakerAccess")
public class RecordLoader {
    /**
     * 文档助手
     */
    private CustomSearchHelper searchHelper;
    /**
     * 每次加载的记录数
     */
    private int recordCount = 100;
    /**
     * 查询条件中的开始时间
     */
    private String startTime;

    public JSONArray load() {
        searchHelper.addRangeCondition("create_at", getRangeCondition());
        searchHelper.setStart(1);
        searchHelper.setSize(recordCount);
        return searchHelper.getSearchResult(searchHelper.getQueryBuilder());
    }

    private JSONObject getRangeCondition() {
        JSONObject createAtCondition = new JSONObject();
        createAtCondition.put("start", startTime);
        return createAtCondition;
    }


    public CustomSearchHelper getSearchHelper() {
        return searchHelper;
    }

    public void setSearchHelper(CustomSearchHelper searchHelper) {
        this.searchHelper = searchHelper;
    }

    public int getRecordCount() {
        return recordCount;
    }

    public void setRecordCount(int recordCount) {
        this.recordCount = recordCount;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public static void main(String[] args) throws UnknownHostException {
        RecordLoader loader = new RecordLoader();
        ClientHelper clientHelper = new ClientHelper();
        clientHelper.setClusterName(CLUSTER_NAME);
        clientHelper.addNode(CLUSTER_NODE);
        CustomSearchHelper searchHelper = new CustomSearchHelper();
        try (TransportClient client = clientHelper.getClient()) {
            searchHelper.setIndexName(INDEX_NAME);
            searchHelper.setTypeName(TYPE_NAME);
            searchHelper.setClient(client);
            loader.setSearchHelper(searchHelper);
            loader.setStartTime("2018-02-22 10:00:00");
            JSONArray records = loader.load();
            System.out.println(records.size());
            for (int i = 0; i < records.size(); i ++) {
                JSONObject record = records.getJSONObject(i);
                System.out.println(record.getString("record_id"));
            }
        }
    }

    private static final String CLUSTER_NAME = "hxsd-bd";
    private static final String CLUSTER_NODE = "10.2.24.57:9300";
    private static final String FIELD_NAME = "record_id";
    private static final String INDEX_NAME = "record_alias_*";
    private static final String TYPE_NAME = "analyze";
}
