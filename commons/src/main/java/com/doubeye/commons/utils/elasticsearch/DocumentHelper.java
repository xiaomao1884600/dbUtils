package com.doubeye.commons.utils.elasticsearch;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;

import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.xcontent.XContentBuilder;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

/**
 * @author doubeye
 * ES文档助手
 */
public class DocumentHelper {

    private IndexHelper indexHelper;
    private String indexName;
    private String typeName;

    /**
     * 添加文档
     * @param documentContents 文档内容
     * @throws IOException
     */
    public void addDocuments(JSONArray documentContents) throws IOException {
        TransportClient client = indexHelper.getClient();
        BulkRequestBuilder builder = client.prepareBulk();

        for (int i = 0; i < documentContents.size(); i ++) {
            builder.add(client.prepareIndex(indexName, typeName).setSource(
                    fromJSONObject(documentContents.getJSONObject(i))));
        }
        if (documentContents.size() > 0) {
            System.out.println(builder.numberOfActions());
            BulkResponse bulkResponse = builder.get();
            if (bulkResponse.hasFailures()) {
                System.out.println(bulkResponse.buildFailureMessage());
            }
        }
    }

    public void mergeDocuments(String id, JSONObject toBeMergedContents) throws ExecutionException, InterruptedException, IOException {
        TransportClient client = indexHelper.getClient();
        UpdateRequest updateRequest = new UpdateRequest(indexName, typeName, id).doc(fromJSONObject(toBeMergedContents));
        client.update(updateRequest).get();
    }

    public void deleteDocument(String id) {
        TransportClient client = indexHelper.getClient();
        DeleteResponse response = client.prepareDelete(indexName, typeName, id).get();
    }

    private static XContentBuilder fromJSONObject(JSONObject content) throws IOException {
        XContentBuilder contentBuilder = jsonBuilder().startObject();
        if (content != null) {
            Set<?> keys = content.keySet();
            for (Object key : keys) {
                contentBuilder.field(key.toString(), content.get(key));
            }
        }
        return contentBuilder.endObject();
    }


    public void setIndexHelper(IndexHelper indexHelper) {
        this.indexHelper = indexHelper;
    }

    public IndexHelper getIndexHelper() {
        return indexHelper;
    }

    public void setIndexName(String indexName) {
        this.indexName = indexName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getIndexName() {
        return indexName;
    }

    public String getTypeName() {
        return typeName;
    }
}
