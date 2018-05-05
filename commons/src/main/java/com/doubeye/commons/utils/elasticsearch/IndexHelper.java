package com.doubeye.commons.utils.elasticsearch;

import net.sf.json.JSONObject;

import org.elasticsearch.action.ActionResponse;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.DeleteByQueryAction;


public class IndexHelper {

    public void setClient(TransportClient client) {
        this.client = client;
    }

    public TransportClient getClient() {
        return client;
    }

    private TransportClient client;

    /**
     * 创建一个索引，类似于数据库中的Database
     * @param indexName
     */
    public void createIndex(String indexName, JSONObject config) {
        Settings settings = Settings.builder().put(config).build();
        client.admin().indices().prepareCreate(indexName).setSettings(settings).get();
    }

    public void deleteIndex(String indexName) {
        client.admin().indices().prepareDelete(indexName).execute().actionGet();
    }

    /**
     * 删除Type下的数据
     * @param indexName 索引名
     * @param typeName 类型名
     */
    public void deleteType(String indexName, String typeName) {
        ActionResponse response = DeleteByQueryAction.INSTANCE.newRequestBuilder(client)
                .filter(QueryBuilders.matchQuery("_type", typeName))
                .source(indexName)
                .get();
        System.out.println(response.toString());
    }


    public static final String INDEX_CONFIG_NUMBER_OF_SHARDS = "index.number_of_shards";
    public static final String INDEX_CONFIG_NUMBER_OF_REPLICAS = "index.number_of_replicas";
    public static final int INDEX_CONFIG_DEFAULT_NUMBER_OF_SHARDS = 2;
    public static final int INDEX_CONFIG_DEFAULT_NUMBER_OF_REPLICAS = 2;
    public static JSONObject getDefaultIndexConfig() {
        JSONObject defaultConfig = new JSONObject();
        defaultConfig.put(INDEX_CONFIG_NUMBER_OF_SHARDS, INDEX_CONFIG_DEFAULT_NUMBER_OF_SHARDS + "");
        defaultConfig.put(INDEX_CONFIG_NUMBER_OF_REPLICAS, INDEX_CONFIG_DEFAULT_NUMBER_OF_REPLICAS + "");
        return defaultConfig;
    }

    /**
     * 创建一个类型，类是数据库中的Table
     * @param indexName 索引名称
     * @param typeName 类型名称
     * @param structure 结构 结构参见ElasticSearch的文档 @see https://www.elastic.co/guide/en/elasticsearch/reference/current/mapping.html
     */
    public void createType(String indexName, String typeName, JSONObject structure) {
        client.admin().indices().preparePutMapping(indexName).setType(typeName).setSource(structure).get();
    }
}
