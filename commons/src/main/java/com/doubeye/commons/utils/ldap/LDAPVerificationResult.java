package com.doubeye.commons.utils.ldap;

/**
 * LDAP认证结果
 */
@SuppressWarnings("unused")
public class LDAPVerificationResult {
    /**
     * 成功
     */
    private boolean success = false;
    /**
     * 用户不存在
     */
    private boolean wrongUserNameOrPassword = false;
    /**
     * 其他错误信息
     */
    private String otherErrorMessage;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        init();
        this.success = success;
    }

    @SuppressWarnings("WeakerAccess")
    public boolean isWrongUserNameOrPassword() {
        return wrongUserNameOrPassword;
    }

    void setWrongUserNameOrPassword(boolean wrongUserNameOrPassword) {
        init();
        this.wrongUserNameOrPassword = wrongUserNameOrPassword;
    }

    public String getOtherErrorMessage() {
        return otherErrorMessage;
    }

    void setOtherErrorMessage(String otherErrorMessage) {
        init();
        this.otherErrorMessage = otherErrorMessage;
    }

    private void init() {
        success = false;
        wrongUserNameOrPassword = false;
        otherErrorMessage = null;
    }
}
