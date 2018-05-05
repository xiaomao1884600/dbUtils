package com.doubeye.experiments.recommandEngine;

import com.doubeye.commons.application.ConnectionManager;
import com.doubeye.commons.application.GlobalApplicationContext;
import com.doubeye.commons.database.sql.SingleNumberPagedExecutor;
import com.doubeye.commons.utils.test.ApplicationContextInitiator;
import com.doubeye.experiments.recommandEngine.beans.BehaviorBean;
import net.sf.json.JSONArray;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BehaviorGenerator {

    public void init() throws Exception {
        allUsers = fillUsers(conn);
        allItems = fillItems(conn);
        allBehaviors = fillBehaviors();
    }

    public void export() throws Exception {
        init();
        BufferedWriter writer = new BufferedWriter(new FileWriter(exportFileName));
        try {
            for (long i = 1; i < recordCount; i++) {
                writer.write(getBehavior());
            }
        } finally {
            writer.flush();
            writer.close();
        }
    }

    private String getBehavior() throws ParseException {
        long userId = getRandomUser();
        long itemId = getRandomItem();
        BehaviorBean behavior = getRandomBehavior();
        String time = getRandomTime();
        return getRecordLine(userId, itemId, behavior, time);
    }

    private String getRandomTime() throws ParseException {
        String dateTimeString = defaultDate + " " + (random.nextInt(24)) + ":" + random.nextInt(60) + ":"
                + random.nextInt(60);
        return  dateTimeString;
        // SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // return sdf.parse(dateTimeString).getTime() / 1000;
    }

    private String getRecordLine(long userId, long itemId, BehaviorBean behavior, String time) {
        StringBuilder builder = new StringBuilder();
        builder.append(userId).append("\t")
                .append(itemId).append("\t")
                .append(behavior.getBehaviorType()).append("\t")
                .append(behavior.getBehaviorAmout()).append("\t")
                .append(behavior.getHebaviorCount()).append("\t")
                .append(time).append("\t")
                .append("\t")//content
                .append("\t")//media_type
                .append("\t")//pos_type
                .append("\t")//position
                .append("\t")//env
                .append("\n");//trace_id
        return builder.toString();
    }

    List<Long> allUsers;
    List<Long> allItems;
    List<BehaviorBean> allBehaviors;


    private Connection conn;
    private long recordCount = 100000;
    private String exportFileName;
    private String defaultDate;
    private Random random = new Random();

    public Connection getConn() {
        return conn;
    }

    public void setConn(Connection conn) {
        this.conn = conn;
    }

    public long getRecordCount() {
        return recordCount;
    }

    public void setRecordCount(long recordCount) {
        this.recordCount = recordCount;
    }

    public String getExportFileName() {
        return exportFileName;
    }

    public void setExportFileName(String exportFileName) {
        this.exportFileName = exportFileName;
    }

    public String getDefaultDate() {
        return defaultDate;
    }

    public void setDefaultDate(String defaultDate) {
        this.defaultDate = defaultDate;
    }

    private static List<Long> fillUsers(Connection conn) throws Exception {
        List<Long> result = new ArrayList<>();
        SingleNumberPagedExecutor executor = new SingleNumberPagedExecutor() {
            /*
            @Override
            public void afterRetrieveData(ResultSet rs) throws SQLException, Exception {
                result.add(rs.getLong("passportid"));
            }
            */
            public void afterRetrieveData(JSONArray result) throws SQLException, Exception {
                for (int i = 0; i < result.size(); i ++) {
                    result.add(result.getJSONObject(i).getLong("passportid"));
                }
            }
        };
        executor.setRecordCountPerPage(1000);
        executor.setConn(conn);
        executor.setSqlTemplate(SQL_SELECT_ALL_PASSPORT_ID);
        executor.setAutoIncrementColumnName("passportid");
        executor.run();
        return result;
    }
    private static final String SQL_SELECT_ALL_PASSPORT_ID = "SELECT passportid FROM t_user_from_passport WHERE passportid > :start AND  passportid <= :end";

    private static List<Long> fillItems(Connection conn) throws Exception {
        List<Long> result = new ArrayList<>();
        SingleNumberPagedExecutor executor = new SingleNumberPagedExecutor() {
            /*
            @Override
            public void afterRetrieveData(ResultSet rs) throws SQLException, Exception {
                result.add(rs.getLong("id"));
            }
            */
            public void afterRetrieveData(JSONArray result) throws SQLException, Exception {
                for (int i = 0; i < result.size(); i ++) {
                    result.add(result.getJSONObject(i).getLong("passportid"));
                }
            }
        };
        executor.setRecordCountPerPage(1000);
        executor.setConn(conn);
        executor.setSqlTemplate(SQL_SELECT_ALL_ARTICLE_ID);
        executor.setAutoIncrementColumnName("id");
        executor.run();
        return result;
    }

    private List<BehaviorBean> fillBehaviors() {
        List<BehaviorBean> result = new ArrayList<>();
        result.add(getBehaviorInstance("grade", random.nextInt(11), 1));
        /*
        //view
        result.add(getBehaviorInstance("view", 0, 1));
        //click
        result.add(getBehaviorInstance("click", 1, 1));
        //collect
        result.add(getBehaviorInstance("collect"));
        //uncoleect
        result.add(getBehaviorInstance("uncollect"));
        //comment
        result.add(getBehaviorInstance("comment"));
        //like
        result.add(getBehaviorInstance("like"));
        //dislike
        result.add(getBehaviorInstance("dislike"));
        //use
        result.add(getBehaviorInstance("use"));
        */
        return result;
    }



    private BehaviorBean getBehaviorInstance(String type, int amount, int count) {
        BehaviorBean bean = new BehaviorBean();
        bean.setBehaviorType(type);
        bean.setBehaviorAmout(amount);
        bean.setHebaviorCount(count);
        return bean;
    }
    private BehaviorBean getBehaviorInstance(String type) {
        BehaviorBean bean = new BehaviorBean();
        bean.setBehaviorType(type);
        bean.setBehaviorAmout(1);
        bean.setHebaviorCount(1);
        return bean;
    }

    private long getRandomUser() {
        return allUsers.get(random.nextInt(allUsers.size()));
    }

    private long getRandomItem() {
        return allItems.get(random.nextInt(allItems.size()));
    }

    private BehaviorBean getRandomBehavior() {
        return allBehaviors.get(random.nextInt(allBehaviors.size()));
    }

    private static final String SQL_SELECT_ALL_ARTICLE_ID = "SELECT id FROM t_user_articles WHERE id > :start AND  id <= :end";


    public static void main(String[] args) throws Exception {
        ApplicationContextInitiator.init();
        String encrytKey = GlobalApplicationContext.getInstance().getStringParameter("dataSourceKey");
        Connection conn = ConnectionManager.getConnection("REC-ENGINE-228", encrytKey);
        BehaviorGenerator generator = new BehaviorGenerator();
        generator.setConn(conn);
        generator.setDefaultDate("2017-06-12");
        generator.setExportFileName("d:/generatedBehavior.txt");
        generator.init();
        generator.export();
    }
}
