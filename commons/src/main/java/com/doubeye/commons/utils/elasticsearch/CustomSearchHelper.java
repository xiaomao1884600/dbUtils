package com.doubeye.commons.utils.elasticsearch;

import net.sf.json.JSONObject;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;

import java.util.Set;

import static org.elasticsearch.index.query.QueryBuilders.matchPhraseQuery;
import static org.elasticsearch.index.query.QueryBuilders.rangeQuery;
import static org.elasticsearch.index.query.QueryBuilders.termQuery;


/**
 * @author doubeye
 * 自定义的搜索器
 */
public class CustomSearchHelper extends SearchHelper{
    private JSONObject matchPhrase = new JSONObject();
    private JSONObject fullTextMatches = new JSONObject();
    private JSONObject rangeConditions = new JSONObject();



    public void addMatchPhase(String fields, String phrase) {
        matchPhrase.put(fields, phrase);
    }

    public void addFullTextMatch(String fields, String phrase) {
        fullTextMatches.put(fields, phrase);
    }

    /**
     * 添加范围条件
     * @param field 字段名
     * @param range 范围对象，结构为JSONObject，属性及意义如下：
     *              start 范围的起始值
     *              end 范围的结束值
     */
    public void addRangeCondition(String field, JSONObject range) {
        rangeConditions.put(field, range);
    }





    public QueryBuilder getQueryBuilder() {

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        if (matchPhrase.size() > 0) {
            getMatchPhrases(boolQueryBuilder, matchPhrase);
        }
        if (fullTextMatches.size() > 0) {
            getMatches(boolQueryBuilder, fullTextMatches);
        }
        //添加范围查询
        if (rangeConditions.size() > 0) {
            getRanges(boolQueryBuilder, rangeConditions);
        }

        return boolQueryBuilder;
    }

    private static void getMatchPhrases(BoolQueryBuilder builder, JSONObject query) {
        Set<?> fields = query.keySet();
        for (Object field : fields) {
            String value = query.getString(field.toString());
            String fieldName = field.toString();
            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
            if (value.contains(",")) {
                String[] values = value.split(",");
                for (String singleValue : values) {
                    boolQueryBuilder.should(matchPhraseQuery(fieldName, singleValue));
                }
                builder.must(boolQueryBuilder);
            } else {
                builder.must(matchPhraseQuery(fieldName, value));
            }
        }
    }

    private static void getMatches(BoolQueryBuilder builder, JSONObject query) {
        Set<?> fields = query.keySet();
        for (Object field : fields) {
            String value = query.getString(field.toString());
            String fieldName = field.toString();
            builder.must(termQuery(fieldName, value));
        }
    }

    private static void getRanges(BoolQueryBuilder builder, JSONObject query) {
        Set<?> fields = query.keySet();
        for (Object field : fields) {
            if (query.get(field) instanceof JSONObject) {
                JSONObject condition = query.getJSONObject(field.toString());
                RangeQueryBuilder range = rangeQuery(field.toString());
                if (condition.containsKey("start")) {
                    range.gt(condition.getString("start")).includeLower(true);
                }
                if (condition.containsKey("end")) {
                    range.lte(condition.get("end")).includeLower(true);
                }
                if (condition.containsKey("start") || condition.containsKey("end")) {
                    builder.must(range);
                }
            }
        }
    }

    public static void main(String[] args) {
        CustomSearchHelper helper = new CustomSearchHelper();
        helper.addFullTextMatch("introduction", "分校");
        helper.addMatchPhase("city", "秦皇岛");
        System.out.println(helper.getQueryBuilder().toString());

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(termQuery("introduction", "复旦"));
        boolQueryBuilder.must(matchPhraseQuery("city", "秦皇岛"));

    }


}
