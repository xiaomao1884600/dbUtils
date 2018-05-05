package com.doubeye.commons.utils.token;

import jdk.nashorn.internal.parser.Token;
import junit.framework.TestCase;
import org.junit.Assert;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class TokenUtilsTest extends TestCase{
    public void testGenerateToken() throws InvalidKeyException, NoSuchAlgorithmException {
        String userId = "zhanglu1782@hxsd.local";
        Calendar calendar = new GregorianCalendar();
        calendar.set(2017, Calendar.AUGUST, 10, 17, 18, 57);
        Date now = calendar.getTime();
        String secretKey = "com.doubeye";
        String plainText = TokenUtils.confusion(userId, secretKey) + now.toString();
        System.out.println(plainText);
        System.out.println(TokenUtils.generateToken(userId, now, secretKey));
        Assert.assertEquals("加密后的数据应该相等：", "9c538f713ae5c88e6f64a0e4dd5dc58dd30843e9", TokenUtils.generateToken(userId, now, secretKey).toLowerCase());
    }
}
