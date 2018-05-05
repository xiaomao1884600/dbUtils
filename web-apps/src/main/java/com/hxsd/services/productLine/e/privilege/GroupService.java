package com.hxsd.services.productLine.e.privilege;

import com.doubeye.commons.application.ConnectionManager;
import com.doubeye.commons.application.GlobalApplicationContext;
import com.doubeye.commons.database.connection.ConnectionHelper;
import com.doubeye.commons.utils.request.RequestHelper;
import com.hxsd.productLine.studentInfo.e.privilege.group.Group;
import net.sf.json.JSONArray;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;


/**
 * Created by doubeye(doubeye@sina.com) on 2016/12/20.
 */
public class GroupService {
    public JSONArray getAllGroups(Map<String, String[]> parameters) throws Exception {
        Connection conn = null;
        String dataSource = RequestHelper.getString(parameters, "dataSource");
        String encrytKey = GlobalApplicationContext.getInstance().getStringParameter("dataSourceKey");
        try {
            conn = ConnectionManager.getConnection(dataSource, encrytKey);
            Group group = new Group();
            group.setConn(conn);
            return group.getAllGroups();
        } finally {
            ConnectionHelper.close(conn);
        }
    }
}
