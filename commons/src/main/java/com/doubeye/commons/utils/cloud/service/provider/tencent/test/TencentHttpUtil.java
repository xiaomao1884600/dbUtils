package com.doubeye.commons.utils.cloud.service.provider.tencent.test;


import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class TencentHttpUtil {

    public static Map<String,String> setUrlJson(
            String appid,
            String secretid,//控制台上对应appid的secretid
            String projectid,
            String template_name,
            String sub_service_type,//0-全文转写 1 实时流式识别�?
            String engine_model_type,//引擎类型引擎模型类型�?0-8k婚恋�? 1-16k通用�?2-16k通用短语音， 3-8k话�?�分离�?�目前实时引擎编号只支持1�?
            String res_type,//__ 0：同步返回；1：尾包返�?
            String result_text_format,//识别结果文本编码方式�?0：UTF-8�?1：GB2312�?2：GBK�?3：BIG5
            String voice_id,
            String timeout,
            String source//0 web  1 sdk
    /*        String timestamp,//当前时间戳，是一个符合UNIXEpoch时间戳规范的数�?�，单位为秒
            String expired,//签名的有效期，是�?个符合UNIXEpoch时间戳规范的数�?�，单位为秒；expired必须大于timestamp且expired-timestamp小于90�?
            String nonce//随机正整数�?�用户需自行生成，最�?10�?*/

    ){
        Map<String,String> urlJson = new TreeMap<>();
        urlJson.put("appid" , appid );
        urlJson.put("secretid" , secretid );
        urlJson.put("projectid" , projectid );
        //urlJson.put("template_name" , template_name );
        urlJson.put("sub_service_type" , sub_service_type );
        urlJson.put("engine_model_type" , engine_model_type );
        urlJson.put("res_type" , res_type );
        //urlJson.put("result_text_format" , result_text_format );
        urlJson.put("res_text_format" , result_text_format );
        //urlJson.put("voice_id" , voice_id );
        //urlJson.put("timeout" , timeout );
        //urlJson.put("source" , source );
        urlJson.put("source_type" , source );
        urlJson.put("url", "http://asr-bucket-1255612177.cossh.myqcloud.com/20171008090459-Outbound-E1Trunk1-3215-018088630272.wav");
        urlJson.put("callback_url", "http://rds.hxsd.com/api/call_back");
        /*
        urlJson.put("timestamp" , toUNIXEpoch() );
        urlJson.put("expired" , toExpiredUNIXEpoch() );
        urlJson.put("nonce" , getNonce());
        */
        urlJson.put("timestamp" , "1514450738");
        urlJson.put("expired" , "1514537138");
        urlJson.put("nonce" , "34293");
        //POSTaai.qcloud.com/asr/v1/1255612177?callback_url=http://rds.hxsd.com/api/call_back&channel_num=1&engine_model_type=8k_0&expired=1514537138&nonce=34293&projectid=1255612177&res_text_format=0&res_type=1&secretid=AKIDeC0fDeGzxkOvJZNY7ZqvoVBj9ORNDqPp&source_type=0&sub_service_type=0&timestamp=1514450738&url=http://asr-bucket-1255612177.cossh.myqcloud.com/20171008090459-Outbound-E1Trunk1-3215-018088630272.wav?sign=zzslJuQ29WeSlgH2+XZieNsMlOBhPTEyNTU2MTIxNzcmaz1BS0lEOEZCMzFLQWQ2T1VRS0ttdlNzQUdFREI0b09telJ2Zk0mZT0xNTE3MDM1OTY4JnQ9MTUxNDQ0Mzk2OCZyPTQ4MDEyMTQwMyZmPS8yMDE3MTAwODA5MDQ1OS1PdXRib3VuZC1FMVRydW5rMS0zMjE1LTAxODA4ODYzMDI3Mi53YXYmYj1hc3ItYnVja2V01255612177?callback_url=http://rds.hxsd.com/api/call_back&channel_num=1&engine_model_type=8k_0&expired=1514537138&nonce=34293&projectid=1255612177&res_text_format=0&res_type=1&secretid=AKIDeC0fDeGzxkOvJZNY7ZqvoVBj9ORNDqPp&source_type=0&sub_service_type=0&timestamp=1514450738&url=http://asr-bucket-1255612177.cossh.myqcloud.com/20171008090459-Outbound-E1Trunk1-3215-018088630272.wav?sign=zzslJuQ29WeSlgH2+XZieNsMlOBhPTEyNTU2MTIxNzcmaz1BS0lEOEZCMzFLQWQ2T1VRS0ttdlNzQUdFREI0b09telJ2Zk0mZT0xNTE3MDM1OTY4JnQ9MTUxNDQ0Mzk2OCZyPTQ4MDEyMTQwMyZmPS8yMDE3MTAwODA5MDQ1OS1PdXRib3VuZC1FMVRydW5rMS0zMjE1LTAxODA4ODYzMDI3Mi53YXYmYj1hc3ItYnVja2V0
/*        urlJson.put("timestamp" , "1508403985" );
        urlJson.put("expired" , "1508490385");
        urlJson.put("nonce" , "2942")*/;

        return urlJson;
    }


    public  static String generateUrl(String serverUrl, Map<String,String> urlJson){
//        serverUrl += urlJson.get("appid") + "?";
//        serverUrl+="projectid="+urlJson.get("projectid")+"&";
//        serverUrl+="template_name="+urlJson.get("template_name")+"&";
//        serverUrl+="sub_service_type="+urlJson.get("sub_service_type")+"&";
//        serverUrl+="engine_model_type="+urlJson.get("engine_model_type")+"&";
//        serverUrl+="res_type="+urlJson.get("res_type")+"&";
//        serverUrl+="result_text_format="+urlJson.get("result_text_format")+"&";
//        serverUrl+="voice_id="+urlJson.get("voice_id")+"&";
//        serverUrl+="seq="+urlJson.get("seq")+"&";
//        serverUrl+="end="+urlJson.get("end")+"&";
//        serverUrl+="timeout="+urlJson.get("timeout")+"&";
//        serverUrl+="source="+urlJson.get("source")+"&";
//        serverUrl+="secretid="+urlJson.get("secretid")+"&";
//        serverUrl+="timestamp="+urlJson.get("timestamp")+"&";
//        serverUrl+="expired="+urlJson.get("expired")+"&";
//        serverUrl+="nonce="+urlJson.get("nonce");
    	
    	StringBuilder strBuilder = new StringBuilder(serverUrl);
    	
    	if (urlJson.containsKey("appid")) {
    		strBuilder.append(urlJson.get("appid"));
    	}
    	
    	strBuilder.append('?');
    	
    	// to make that all the parameters are sorted by ASC order
    	TreeMap<String, String> sortedMap = new TreeMap<>(urlJson);

    	for (Map.Entry<String, String> entry : sortedMap.entrySet()) {
    		if (entry.getKey().equals("appid")) {
    			continue;
    		}
    		strBuilder.append(entry.getKey());
    		strBuilder.append('=');
    		strBuilder.append(entry.getValue());
    		strBuilder.append('&');
    	}
    	
    	if (urlJson.size() > 0) {
    		strBuilder.setLength(strBuilder.length() - 1);
    	}
    	
    	System.out.println("Generated URL: " + strBuilder);

        return strBuilder.toString();
    }

    public static String base64_hmac_sha1(String value, String keyStr) {
		String encoded = "";
		String type = "HmacSHA1";	
		try {
			byte[] key = (keyStr).getBytes("UTF-8");
			byte[] Sequence = (value).getBytes("UTF-8");
			
			Mac HMAC = Mac.getInstance(type);
			SecretKeySpec secretKey = new SecretKeySpec(key, type);

			HMAC.init(secretKey);
			byte[] Hash = HMAC.doFinal(Sequence);

			encoded = Base64.getEncoder().encodeToString(Hash);
		} catch (Exception e) {
			e.printStackTrace();
		}
	    return encoded;
	}
    
    /*
     * UNIXEpoch 时间�?
     */
    public static String toUNIXEpoch() {
        long unixTime = System.currentTimeMillis() / 1000L;
        return unixTime + "";
    }

    /*
    * ExpiredUNIXEpoch 时间�?
    */
    public static String toExpiredUNIXEpoch() {
        long unixTime = System.currentTimeMillis() / 1000L + 24 * 60 * 60 ;
        return unixTime + "";
    }

    public static String getNonce(){
        Random random = new Random(System.currentTimeMillis());
        return random.nextInt(10000) + "";
    }

    public static String createSign(String serverUrl, String secretKey) throws UnsupportedEncodingException ,NoSuchAlgorithmException, InvalidKeyException {
    	String strToBeEncoded = "POST" + serverUrl.substring(7);
    	System.out.println("String to be encoded: " + strToBeEncoded);
    	return base64_hmac_sha1(strToBeEncoded, secretKey);
    }

    /**
     * 生成签名
     *
     */
    public static String encrypt(String oldStr,String secretKey ) throws NoSuchAlgorithmException, InvalidKeyException {
        String sign = "";
        System.out.println(oldStr);
        //System.out.println("POSTaai.qcloud.com/asr/v1/2000001?callback_url=http://test.qq.com/rec_callback&engine_model_type=1&expired=1473752807&nonce=44925&projectid=0&res_text_format=0&res_type=1&secretid=AKIDUfLUEUigQiXqm7CVSspKJnuaiIKtxqAv&source_type=0&sub_service_type=0&timestamp=1473752207&url=http://test.qq.com/voice_url");
        Mac mac = Mac.getInstance("HmacSHA1");
        SecretKeySpec keySpec = new SecretKeySpec(secretKey.getBytes(), "HmacSHA1");
        mac.init(keySpec);
        mac.update(oldStr.getBytes());
        sign = Base64.getEncoder().encodeToString(mac.doFinal());
        System.out.println("生成签名串：" + sign);
        return sign;
    }


}