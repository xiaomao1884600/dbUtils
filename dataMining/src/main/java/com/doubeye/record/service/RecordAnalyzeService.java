package com.doubeye.record.service;

import com.doubeye.commons.utils.elasticsearch.ClientHelper;
import com.doubeye.commons.utils.elasticsearch.CustomSearchHelper;
import com.doubeye.commons.utils.request.RequestHelper;
import com.doubeye.commons.utils.response.ResponseHelper;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.search.SearchHits;

import java.net.UnknownHostException;
import java.util.Map;

/**
 * @author doubeye
 * 录音文件分析结果服务
 */
@SuppressWarnings("unused")
public class RecordAnalyzeService {

    /**
     * 根据录音id获得识别结果
     * @param parameters 参数，需包含以下属性
     *                   recordIds 录音Id
     * @return 根据录音id找到的识别结果
     */
    public JSONObject getAnalyzeResultByRecordIds(Map<String, String[]> parameters) throws UnknownHostException {
        String recordIds = RequestHelper.getString(parameters, PROPERTY_NAME_RECORD_IDS);
        recordIds = recordIds.replace("\n", ",").replace("\r", "");
        int start = RequestHelper.getInt(parameters, PROPERTY_NAME_START);
        int size = RequestHelper.getInt(parameters, PROPERTY_NAME_SIZE);
        ClientHelper clientHelper = new ClientHelper();
        clientHelper.setClusterName(CLUSTER_NAME);
        clientHelper.addNode(CLUSTER_NODE);
        CustomSearchHelper searchHelper = new CustomSearchHelper();
        try (TransportClient client = clientHelper.getClient()) {
            searchHelper.setIndexName(INDEX_NAME);
            searchHelper.setTypeName(TYPE_NAME);
            searchHelper.setClient(client);
            searchHelper.addMatchPhase(FIELD_NAME, recordIds);
            searchHelper.setStart(start);
            searchHelper.setSize(size);
            SearchResponse response = searchHelper.getSearchResponse(searchHelper.getQueryBuilder());
            SearchHits hits = response.getHits();
            long total = hits.getTotalHits();
            System.out.println(total);
            JSONArray universities = searchHelper.fromHits(hits);
            return ResponseHelper.getSuccessObject(universities, total);
        }
    }

    private static final String CLUSTER_NAME = "hxsd-bd";
    private static final String CLUSTER_NODE = "10.2.24.57:9300";
    private static final String PROPERTY_NAME_RECORD_IDS = "recordIds";
    private static final String PROPERTY_NAME_START = "start";
    private static final String PROPERTY_NAME_SIZE = "size";
    private static final String FIELD_NAME = "record_id";
    private static final String INDEX_NAME = "record_alias_*";
    private static final String TYPE_NAME = "analyze";
}
