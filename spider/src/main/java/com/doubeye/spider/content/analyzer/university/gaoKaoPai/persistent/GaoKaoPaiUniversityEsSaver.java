package com.doubeye.spider.content.analyzer.university.gaoKaoPai.persistent;

import com.doubeye.commons.application.ConnectionManager;
import com.doubeye.commons.application.GlobalApplicationContext;
import com.doubeye.commons.database.sql.SingleNumberPagedExecutor;
import com.doubeye.commons.utils.elasticsearch.*;
import com.doubeye.commons.utils.test.ApplicationContextInitiator;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.sort.SortOrder;

import java.io.IOException;
import java.net.UnknownHostException;
import java.sql.Connection;


/**
 * @author doubeye
 * @version 1.0.0
 * 高考派大学信息Es保存助手
 */
public class GaoKaoPaiUniversityEsSaver {
    private static Logger logger = LogManager.getLogger(GaoKaoPaiUniversityEsSaver.class);


    //测试数据库
    //private static final String CLUSTER_NAME = "hxsd-loges";
    //private static final String CLUSTER_ADDRESS = "10.2.24.65:9300";
    //谢文豪
    public static final String CLUSTER_NAME = "hxsd-es";
    public static final String CLUSTER_ADDRESS = "10.2.20.59:9300";
    public static final String INDEX_NAME = "spider";
    public static final String TYPE_NAME = "universities_gx211";

    private IndexHelper helper;
    private Connection conn;

    private static JSONObject getUniversityTypeStructure() {
        JSONObject structure = new JSONObject();
        //允许全字段检索
        JSONObject fieldAllProperty = new JSONObject();
        fieldAllProperty.put("enabled", true);
        fieldAllProperty.put("analyzer", "ik_max_word");
        structure.put("_all", fieldAllProperty);



        //字段的映射
        JSONObject properties = new JSONObject();
        //id
        JSONObject id = getLongField();
        properties.put("id", id);
        //大学名称
        JSONObject university = getTextFields("text", "");
        properties.put("university", university);
        //所在城市
        JSONObject city = getTextFields("text", "");
        properties.put("city", city);
        //phone
        JSONObject phone = getTextFields("text", "");
        properties.put("phone", phone);

        //邮箱
        JSONObject email = getTextFields("text", "");
        properties.put("email", email);

        //简介
        JSONObject introduction = new JSONObject();
        introduction.put("include_in_all", true);
        introduction.put("type", "text");
        introduction.put("analyzer", "ik_max_word");
        properties.put("introduction", introduction);

        //专业
        JSONObject majors = new JSONObject();
        majors.put("include_in_all", true);
        majors.put("type", "text");
        majors.put("analyzer", "ik_max_word");
        properties.put("majors", majors);


        structure.put("properties", properties);


        return structure;
    }

    private static JSONObject getTextFields(String type, String analyzer) {
        JSONObject result = new JSONObject();
        result.put("type", "text");
        JSONObject fieldConfig = new JSONObject();

        JSONObject typeObject = new JSONObject();
        typeObject.put("type", type);

        fieldConfig.put(type, typeObject);

        result.put("fields", fieldConfig);
        return  result;
    }

    private static JSONObject getLongField() {
        JSONObject result = new JSONObject();
        result.put("type", "long");

        return result;
    }

    private static final String SQL_SELECT_UNIVERSITY_PAGED_TEMPLATE = "SELECT id, name university, city, phone, email, introduction, majors FROM spider_university_gx211 WHERE id > :start AND id <= :end ORDER BY id";
    public void doSave() throws Exception {
        SingleNumberPagedExecutor sqlExecutor = new SingleNumberPagedExecutor() {
            @Override
            public void afterRetrieveData(JSONArray result) throws Exception {
                saveUniversities(result);
            }
        };
        sqlExecutor.setConn(conn);
        sqlExecutor.setAutoIncrementColumnName("id");
        sqlExecutor.setRecordCountPerPage(20);
        sqlExecutor.setSqlTemplate(SQL_SELECT_UNIVERSITY_PAGED_TEMPLATE);
        sqlExecutor.run();
    }
    private void saveUniversities(JSONArray universities) throws IOException {
        for (int i = 0; i < universities.size(); i ++) {
            JSONObject university = universities.getJSONObject(i);
            String majors = university.getString("majors");
            majors = majors.replace("\"", "").replace("[", "").replace("{", "").replace("]},", "\r").replace("]", "");
            university.put("majors", majors);
        }

        DocumentHelper documentHelper = new DocumentHelper();
        documentHelper.setIndexHelper(helper);
        documentHelper.setIndexName(INDEX_NAME);
        documentHelper.setTypeName(TYPE_NAME);
        documentHelper.addDocuments(universities);
    }

    public static void main(String[] args) throws Exception {
        ApplicationContextInitiator.init();

        String key = GlobalApplicationContext.getInstance().getStringParameter("secretDatasourceKey");
        ClientHelper helper = new ClientHelper();
        helper.setClusterName(CLUSTER_NAME);
        helper.addNode(CLUSTER_ADDRESS);
        helper.setClientTransportSniff(false);
        try (TransportClient client = helper.getClient();
             Connection conn = ConnectionManager.getConnection("DATA-ANALYZE", key)) {
            GaoKaoPaiUniversityEsSaver saver = new GaoKaoPaiUniversityEsSaver();
            IndexHelper indexHelper = new IndexHelper();
            indexHelper.setClient(client);
            saver.setHelper(indexHelper);
            saver.setConn(conn);


            //indexHelper.deleteIndex(INDEX_NAME);
            //System.out.println("delete index successes");
            //saver.createIndex();
            //System.out.println("create index successes");
            saver.truncateMapping();
            System.out.println("truncate type successes");
            //saver.createMapping();
            //System.out.println("create type successes");
            saver.doSave();


            CustomSearchHelper searchHelper = new CustomSearchHelper();
            searchHelper.setClient(client);
            searchHelper.setIndexName(INDEX_NAME);
            searchHelper.setTypeName(TYPE_NAME);


            searchHelper.addMatchPhase("city", "上海");
            searchHelper.setStart(10);
            searchHelper.setSize(5);
            searchHelper.addSort("id", SortOrder.ASC);
            //searchHelper.addFullTextMatch("introduction", "独立");

            //SearchResponse response = client.prepareSearch(INDEX_NAME).setTypes(TYPE_NAME).setQuery(searchHelper.getQueryBuilder()).setSearchType(SearchType.QUERY_THEN_FETCH).setExplain(true).get();
            //SearchHits hits = response.getHits();


            SearchResponse response = searchHelper.getSearchResponse(searchHelper.getQueryBuilder());
            SearchHits hits = response.getHits();
            System.out.println(hits.getTotalHits());
            System.out.println(hits.getHits().length);
            hits.forEach(hit -> {
                    JSONObject entry = JSONObject.fromObject(hit.getSourceAsString());
                    System.out.println(entry.getString("id") + entry.getString("university"));
            });


            /*
            SearchResponse response = client.prepareSearch(INDEX_NAME).setTypes(TYPE_NAME).setQuery(QueryBuilders.boolQuery().must(matchPhraseQuery("city", "上海"))).setFrom(5).setSize(5).addSort("id", SortOrder.ASC).get();
            SearchHits hits = response.getHits();
            System.out.println(hits.getTotalHits());
            System.out.println(hits.getHits().length);
            hits.forEach(hit -> System.out.println(hit.getSourceAsString()));
            */
        }
    }

    /**
     * 创建索引
     */
    public void createIndex() throws UnknownHostException {

        helper.createIndex(INDEX_NAME, IndexHelper.getDefaultIndexConfig());

    }

    /**
     * 删除类型（映射）
     */
    public void truncateMapping() {
        helper.deleteType(INDEX_NAME, TYPE_NAME);
    }

    /**
     * 创建类型（映射）
     */
    public void createMapping() {
        helper.createType(INDEX_NAME, TYPE_NAME, getUniversityTypeStructure());
    }

    public void setHelper(IndexHelper helper) {
        this.helper = helper;
    }

    public void setConn(Connection conn) {
        this.conn = conn;
    }
}

