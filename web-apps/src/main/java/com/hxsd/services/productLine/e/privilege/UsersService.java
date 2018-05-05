package com.hxsd.services.productLine.e.privilege;

import com.doubeye.commons.application.ConnectionManager;
import com.doubeye.commons.application.GlobalApplicationContext;
import com.doubeye.commons.database.connection.ConnectionHelper;
import com.doubeye.commons.utils.request.RequestHelper;
import com.hxsd.productLine.studentInfo.e.privilege.group.Group;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

/**
 * Created by doubeye(doubeye@sina.com) on 2017/4/2.
 */
public class UsersService {
    public JSONArray getAllChannelUsers(Map<String, String[]> parameters) throws Exception {
        Connection conn = null;
        String dataSource = RequestHelper.getString(parameters, "dataSource");
        String encrytKey = GlobalApplicationContext.getInstance().getStringParameter("dataSourceKey");
        try {
            conn = ConnectionManager.getConnection(dataSource, encrytKey);
            JSONObject object = new JSONObject();
            Group group = new Group();
            group.setConn(conn);
            object.put("channelUsers", group.getAllUserByGroup(PERMISSION_CHANNEL_USERS));
            object.put("channelManagers", group.getAllUserByGroup(PERMISSION_CHANNEL_MANAGERS));
            JSONArray result = new JSONArray();
            result.add(object);
            return result;
        } finally {
            ConnectionHelper.close(conn);
        }
    }

    private static final String PERMISSION_CHANNEL_USERS = "data_channel_users";
    private static final String PERMISSION_CHANNEL_MANAGERS = "data_channel_managerview";
}
