package com.doubeye.commons.utils.token;



import com.doubeye.commons.utils.collection.CollectionUtils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;


@SuppressWarnings("unused | WeakerAccess")
public class TokenUtils {
    private static final String HMAC_SHA1 = "HmacSHA1";

    public static String generateToken(String userId, String expiredTime, String secretKey) throws NoSuchAlgorithmException, InvalidKeyException {
        String plainText = confusion(userId, secretKey) + expiredTime;
        SecretKeySpec signingKey = new SecretKeySpec(secretKey.getBytes(), HMAC_SHA1);
        Mac mac = Mac.getInstance(HMAC_SHA1);
        mac.init(signingKey);
        byte[] rawHmac = mac.doFinal(plainText.getBytes());
        return CollectionUtils.toHex(rawHmac);
    }

    public static String generateToken(String userId, Date expiredTime, String secretKey) throws NoSuchAlgorithmException, InvalidKeyException {
        return generateToken(userId, expiredTime.toString(), secretKey);
    }

    public static String confusion(String firstSource, String secondSource) {
        String longerString, shorterString;
        if (firstSource.length() >= secondSource.length()) {
            longerString = firstSource;
            shorterString = secondSource;
        } else {
            longerString = secondSource;
            shorterString = secondSource;
        }

        StringBuilder builder = new StringBuilder();
        int shortStringLength = shorterString.length();
        for (int i = 0; i < longerString.length() - 1; i ++) {
            builder.append(longerString.charAt(i)).append(shorterString.charAt(i % shortStringLength));
        }
        return builder.toString();
    }
}
