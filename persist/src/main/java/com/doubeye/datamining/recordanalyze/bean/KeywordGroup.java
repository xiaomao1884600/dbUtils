package datamining.recordanalyze.bean;

import net.sf.json.JSONArray;

/**
 * @author doubeye
 * @version 1.0.0
 * 关键词组Bean
 */
public class KeywordGroup {
    /**
     * 自增id
     */
    private int id = 0;
    /**
     * 关键词组名称
     */
    private String name;
    /**
     * 关键词
     */
    private String keywords;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }
}
