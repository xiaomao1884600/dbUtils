package com.doubeye.experiments.elasticSearch;



import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
//import org.apache.commons.math.stat.descriptive.SummaryStatistics;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.cluster.node.DiscoveryNode;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.transport.TransportAddress;

import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.avg.AvgAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.max.MaxAggregationBuilder;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.elasticsearch.xpack.client.PreBuiltXPackTransportClient;


import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirstTest {
    public static void main(String[] args) throws IOException {
        /*
        String request1 = "POST /remind/getnoreadassign?v=1500278509440 HTTP/1.1";
        String request2 = "GET /xncreate?kfid=hxsdlmj&uid=guestD4D90D26-8A34-89&ip=221.180.45.186&province=%E5%B1%B1%E8%A5%BF&city=%E6%99%8B%E4%B8%AD&devicetype=3&v=fsdfsfgghad HTTP/1.1";
        System.out.println(getController(request1));
        System.out.println(getController(request2));
        */
        //testXieWenhao();
        testZaiJinxin();
        //testStatusNot200();
    }

    private static void testStatusNot200() throws UnknownHostException {
        long t1 = System.currentTimeMillis();
        Settings settings = Settings.builder().put("cluster.name", "elasticsearch").put("client.transport.sniff", true).build();
        TransportClient client = new PreBuiltTransportClient(settings);
        TransportAddress address = new InetSocketTransportAddress(InetAddress.getByName("10.2.24.235"), 9300);
        client.addTransportAddress(address);


        SearchResponse searchResponse = client.prepareSearch("jc-e-jc-e-2017.07.17").setTypes("jc-e").setQuery(QueryBuilders.boolQuery().mustNot(QueryBuilders.termQuery("status", "200"))).setFetchSource(new String[] {"request", "status"}, null).setScroll(new TimeValue(10000)).setFrom(0).setSize(200).execute().actionGet();
        SearchHits hits = searchResponse.getHits();
        Map<String, Integer> result = new HashMap<>();
        long count = 0;
        while (true) {
            System.out.println(count);
            if (searchResponse.getHits().getHits().length == 0) {
                break;
            }
            SearchHits requestHits = searchResponse.getHits();
            for (SearchHit hit : requestHits) {
                JSONObject record = JSONObject.fromObject(hit.getSourceAsString());
                String request = getController(record.getString("request"));
                String status = record.getString("status");
                String resultKey = request + " " + status;
                int value;
                if (result.containsKey(resultKey)) {
                    value = result.get(resultKey);
                } else {
                    value = 0;
                    result.put(resultKey, value);
                }
                result.put(resultKey, value + 1);
                count ++;
            }
            searchResponse = client.prepareSearchScroll(searchResponse.getScrollId()).setScroll(new TimeValue(10000)).execute().actionGet();
        }
        System.out.println("row count = " + count);



        long t2 = System.currentTimeMillis();
        System.out.println("it takes " + (t2 - t1) + "milli-seconds to retrieve records");
        JSONArray jsonArray = new JSONArray();
        result.forEach((key, value) -> {
            JSONObject object = new JSONObject();
            String[] requestResult = key.split(" ");
            object.put("request", requestResult[0]);
            object.put("status", requestResult[1]);
            object.put("cnt", value);
            jsonArray.add(object);
        });

        long t3 = System.currentTimeMillis();
        System.out.println(jsonArray.toString());
        System.out.println("it takes " + (t3 - t2) + "milli-seconds to compute");
    }

    private static void testXieWenhao() throws UnknownHostException {
    //以下几行是连接谢文豪的服务
        Settings settings = Settings.builder().put("cluster.name", "hxsd-es").put("client.transport.sniff", true).put("xpack.security.user", "elastic:changeme").put("xpack.security.transport.ssl.enabled", false).build();
        TransportClient client = new PreBuiltXPackTransportClient(settings);
        TransportAddress address = new InetSocketTransportAddress(InetAddress.getByName("10.2.20.59"), 9300);
        client.addTransportAddress(address);
        address = new InetSocketTransportAddress(InetAddress.getByName("10.2.20.66"), 9300);
        client.addTransportAddress(address);
        address = new InetSocketTransportAddress(InetAddress.getByName("10.2.20.59"), 9300);
        client.addTransportAddress(address);

        GetResponse getResponse = client.prepareGet("edu", "feedback", "AV0_2GbE8eQwtHig0jD5").setOperationThreaded(false).get();
        System.out.println(getResponse.getSourceAsString());

        SearchResponse searchResponse = client.prepareSearch("edu").setTypes("feedback").setQuery(QueryBuilders.matchAllQuery()).setFrom(0).setSize(100).get();
        SearchHits hits = searchResponse.getHits();
        hits.forEach(hit -> System.out.println(hit.getSourceAsString()));
    }


    private static void testZaiJinxin() throws UnknownHostException {
        //以下3行为连接宰金鑫的服务
        long t1 = System.currentTimeMillis();
        Settings settings = Settings.builder().put("cluster.name", "elasticsearch").put("client.transport.sniff", true).build();
        TransportClient client = new PreBuiltTransportClient(settings);
        // TransportAddress address = new InetSocketTransportAddress(InetAddress.getByName("10.2.24.235"), 9300);
        TransportAddress address = new InetSocketTransportAddress(InetAddress.getByName("10.2.20.114"), 9300);
        client.addTransportAddress(address);

        List<DiscoveryNode> connectedNodes = client.connectedNodes();
        connectedNodes.forEach(node -> System.out.println(node.getHostName() + " " + node.getHostAddress()));

        /*
        SearchResponse searchResponse = client.prepareSearch("jc-e-jc-e-2017.07.16").setTypes("jc-e").setQuery(QueryBuilders.matchAllQuery()).setFrom(0).setSize(100).get();
        SearchHits hits = searchResponse.getHits();
        hits.forEach(hit -> System.out.println(hit.getSourceAsString()));
        */

        SearchResponse searchResponse = client.prepareSearch("jc-e-jc-e-2017.07.20").setTypes("jc-e").setQuery(QueryBuilders.matchAllQuery()).setFetchSource(new String[] {"request", "request_time"}, null).setScroll(new TimeValue(10000)).setFrom(0).setSize(100).execute().actionGet();
        SearchHits hits = searchResponse.getHits();
        System.out.println(hits.totalHits());
        hits.forEach(hit -> System.out.println(hit.getSourceAsString()));
        Map<String, List<Double>> result = new HashMap<>();
        long count = 0;
        while (true) {
            if (searchResponse.getHits().getHits().length == 0) {
                break;
            }
            System.out.println(count);
            SearchHits requestHits = searchResponse.getHits();
            for (SearchHit hit : requestHits) {
                JSONObject record = JSONObject.fromObject(hit.getSourceAsString());
                String request = getController(record.getString("request"));

                List<Double> values;
                if (result.containsKey(request)) {
                    values = result.get(request);
                } else {
                    values = new ArrayList<Double>();
                    result.put(request, values);
                }
                values.add(record.getDouble("request_time"));
                count ++;
            }
            searchResponse = client.prepareSearchScroll(searchResponse.getScrollId()).setScroll(new TimeValue(10000)).execute().actionGet();
        }
        System.out.println("row count = " + count);


        /*
        long t2 = System.currentTimeMillis();
        System.out.println("it takes " + (t2 - t1) + "milli-seconds to retrieve records");
        SummaryStatistics stats = new SummaryStatistics();
        JSONArray jsonArray = new JSONArray();
        result.forEach((key, values) -> {
            stats.clear();
            values.forEach(stats::addValue);
            JSONObject record = new JSONObject();
            record.put("controllerName", key);
            record.put("times", values.size());
            record.put("min", stats.getMin());
            record.put("max", stats.getMax());
            record.put("mean", stats.getMean());
            record.put("stdev", stats.getStandardDeviation());
            jsonArray.add(record);
            //System.out.println(key + " min=" + stats.getMin() + " max=" + stats.getMax() + " stdev=" + stats.getStandardDeviation() + " mean=" + stats.getMean() + " times=" + values.size());
        });
        */

        long t3 = System.currentTimeMillis();
        //System.out.println(jsonArray.toString());
        //System.out.println("it takes " + (t3 - t2) + "milli-seconds to compute");



        //聚集
        /*
        SearchRequestBuilder sbuilder = client.prepareSearch("jc-e-jc-e-2017.07.17").setTypes("jc-e");
        TermsAggregationBuilder pathAgg = AggregationBuilders.terms("request_time_count").field("request_time");
        sbuilder.addAggregation(pathAgg);
        //MaxAggregationBuilder last = AggregationBuilders.max("avg_last").field("request_time");
        //sbuilder.addAggregation(pathAgg.subAggregation(last));
        SearchResponse response = sbuilder.execute().actionGet();

        SearchHits aggHits = response.getHits();
        aggHits.forEach(hit -> {
            System.out.println(hit.getSourceAsString());
        });

        */

        /*
        SearchResponse sr = client.prepareSearch().setTypes("jc-e")
                .addAggregation(
                        AggregationBuilders.terms("by_path").field("path")
                                        .subAggregation(AggregationBuilders.avg("avg_last").field("request_time")
                                )
                )
                .execute().actionGet();

        SearchHits hits = sr.getHits();
        hits.forEach(hit -> System.out.println(hit.getSourceAsString()));*/
    }

    private static String processRequest(String origin) {
        //System.out.println(origin);
        String withoutParamV = origin.substring(origin.indexOf("/"), (origin.length() - "HTTP/1.1".length() - 1));
        int position = -1;
        if (withoutParamV.contains("?v=")) {
            position = withoutParamV.indexOf("?v=");
        } else if (withoutParamV.contains("&v=")) {
            position = withoutParamV.indexOf("&v=");
        }
        if (position >= 0) {
            withoutParamV = withoutParamV.substring(0, position);
        }
        position = -1;
        if (withoutParamV.contains("?_=")) {
            position = withoutParamV.indexOf("?_=");
        } else if (withoutParamV.contains("&_=")) {
            position = withoutParamV.indexOf("&_=");
        }
        if (position >= 0) {
            withoutParamV = withoutParamV.substring(0, position);
        }
        return withoutParamV;
    }

    private static String getController(String origin) {
        String withoutParamV = origin.substring(origin.indexOf("/"), (origin.length() - "HTTP/1.1".length() - 1));
        if (withoutParamV.contains("?")) {
            return withoutParamV.substring(0, withoutParamV.indexOf("?"));
        } else {
            return withoutParamV;
        }
    }

    public static void getAllNoReadMessageRequest() throws UnknownHostException {
        long t1 = System.currentTimeMillis();
        Settings settings = Settings.builder().put("cluster.name", "elasticsearch").put("client.transport.sniff", true).build();
        TransportClient client = new PreBuiltTransportClient(settings);

        TransportAddress address = new InetSocketTransportAddress(InetAddress.getByName("10.2.20.114"), 9300);
        client.addTransportAddress(address);

        List<DiscoveryNode> connectedNodes = client.connectedNodes();
        connectedNodes.forEach(node -> System.out.println(node.getHostName() + " " + node.getHostAddress()));

        /*
        SearchResponse searchResponse = client.prepareSearch("jc-e-jc-e-2017.07.16").setTypes("jc-e").setQuery(QueryBuilders.matchAllQuery()).setFrom(0).setSize(100).get();
        SearchHits hits = searchResponse.getHits();
        hits.forEach(hit -> System.out.println(hit.getSourceAsString()));
        */

        SearchResponse searchResponse = client.prepareSearch("jc-e-jc-e-2017.07.20").setTypes("jc-e").setQuery(QueryBuilders.matchAllQuery()).setFetchSource(new String[] {"request", "request_time"}, null).setScroll(new TimeValue(10000)).setFrom(0).setSize(100).execute().actionGet();
        SearchHits hits = searchResponse.getHits();

    }

}
