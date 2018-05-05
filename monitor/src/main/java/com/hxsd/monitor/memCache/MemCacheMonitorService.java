package com.hxsd.monitor.memCache;

import com.doubeye.commons.utils.DateTimeUtils.DateTimeUtils;
import com.doubeye.commons.utils.json.JSONUtils;
import com.doubeye.commons.utils.request.RequestHelper;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;


/**
 * @author doubeye
 * @version 1.0.0
 * 用来获得MemCache监控状态的服务
 * TODO 将ES相关内容进行封装，参考DBUtils
 */
@SuppressWarnings("unused | WeakerAccess")
public class MemCacheMonitorService {
    private static Logger logger = LogManager.getLogger(MemCacheMonitorService.class);

    /**
     * 获得监控结果
     * @param parameters 参数，具体见文档
     * @return 监控结果，具体见文档
     * @throws UnknownHostException 未知主机异常
     */
    public JSONArray getMonitorResult(Map<String, String[]> parameters) throws UnknownHostException {
        JSONArray result = new JSONArray();
        String startTimeString = RequestHelper.getString(parameters, "startTime");
        String endTimeString = RequestHelper.getString(parameters, "endTime");
        String memCache = RequestHelper.getString(parameters, "memCache");

        try (TransportClient client = getElasticSearchClient()) {
            //.addSort("time", SortOrder.ASC)

            //TODO 通过字典读取，消除if - else  	mem-me-2017.08
            String type = "";
            String index = "";
            if ("123_56_154_55:10010".equals(memCache)) {
                index = "mem-me-*";
                type = "mem-me-";
            } else if ("10.2.24.38:11211".equals(memCache)) {
                index = "mem-38*";
                type = "mem-38";
            }
            SearchResponse searchResponse = client.prepareSearch(index).addSort("rand_starttime", SortOrder.ASC).setTypes(type).setQuery(getQuery(startTimeString, endTimeString, memCache)).setFetchSource(new String[]{
                    "startdate", "counts", "bytes", "curr_connections", "threads", "readTime", "writeTime"
            }, null).setScroll(new TimeValue(10000)).setFrom(0).setSize(10000).execute().actionGet();

            SearchHits hits = searchResponse.getHits();

            while (true) {
                if (searchResponse.getHits().getHits().length == 0) {
                    break;
                }
                SearchHits requestHits = searchResponse.getHits();
                for (SearchHit hit : requestHits) {
                    JSONObject record = JSONObject.fromObject(hit.getSourceAsString());
                    result.add(record);
                }
                searchResponse = client.prepareSearchScroll(searchResponse.getScrollId()).setScroll(new TimeValue(10000)).execute().actionGet();
            }
            return result;
        }
    }

    private static TransportClient getElasticSearchClient() throws UnknownHostException {
        Settings settings = Settings.builder().put("cluster.name", "hxsd-loges").put("client.transport.sniff", true).build();
        TransportClient client = new PreBuiltTransportClient(settings);
        TransportAddress address = new InetSocketTransportAddress(InetAddress.getByName("10.2.24.66"), 9300);
        client.addTransportAddress(address);
        return client;
    }


    private static final String QUERY_TEMPLATE = "path:/%s/ AND time:[%d TO %d]";
    private static QueryBuilder getQuery(String startTime, String endTime, String memCache) {
        long start = DateTimeUtils.getDateFromDefaultFormat(startTime).getTime() / 1000;
        long end = DateTimeUtils.getDateFromDefaultFormat(endTime).getTime() / 1000;
        //logger.warn(String.format(QUERY_TEMPLATE, getPathPart(memCache), start, end));
        return QueryBuilders.queryStringQuery(String.format(QUERY_TEMPLATE, getPathPart(memCache), start, end));
    }

    private static String getPathPart(String memCache) {
        return memCache.replace(".", "_").replace(":", "_");
    }


    public static void main(String[] args) throws UnknownHostException {
        MemCacheMonitorService service = new MemCacheMonitorService();
        Map<String, String[]> params = new HashMap<>();
        params.put("startTime", new String[] {"2017-08-28 15:22:35"});
        params.put("endTime", new String[] {"2017-08-28 16:22:35"});
        params.put("memCache", new String[] {"10.2.24.38:11211"});
        System.out.println(service.getMonitorResult(params).toString());
    }
}
