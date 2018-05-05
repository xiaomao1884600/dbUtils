package com.doubeye.core.authorization.service;

import com.doubeye.commons.application.ConnectionManager;
import com.doubeye.commons.application.GlobalApplicationContext;
import com.doubeye.commons.database.ResultSet.ResultSetWarpper.ResultSetJSONWrapper;
import com.doubeye.commons.utils.refactor.RefactorUtils;
import com.doubeye.commons.utils.request.RequestHelper;
import com.doubeye.commons.utils.response.ResponseHelper;
import com.doubeye.commons.utils.string.StringUtils;
import com.doubeye.core.authorization.bean.UserBean;
import com.doubeye.core.authorization.helper.core.CoreOnlineAdaUserHelper;
import com.doubeye.core.authorization.helper.online.RecordOnlineHelper;
import com.doubeye.core.opration.template.OperationTemplate;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.sql.Connection;
import java.util.Map;

/**
 * @author 项目所使用的简单的用户服务
 */
@SuppressWarnings("unused")
public class SimpleUserService {
    public JSONObject addOnlineAda(Map<String, String[]> parameters) throws Exception {
        JSONObject userObject = RequestHelper.getJSONObject(parameters, "user");
        UserBean userBean = new UserBean();
        RefactorUtils.fillByJSON(userBean, userObject, true);
        String secretKey = GlobalApplicationContext.getInstance().getStringParameter("dataSourceKey");
        try (Connection coreConnection = GlobalApplicationContext.getInstance().getCoreConnection();
             Connection conn = ConnectionManager.getConnection("RECORD-ANALYZE-PRODUCT", secretKey)){
            OperationTemplate template = new OperationTemplate();
            template.addOperation(RecordOnlineHelper.getInstance(conn, userBean));
            template.addOperation(CoreOnlineAdaUserHelper.getInstance(coreConnection, userBean));
            template.run();
            return ResponseHelper.getSuccessObject();
        }
    }

    public JSONObject checkUser(Map<String, String[]> parameters) throws Exception {
        JSONObject userObject = RequestHelper.getJSONObject(parameters, "user");
        String secretKey = GlobalApplicationContext.getInstance().getStringParameter("dataSourceKey");
        try (Connection coreConnection = GlobalApplicationContext.getInstance().getCoreConnection();
             Connection conn = ConnectionManager.getConnection("RECORD-ANALYZE-PRODUCT", secretKey)){
            JSONArray onlineUsers = ResultSetJSONWrapper.getJSONArrayFromSQL(conn, StringUtils.format(RecordOnlineHelper.SQL_SELECT_RECORD_ONLINE_USER_BY_DOMAIN_NAME, userObject));
            JSONArray coreUsers = ResultSetJSONWrapper.getJSONArrayFromSQL(coreConnection, StringUtils.format(CoreOnlineAdaUserHelper.SQL_SELECT_CORE_USER, userObject));
            JSONObject result =  ResponseHelper.getSuccessObject();
            result.put("userExists", (onlineUsers.size() > 0 && coreUsers.size() > 0));
            return result;
        }
    }
}
