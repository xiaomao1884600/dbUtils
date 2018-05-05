package com.doubeye.commons.utils.cloud.service.provider.tencent.restful;

import com.doubeye.commons.utils.DateTimeUtils.DateTimeUtils;

import java.util.Random;

/**
 * @author doubeye
 * 腾讯云提交参数中的时间戳，过期时间以及随机数的对象
 */
public class TimeParameters {
    private long timeStamp;
    private long expired;
    private int nonce;

    public TimeParameters () {
        timeStamp = System.currentTimeMillis() / 1000;
        expired = timeStamp + DateTimeUtils.SECOND_PER_DAY;
        nonce = new Random().nextInt(100000);
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public long getExpired() {
        return expired;
    }

    public int getNonce() {
        return nonce;
    }

    public TimeParameters(long timeStamp, long expired, int nonce) {
        this.timeStamp = timeStamp;
        this.expired = expired;
        this.nonce = nonce;
    }
}
