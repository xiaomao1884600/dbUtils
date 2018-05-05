package com.doubeye.core.authorization.service;

import com.doubeye.commons.application.GlobalApplicationContext;
import com.doubeye.commons.database.ResultSet.ResultSetWarpper.ResultSetJSONWrapper;
import com.doubeye.commons.jsonBuilder.JSONArrayTreeWrapper;
import com.doubeye.commons.utils.request.RequestHelper;

import com.doubeye.commons.utils.test.ApplicationContextInitiator;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;


import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;


/**
 * @author doubeye
 * @version 1.0.0
 * 菜单服务
 */
@SuppressWarnings("unused | WeakerAccess")
public class MenuService {
    public JSONArray getAllMenus(Map<String, String[]> params) throws Exception {
       return getMenus("");
    }


    public JSONArray getAllMenusByUserId(Map<String, String[]> params) throws Exception {
        String userId = RequestHelper.getString(params, "_userId");
        return getMenus(userId);

    }

    private static final String SQL_SELECT_MENUS_BY_USER_ID = "SELECT id menuId, name name , identifier, component, if(length(component) > 0, 'link', 'toggle') type, parent_id, `order` FROM core_menu WHERE id IN (SELECT rm.menu_id FROM core_user u, core_user_role ur, core_role r, core_role_menu rm WHERE u.user_id = ur.user_id AND ur.role_id = rm.role_id AND u.user_id = '%s')";
    private static final String SQL_SELECT_ALL_MENUS = "SELECT id menuId, name name , identifier, component, if(length(component) > 0, 'link', 'toggle') type, parent_id, `order` FROM core_menu";


    private static JSONArray getMenus(String userId) throws Exception {
        String sql = getMenusSQL(userId);
        try (Connection conn = GlobalApplicationContext.getInstance().getCoreConnection()){
            JSONArray menus = ResultSetJSONWrapper.getJSONArrayFromSQL(conn, sql);
            JSONArrayTreeWrapper tw = new JSONArrayTreeWrapper();
            tw.setOriginArray(menus);
            tw.setIdPropertyName("menuId");
            tw.setIdPropertyNameInResult("menuId");
            tw.setTextPropertyName("name");
            tw.setTextPropertyNameInResult("name");
            tw.setParentIdPropertyName("parent_id");
            tw.setChildPropertyName("pages");
            tw.setOrderColumnName("order");
            JSONObject condition = new JSONObject();
            condition.put("parent_id", 1);
            tw.setTopCondition(condition);
            Map<String, String> otherColumns = new HashMap<>(4);
            otherColumns.put("component", "state");
            otherColumns.put("identifier", "id");
            otherColumns.put("type", "type");
            otherColumns.put("order", "order");
            tw.setInterestingColumns(otherColumns);
            return tw.generateTree();
        }
    }

    private static String getMenusSQL(String userId) {
        if (StringUtils.isEmpty(userId)) {
            return SQL_SELECT_ALL_MENUS;
        } else {
            return String.format(SQL_SELECT_MENUS_BY_USER_ID, userId);
        }
    }

    public static void main(String[] args) throws Exception {
        ApplicationContextInitiator.init();
        System.out.println(new MenuService().getAllMenus(null));
    }
}
