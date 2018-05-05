package com.doubeye.commons.utils.ldap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import java.util.Hashtable;

/**
 * @author doubeye
 * @version 1.0.0
 * LDAP工具
 */
@SuppressWarnings("WeakerAccess")
public class LDAPUtils {
    private static Logger logger = LogManager.getLogger(LDAPUtils.class);
    /**
     * 用户名密码错误异常特征
     */
    private static final String WRONG_USERNAME_OR_PASSWORD_SIGNATURE = "error code 49";
    @SuppressWarnings("WeakerAccess")
    public static LDAPVerificationResult identityVerification(Hashtable<String, String> ldapEnvironment, String userName, String password) {
        LDAPVerificationResult result = new LDAPVerificationResult();
        ldapEnvironment.put(Context.SECURITY_PRINCIPAL, userName);
        ldapEnvironment.put(Context.SECURITY_CREDENTIALS, password);
        DirContext ctx = null;
        try {
            ctx = new InitialDirContext(ldapEnvironment);
            result.setSuccess(true);
        } catch (NamingException e) {
            String errorMessage = e.getMessage();
            if (errorMessage.contains(WRONG_USERNAME_OR_PASSWORD_SIGNATURE)) {
                result.setWrongUserNameOrPassword(true);
            } else {
                result.setOtherErrorMessage(e.getMessage());
            }
        } finally {
            try {
                if (ctx != null) {
                    ctx.close();
                }
            } catch (NamingException e) {
                logger.error(e.getMessage());
            }
        }
        return result;
    }
}
