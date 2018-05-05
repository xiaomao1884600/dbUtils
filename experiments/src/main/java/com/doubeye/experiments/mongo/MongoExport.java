package com.doubeye.experiments.mongo;

import com.mongodb.*;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;


import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MongoExport {
    public static void main(String[] args) {
        MongoClient client = new MongoClient("10.2.20.227", 27017);
        MongoDatabase db = client.getDatabase("xiaoneng");
        MongoCollection<Document> collection = db.getCollection("customData_20170712");
        FindIterable<Document> cursor = collection.find();
        List<String> records = new ArrayList<>();
        for (Document document : cursor) {
            records.addAll(processDBObject(document));
        }

        for (String record : records) {
            System.out.println(record);
        }
    }

    private static List<String> processDBObject(Object object) {
        List<String> result = new ArrayList<>();
        int index = 1;
        JSONObject json = JSONObject.fromObject(object);
        while (true) {
            if (json.has(index + "")) {
                JSONArray array = json.getJSONArray(index + "");
                for (int i = 0; i < array.size(); i ++) {
                    StringBuilder line = new StringBuilder();
                    JSONObject record = array.getJSONObject(i);
                    Set keys = record.keySet();
                    for (Object key : keys) {
                        String fieldValue = record.getString(key.toString());
                        line.append("\"").append(fieldValue.replace("\"", "\\\"")).append("\"").append(",");
                    }
                    result.add(StringUtils.removeEnd(line.toString(), ","));
                }
                index ++;
            } else {
                break;
            }
        }
        return result;
    }


}
