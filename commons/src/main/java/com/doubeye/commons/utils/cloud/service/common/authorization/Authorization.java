package com.doubeye.commons.utils.cloud.service.common.authorization;

/**
 * @author doubeye
 * @version 1.0.0
 * 阿里云身份证则对象
 */
public class Authorization {
    /**
     * access key
     */
    private String accessKeyId;
    /**
     * access key secret
     */
    private String accessKeySecret;

    public Authorization() {

    }

    public Authorization(String accessKeyId, String accessKeySecret) {
        this.accessKeyId = accessKeyId;
        this.accessKeySecret = accessKeySecret;
    }

    public String getAccessKeyId() {
        return accessKeyId;
    }

    public void setAccessKeyId(String accessKeyId) {
        this.accessKeyId = accessKeyId;
    }

    public String getAccessKeySecret() {
        return accessKeySecret;
    }

    public void setAccessKeySecret(String accessKeySecret) {
        this.accessKeySecret = accessKeySecret;
    }
}
