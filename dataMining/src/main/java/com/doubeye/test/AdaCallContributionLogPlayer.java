package com.doubeye.test;

import com.doubeye.commons.utils.elasticsearch.ClientHelper;
import com.doubeye.commons.utils.elasticsearch.CustomSearchHelper;
import com.doubeye.commons.utils.file.FileUtils;
import com.doubeye.core.log.replay.player.LogPlayer;
import com.doubeye.core.log.replay.retriever.EsLogRetriever;
import com.doubeye.log.replay.result.formatter.AdaContributionResultFormatter;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.elasticsearch.client.transport.TransportClient;

import java.io.IOException;

/**
 * @author doubeye
 * 重放ada录音贡献度
 */
public class AdaCallContributionLogPlayer {
    public static void main(String[] args) throws IOException {
        ClientHelper clientHelper = new ClientHelper();
        clientHelper.setClusterName("hxsd-bd");
        clientHelper.addNode("10.2.24.57:9300");
        CustomSearchHelper searchHelper = new CustomSearchHelper();
        searchHelper.addMatchPhase("request_url", "http://record.hxsd.local/api/record/report/get_personal_contribute");
        long recordCount = 0;

        LogPlayer player = new LogPlayer();
        player.setApiUrl("http://record.hxsd.local/api/record/report/get_personal_contribute_es");
        player.setMethod("GET");
        try (TransportClient client = clientHelper.getClient()){
            searchHelper.setClient(client);
            searchHelper.setIndexName("record_request_logs");
            searchHelper.setTypeName("request_logs");

            EsLogRetriever logRetriever = new EsLogRetriever(searchHelper);
            JSONArray result;

            AdaContributionResultFormatter formatter = new AdaContributionResultFormatter();

            while ((result = logRetriever.retrieveNext()) != null) {
                recordCount += result.size();
                for (int i = 0; i < result.size(); i ++) {
                    JSONObject entry = result.getJSONObject(i);
                    try {

                        JSONObject parameters = entry.getJSONObject("request_params");
                        player.setParameters(parameters);
                        JSONObject esResponse = formatter.format(JSONObject.fromObject(player.play()));
                        esResponse.remove("success");
                        esResponse.remove("errorCode");
                        esResponse.remove("errorMessage");
                        JSONObject mysqlResponse = formatter.format(entry.getJSONObject("response_params"));


                        String id = entry.getString("_id");
                        if (!esResponse.toString().equalsIgnoreCase(mysqlResponse.toString())) {
                            String fileName = "d:/result/get_personal_contribute_" + id + "_mysql" + ".txt";
                            FileUtils.toFile(fileName, mysqlResponse);
                            fileName = "d:/result/get_personal_contribute_" + id + "_es" + ".txt";;
                            FileUtils.toFile(fileName, esResponse);
                            fileName = "d:/result/get_personal_contribute_" + id + "_parameters" + ".txt";;
                            FileUtils.toFile(fileName, parameters);

                        } else {
                            System.out.println(entry.getString("_id") + " " + "passed test");
                        }
                    } catch (Exception e) {
                        System.err.println(e.getMessage());
                    }
                }
            }
        }
    }
}
