package com.hxsd.monitor;

import com.doubeye.commons.application.GlobalApplicationContext;
import com.doubeye.commons.database.ResultSet.ResultSetWarpper.ResultSetJSONWrapper;
import com.doubeye.commons.utils.refactor.RefactorUtils;
import com.doubeye.commons.utils.test.ApplicationContextInitiator;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class MemCacheServerConfig {
    /**
     * 连接对象
     */
    private Connection conn;


    private static final String SQL_SELECT_ALL = "SELECT id, name, host, port, projects FROM environment_memcache";
    public List<MemCacheServerConfigBean> getAll() throws SQLException {
        List<MemCacheServerConfigBean> configs = new ArrayList<>();
        JSONArray array = ResultSetJSONWrapper.getJSONArrayFromSQL(conn, SQL_SELECT_ALL);
        for (int i = 0; i < array.size(); i ++) {
            JSONObject element = array.getJSONObject(i);
            MemCacheServerConfigBean config = new MemCacheServerConfigBean();
            RefactorUtils.fillByJSON(config, element);
            configs.add(config);
        };
        return configs;
    }

    public static JSONArray toArray(List<MemCacheServerConfigBean> configBeans) {
        JSONArray result = new JSONArray();
        configBeans.forEach(bean -> {
            JSONObject element = JSONObject.fromObject(bean);
            element.put("param", bean.getServerParam());
            result.add(element);
        });
        return result;
    }

    public void setConn(Connection conn) {
        this.conn = conn;
    }

    public static void main(String[] args) throws SQLException {
        ApplicationContextInitiator.init();
        MemCacheServerConfig config = new MemCacheServerConfig();
        try (Connection conn = GlobalApplicationContext.getInstance().getCoreConnection()) {
            config.setConn(conn);
            List<MemCacheServerConfigBean> beans = config.getAll();
            System.out.println(toArray(beans));
        }

    }
}
