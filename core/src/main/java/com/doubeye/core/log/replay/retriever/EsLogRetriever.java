package com.doubeye.core.log.replay.retriever;


import com.doubeye.commons.utils.elasticsearch.CustomSearchHelper;
import com.doubeye.commons.utils.json.JSONUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.elasticsearch.search.SearchHits;

/**
 * @author doubeye
 * ES日志提取器
 */
public class EsLogRetriever extends AbstractLogRetriever{
    private CustomSearchHelper searchHelper;
    private JSONObject filter;

    public EsLogRetriever(CustomSearchHelper searchHelper) {
        this.searchHelper = searchHelper;
        searchHelper.setStart(3200);
        searchHelper.setDeepPaging(true);
        searchHelper.setSize(200);
    }

    public EsLogRetriever(CustomSearchHelper searchHelper, JSONObject filter) {
        this(searchHelper);
        this.filter = filter;
    }

    @Override
    public void setPageSize(int pageSize) {
        super.setPageSize(pageSize);
        searchHelper.setSize(pageSize);
    }

    @Override
    public JSONArray retrieveNext() {
        SearchHits hits = searchHelper.getHits(searchHelper.getQueryBuilder());
        if (hits.getHits().length == 0) {
            return null;
        } else {
            JSONArray result = new JSONArray();
            hits.forEach(hit -> {
                try {
                    if (hit.getSourceAsString().length() < 1000000) {
                        JSONObject entry = JSONObject.fromObject(hit.getSource());
                        if (JSONUtils.equalByCondition(entry, filter)) {
                            entry.put("_id", hit.getId());
                            result.add(entry);
                        }
                    }

                } catch (Exception e) {
                    System.err.println(hit.getId() + " format error");
                }
            });
            return result;
        }
    }
}
