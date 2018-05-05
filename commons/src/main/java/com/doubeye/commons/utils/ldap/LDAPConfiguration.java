package com.doubeye.commons.utils.ldap;

import javax.naming.Context;
import java.util.Hashtable;

/**
 * @author doubeye
 * @version 1.0.0
 * LDAP配置
 */
@SuppressWarnings("unused")
public class LDAPConfiguration {
    /**
     * ldap服务的url
     */
    private String url;
    /**
     * 认证方式
     */
    private String securityAuthentication;
    /**
     * LDAP驱动
     */
    private static final String LDAP_FACTORY = "com.sun.jndi.ldap.LdapCtxFactory";
    /**
     * 用户名密码认证方式
     */
    @SuppressWarnings("WeakerAccess")
    public static final String SECURITY_AUTHENTICATION_SIMPLE = "simple";

    /**
     * 获得LDAP环境参数
     * @return LDAP环境参数
     */
    public Hashtable<String, String> getLDAPEnvironment() {
        Hashtable<String, String> ldapEnvironment = new Hashtable<>();
        ldapEnvironment.put(Context.SECURITY_AUTHENTICATION, securityAuthentication);
        ldapEnvironment.put(Context.PROVIDER_URL, url);
        ldapEnvironment.put(Context.INITIAL_CONTEXT_FACTORY, LDAP_FACTORY);
        return ldapEnvironment;
    }


    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSecurityAuthentication() {
        return securityAuthentication;
    }
    @SuppressWarnings("WeakerAccess")
    public void setSecurityAuthentication(String securityAuthentication) {
        this.securityAuthentication = securityAuthentication;
    }
}
