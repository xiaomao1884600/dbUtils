package com.doubeye.spider.content.analyzer.university.gaoKaoPai.service;

import com.doubeye.commons.utils.elasticsearch.ClientHelper;
import com.doubeye.commons.utils.elasticsearch.CustomSearchHelper;
import com.doubeye.commons.utils.request.RequestHelper;
import com.doubeye.commons.utils.response.ResponseHelper;
import com.doubeye.spider.content.analyzer.university.gaoKaoPai.persistent.GaoKaoPaiUniversityEsSaver;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.sort.SortOrder;

import java.net.UnknownHostException;
import java.util.Map;

/**
 * @author doubeye
 * @version 1.0.0
 * 高考派高校查询服务
 */
@SuppressWarnings("unused")
public class SearchService {
    public JSONObject fullTextSearch(Map<String, String[]> parameters) throws UnknownHostException {
        String keyword = RequestHelper.getString(parameters, "keyword");
        String city = RequestHelper.getString(parameters, "city");
        int start = RequestHelper.getInt(parameters, "start", 1);
        int size = RequestHelper.getInt(parameters, "size", 10);
        String source = RequestHelper.getString(parameters, "source");
        ClientHelper clientHelper = new ClientHelper();
        clientHelper.setClusterName(GaoKaoPaiUniversityEsSaver.CLUSTER_NAME);
        clientHelper.addNode(GaoKaoPaiUniversityEsSaver.CLUSTER_ADDRESS);
        try (TransportClient client = clientHelper.getClient();) {

            CustomSearchHelper searchHelper = new CustomSearchHelper();
            searchHelper.setClient(client);
            searchHelper.setIndexName(GaoKaoPaiUniversityEsSaver.INDEX_NAME);
            searchHelper.setTypeName(source);
            if (!StringUtils.isEmpty(keyword)) {
                searchHelper.addFullTextMatch("introduction", keyword);
            }
            if (!StringUtils.isEmpty(city)) {
                searchHelper.addMatchPhase("city", city);
            }

            searchHelper.setStart(start);
            searchHelper.setSize(size);
            searchHelper.addSort("id", SortOrder.ASC);

            SearchResponse response = searchHelper.getSearchResponse(searchHelper.getQueryBuilder());
            SearchHits hits = response.getHits();
            long total = hits.getTotalHits();
            JSONArray universities = searchHelper.fromHits(hits);

            return ResponseHelper.getSuccessObject(universities, total);
        }
    }
}
