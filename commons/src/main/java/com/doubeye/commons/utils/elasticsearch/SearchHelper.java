package com.doubeye.commons.utils.elasticsearch;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.http.client.methods.RequestBuilder;
import org.elasticsearch.action.ActionRequestBuilder;
import org.elasticsearch.action.search.*;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.MatchAllQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.sort.SortOrder;

import java.util.ArrayList;
import java.util.List;

import static org.elasticsearch.index.query.QueryBuilders.matchQuery;

/**
 * @author doubeye
 * @version 1.0.0
 * 查询助收
 */
@SuppressWarnings("WeakerAccess")
public class SearchHelper {
    private TransportClient client;
    private String indexName;
    private String typeName;
    private PageConfig pageConfig = new PageConfig();
    private String scrollId;
    /**
     * 是否支持深分页
     */
    private boolean deepPaging = false;
    private List<SortConfig> sorts = new ArrayList<>();

    public void setClient(TransportClient client) {
        this.client = client;
    }

    public void setIndexName(String indexName) {
        this.indexName = indexName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public void setStart(int start) {
        pageConfig.setStart(start);
    }

    public void setEnd(int end) {
        pageConfig.setEnd(end);
    }

    public void setSize(int size) {
        pageConfig.setSize(size);
    }

    public void addSort(String field, SortOrder order) {
        sorts.add(new SortConfig(field, order));
    }

    public boolean isDeepPaging() {
        return deepPaging;
    }

    public void setDeepPaging(boolean deepPaging) {
        this.deepPaging = deepPaging;
    }

    public JSONArray fulltext(String fields, String keyword) {
        JSONArray result = new JSONArray();
        MatchQueryBuilder queryBuilder = matchQuery(fields, keyword);
        SearchResponse searchResponse = client.prepareSearch(indexName).setTypes(typeName).setQuery(queryBuilder).setSize(100).execute().actionGet();
        if (StringUtils.isNotEmpty(scrollId)) {
            this.scrollId = scrollId;
        }
        SearchHits searchHits = searchResponse.getHits();
        if (searchHits.getTotalHits() == 0) {
            this.scrollId = null;
        }
        for (SearchHit hit : searchHits) {
            result.add(JSONObject.fromObject(hit.getSourceAsString()));
        }
        return result;
    }

    public SearchResponse getSearchResponse(QueryBuilder queryBuilder) {
        System.out.println(queryBuilder.toString());
        SearchResponse searchResponse = (SearchResponse) getPrepareSearch(queryBuilder).get();
        String scrollId = searchResponse.getScrollId();
        if (StringUtils.isNotEmpty(scrollId)) {
            this.scrollId = scrollId;
        }
        if (searchResponse.getHits().getTotalHits() == 0) {
            this.scrollId = null;
        }
        return searchResponse;
    }

    public JSONArray getSearchResult(QueryBuilder queryBuilder) {
        SearchHits hits = getHits(queryBuilder);
        return fromHits(hits);
    }

    public SearchHits getHits(QueryBuilder queryBuilder) {
        SearchResponse searchResponse = (SearchResponse) getPrepareSearch(queryBuilder).get();
        String scrollId = searchResponse.getScrollId();
        if (StringUtils.isNotEmpty(scrollId)) {
            this.scrollId = scrollId;
        }
        if (searchResponse.getHits().getTotalHits() == 0) {
            this.scrollId = null;
        }
        return searchResponse.getHits();
    }

    public ActionRequestBuilder getPrepareSearch(QueryBuilder queryBuilder) {
        ActionRequestBuilder builder;
        if (deepPaging && StringUtils.isNotEmpty(this.scrollId)) {
            builder = client.prepareSearchScroll(scrollId);
        } else {
            builder = client.prepareSearch(indexName).setTypes(typeName).setQuery(queryBuilder);
            if (pageConfig.getStart() >= 0) {
                int size = pageConfig.getSize();
                ((SearchRequestBuilder) builder).setFrom(pageConfig.getStart()).setSize(size);

            }
            for (SortConfig sort : sorts) {
                ((SearchRequestBuilder) builder).addSort(sort.getFiledName(), sort.getSort());
            }
        }
        if (deepPaging) {
            if (builder instanceof SearchScrollRequestBuilder) {
                ((SearchScrollRequestBuilder)builder).setScroll(new TimeValue(60000));
            }
            if (builder instanceof SearchRequestBuilder) {
                ((SearchRequestBuilder)builder).setScroll(new TimeValue(60000));
            }
        }

        return builder;
    }

    public QueryBuilder getMatchAllQueryBuilder() {
        return new MatchAllQueryBuilder();
    }

    public JSONArray fromHits(SearchHits hits) {
        JSONArray result = new JSONArray();
        hits.forEach(hit -> {
            try {
                result.add(JSONObject.fromObject(hit.getSource()));
            } catch (Exception e) {
                System.err.println(hit.getId() + " format error");
            }
        });
        return result;
    }


}
