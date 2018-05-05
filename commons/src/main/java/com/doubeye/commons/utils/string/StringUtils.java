package com.doubeye.commons.utils.string;

import com.doubeye.commons.utils.constant.CommonConstant;
import net.sf.json.JSONObject;
import org.elasticsearch.common.util.set.Sets;
import sun.misc.BASE64Encoder;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author doubeye
 * String 工具类
 */
@SuppressWarnings("unused | WeakerAccess")
public class StringUtils {
    /**
     * 返回源字符串(source)中指定字符串(delimiter)以前的所有字符
     *
     * @param source
     *            源字符串
     * @param delimiter
     *            指定字符串
     * @return 源字符串(source)中指定字符串(delimiter)以前的所有字符，如果源字符串为空字符串，则返回空字符串
     *         如果源字符串中不包含指定字符串，则返回源字符串
     */
    public static String getStringBefore(String source, String delimiter) {
        if (source.equals("")) {
            return "";
        } else if (source.contains(delimiter)) {
            return source.substring(0, source.indexOf(delimiter));
        } else {
            return source;
        }
    }

    /**
     * 返回源字符串(source)中指定字符串(delimiter)以后的所有字符，如果delimiter出现过多次，则返回最后一次出现位置之后的字符串
     *
     * @param source
     *            源字符串
     * @param delimiter
     *            指定字符串
     * @return 源字符串(source)中指定字符串(delimiter)以后的所有字符，如果源字符串为空字符串，则返回空字符串
     *         如果源字符串中不包含指定字符串，则返回源字符串
     */
    public static String getStringAfterLast(String source, String delimiter) {
        if (source.equals("")) {
            return "";
        } else if (source.lastIndexOf(delimiter) > -1) {
            return source.substring(source.lastIndexOf(delimiter)
                    + delimiter.length());
        } else {
            return source;
        }
    }

    /**
     * 返回源字符串(source)中指定字符串(delimiter)以前的所有字符，如果delimiter出现过多次，则返回最后一次出现位置之前的字符串
     *
     * @param source
     *            源字符串
     * @param delimiter
     *            指定字符串
     * @return 源字符串(source)中指定字符串(delimiter)以前的所有字符，如果源字符串为空字符串，则返回空字符串
     *         如果源字符串中不包含指定字符串，则返回源字符串
     */
    public static String getStringBeforeLast(String source, String delimiter) {
        if (source.equals("")) {
            return "";
        } else if (source.lastIndexOf(delimiter) > -1) {
            return source.substring(0, source.lastIndexOf(delimiter));
        } else {
            return source;
        }
    }

    private static int compare(String str, String target)
    {
        int d[][];              // 矩阵
        int n = str.length();
        int m = target.length();
        int i;                  // 遍历str的
        int j;                  // 遍历target的
        char ch1;               // str的
        char ch2;               // target的
        int temp;               // 记录相同字符,在某个矩阵位置值的增量,不是0就是1
        if (n == 0) { return m; }
        if (m == 0) { return n; }
        d = new int[n + 1][m + 1];
        for (i = 0; i <= n; i++)
        {                       // 初始化第一列
            d[i][0] = i;
        }

        for (j = 0; j <= m; j++)
        {                       // 初始化第一行
            d[0][j] = j;
        }

        for (i = 1; i <= n; i++)
        {                       // 遍历str
            ch1 = str.charAt(i - 1);
            // 去匹配target
            for (j = 1; j <= m; j++)
            {
                ch2 = target.charAt(j - 1);
                if (ch1 == ch2 || ch1 == ch2+32 || ch1+32 == ch2)
                {
                    temp = 0;
                } else
                {
                    temp = 1;
                }
                // 左边+1,上边+1, 左上角+temp取最小
                d[i][j] = min(d[i - 1][j] + 1, d[i][j - 1] + 1, d[i - 1][j - 1] + temp);
            }
        }
        return d[n][m];
    }

    public static double cosineSimilarity(Map<String, Double> v1, Map<String, Double> v2) {
        Set<String> both = Sets.newHashSet(v1.keySet());
        both.retainAll(v2.keySet());
        double sclar = 0, norm1 = 0, norm2 = 0;
        for (String k : both) {
            sclar += v1.get(k) * v2.get(k);
        }
        for (String k : v1.keySet()) {
            norm1 += v1.get(k) * v1.get(k);
        }
        for (String k : v2.keySet()) {
            norm2 += v2.get(k) * v2.get(k);
        }
        return Math.sqrt(norm1 * norm2) == 0 ? 0 : sclar / Math.sqrt(norm1 * norm2);
    }

    public static long getWordCount(Map<String, Double> frequencies) {
        System.out.println(frequencies);
        long wordCount = 0;
        for (Map.Entry<String, Double> entry : frequencies.entrySet()) {
            wordCount += entry.getKey().length() * entry.getValue();
        }
        return wordCount;
    }

    private static int min(int one, int two, int three)
    {
        return (one = one < two ? one : two) < three ? one : three;
    }

    /**
     * 获取两字符串的相似度
     */
    public static float getSimilarityRatio(String str, String target)
    {
        return 1 - (float) compare(str, target) / Math.max(str.length(), target.length());
    }

    public static String format(String namedTemplate, JSONObject parameters) {
        String result = namedTemplate;
        List<String> namedParameters = getNamedParameters(namedTemplate);
        for (String parameterName : namedParameters) {
            //String parameterName = holder.replace(PARAMETER_START, "").replace(PARAMETER_END, "");
            String holder = PARAMETER_START + parameterName + PARAMETER_END;
            if (parameters.containsKey(parameterName)) {
                result = result.replace(holder, parameters.getString(parameterName));
            } else {
                result = result.replace(holder, "");
            }
        }
        return result;
    }

    private static List<String> getNamedParameters (String namedTemplate) {
        List<String> result = new ArrayList<>();
        String temp = namedTemplate;
        while (temp.contains(PARAMETER_START) && temp.contains(PARAMETER_END)) {
            result.add(org.apache.commons.lang.StringUtils.substringBetween(temp, PARAMETER_START, PARAMETER_END));
            temp = org.apache.commons.lang.StringUtils.substringAfter(temp, PARAMETER_END);
        }
        return result;
    }


    /**
     * 用指定的key对字符串进行加密，并将加密的结果进行BASE64编码，相对于PHP的 Base64Encode(HmacSha1(plainText, SecretKey))
     * @param plainText 要加密的字符串
     * @param key key
     * @return 加密结果
     */
    public static String toHmacSha1(String plainText, String key) throws UnsupportedEncodingException {

        SecretKeySpec signingKey = new SecretKeySpec(key.getBytes(CommonConstant.CHARSETS.UTF_8.toString()), CommonConstant.CIPHERS.Hmac_SHA1.toString());
        try {
            Mac mac = Mac.getInstance(CommonConstant.CIPHERS.Hmac_SHA1.toString());
            mac.init(signingKey);
            byte[] rwoHmac = mac.doFinal(plainText.getBytes(CommonConstant.CHARSETS.UTF_8.toString()));
            return new BASE64Encoder().encode(rwoHmac);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }

    private static final String PARAMETER_START = "([{";
    private static final String PARAMETER_END = "}])";

    public static void main(String[] args) {
        System.out.println(getSimilarityRatio("198广告传媒", "上海优翔广告传媒"));
    }
}
