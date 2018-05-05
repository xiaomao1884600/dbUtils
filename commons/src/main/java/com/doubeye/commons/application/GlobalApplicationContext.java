package com.doubeye.commons.application;

import com.doubeye.commons.database.connection.ConnectionHelper;
import com.doubeye.commons.database.connection.bean.DataSource;
import com.doubeye.commons.utils.cloud.service.common.authorization.Authorization;
import com.doubeye.commons.utils.ldap.LDAPConfiguration;
import com.doubeye.commons.utils.refactor.RefactorUtils;
import net.sf.json.JSONObject;

import java.sql.Connection;

/**
 * @author doubeye
 * @version 1.0.0
 * 应用服务启动设置，目前提供了初始化核心数据库的功能
 */
public class GlobalApplicationContext {
    /**
     * 单例模式使用
     */
    private static volatile GlobalApplicationContext applicationInitializer = null;
    /**
     * 核心库数据源
     */
    private static DataSource coreDataSource;
    /**
     * 连接助手
     */
    private static ConnectionHelper connectionHelper = null;
    /**
     * 配置对象
     */
    private JSONObject config;

    private static LDAPConfiguration ldapConfiguration;

    /**
     * 构造函数，用来从配置信息中读取核心数据库的配置，并实例化全局的核心数据库连接器助手
     * @param initConfig 配置信息
     */
    private GlobalApplicationContext(JSONObject initConfig) {
        config = initConfig;
        JSONObject coreConnectionConfig = initConfig.getJSONObject(CONFIG_CORE_CONNECTION);
        JSONObject dataSourceConfig = coreConnectionConfig.getJSONObject(CONFIG_DATA_SOURCE);
        coreDataSource = new DataSource();
        RefactorUtils.fillByJSON(coreDataSource, dataSourceConfig);
        String connectionHelperClassName = coreConnectionConfig.getString(CONFIG_CORE_CONNECTION_HELPER_CLASS_NAME);
        connectionHelper = (ConnectionHelper) RefactorUtils.getClassInstanceByName(connectionHelperClassName);
        // 初始化ldap配置
        ldapConfiguration = new LDAPConfiguration();
        ldapConfiguration.setSecurityAuthentication(LDAPConfiguration.SECURITY_AUTHENTICATION_SIMPLE);
        ldapConfiguration.setUrl(initConfig.getString("ldapUrl"));
    }

    /**
     * 初始化函数
     * @param initConfig 初始化配置对象
     */
    public static void init(JSONObject initConfig) {
        if (applicationInitializer == null) {
            synchronized (GlobalApplicationContext.class) {
                if (applicationInitializer == null) {
                    applicationInitializer = new GlobalApplicationContext(initConfig);
                }
            }
        }
    }

    /**
     * 获得应用级上下文的实例
     * @return 应用级上下文
     */
    public static GlobalApplicationContext getInstance() {
        if (applicationInitializer == null) {
            throw new RuntimeException("请先调用GlobalApplicationContext.init(JSONObject initConfig)方法进行初始化");
        }
        return applicationInitializer;
    }

    /**
     * 根据标识获得阿里云权限对象
     * @param identifier 标识符
     * @return 权限对象
     */
    public Authorization getAliyunAuthorizationByIdentifier(String identifier) {
        JSONObject config = getInstance().config;
        if (config.containsKey(CONFIG_ALIYUN_AUTHORIZATION)) {
            JSONObject allAuthorizations = config.getJSONObject(CONFIG_ALIYUN_AUTHORIZATION);
            if (allAuthorizations.containsKey(identifier)) {
                JSONObject authorizationConfig = allAuthorizations.getJSONObject(identifier);
                Authorization authorization = new Authorization();
                RefactorUtils.fillByJSON(authorization, authorizationConfig);
                return authorization;
            } else {
                throw new RuntimeException("配置文件中没有指定的阿里云权限下：" + identifier);
            }
        } else {
            throw new RuntimeException("配置文件中没有设置阿里云权限项");
        }

    }

    /**
     * 获得核心数据库连接器助手
     * @return 核心数据库连接器助手
     */
    private ConnectionHelper getCoreConnectionHelper() {
        return connectionHelper;
    }

    /**
     * 获得核心数据库连接对象
     * @return 核心数据库连接对象
     */
    public Connection getCoreConnection() {
        GlobalApplicationContext context = getInstance();
        return context.getCoreConnectionHelper().getConnection(context.getCoreDataSource());
    }

    /**
     * 获得配置文件中的参数值
     * @param parameterName 参数名
     * @return 参数值
     * TODO here
     */
    public String getStringParameter(String parameterName) {
        if (config.has(parameterName)) {
            return config.getString(parameterName);
        } else {
            return null;
        }
    }

    public void setParameter(String parameterName, String value) {
        config.put(parameterName, value);
    }

    public LDAPConfiguration getLDAPConfigration() {
        return ldapConfiguration;
    }

    /**
     * 获得核心库数据源
     * @return 核心库数据源
     */
    public DataSource getCoreDataSource() {
        return coreDataSource;
    }

    /**
     * 配置信息中的核心数据库类信息key
     */
    private static final String CONFIG_CORE_CONNECTION = "coreDataSource";
    /**
     * 配置信息中的核心数据库连接助手类key
     */
    private static final String CONFIG_CORE_CONNECTION_HELPER_CLASS_NAME = "connectionHelperClassName";
    /**
     * 配置信息中的核心数据库数据源key
     */
    private static final String CONFIG_DATA_SOURCE = "dataSource";

    private static final String CONFIG_ALIYUN_AUTHORIZATION = "aliyunAuthorization";

}
