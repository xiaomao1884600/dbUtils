package com.alibaba.oss;

import com.aliyun.oss.OSSClient;

import com.aliyun.oss.model.Callback;
import com.aliyun.oss.model.PutObjectResult;
import com.doubeye.commons.utils.cloud.service.provider.aliyun.oss.OssHelper;
import com.doubeye.commons.utils.file.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.LogManager;


import java.io.File;
import java.util.List;
import java.util.Map;


public class BunchUploadTest {
    private static Logger logger = LogManager.getLogger(BunchUploadTest.class);


    public static void main(String[] args) throws Throwable {
        logger.setLevel(Level.DEBUG);
        List<File> files = FileUtils.getAllFileInDirectory("d:/record");
        String bucket = "doubeye-test-conversation-analyze";
        long allCost = 0;
        OSSClient client = OssHelper.getOSSClient("oss-cn-beijing.aliyuncs.com", "LTAIknRDVvtaSLBM", "6X9cZbHkjZtxFtPzwylIRajIMSdd6h");
        Map<String, Object> config = OssHelper.getDefaultUploadConfig();
        for (File file : files) {
            long t1 = System.currentTimeMillis();
            String key = StringUtils.substringAfter(file.getAbsolutePath(), ":\\");
            //OssHelper.putFile(client, bucket, key, file);
            //OssHelper.uploadFile(client, bucket, key, file, null, config);
            PutObjectResult result = OssHelper.putFile(client, bucket, key, file, getCallback());
            byte[] buffer = new byte[1024];
            result.getCallbackResponseBody().read(buffer);
            System.out.println(new String(buffer));
            result.getCallbackResponseBody().close();
            long t2 = System.currentTimeMillis();
            long cost = t2 - t1;
            logger.debug("上传文件 " + file.getAbsolutePath() + "花费" + cost + "ms");
            allCost += cost;
        }
        logger.debug("上传所有文件花费" + allCost + "ms");
    }

    private static Callback getCallback() {

        Callback callback = new Callback();
        callback.setCallbackUrl(URL_CALLBACK);
        callback.setCallbackHost("oss-cn-beijing.aliyuncs.com");
        /*
        JSONObject body = new JSONObject();
        body.put("mimeType", "text");
        body.put("size", 100);
        */
        callback.setCallbackBody("{\\\"mimeType\\\":${mimeType},\\\"size\\\":${size}");
        System.out.println(Callback.CalbackBodyType.JSON.toString());
        callback.setCalbackBodyType(Callback.CalbackBodyType.JSON);
        callback.addCallbackVar("x:var1", "value1");
        callback.addCallbackVar("x:var2", "value2");
        return callback;
    }

    private static final String URL_CALLBACK = "http://recordanalyzetest.hxsd.com/false";

}
