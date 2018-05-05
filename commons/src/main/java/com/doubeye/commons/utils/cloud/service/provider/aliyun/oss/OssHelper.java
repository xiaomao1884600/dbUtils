package com.doubeye.commons.utils.cloud.service.provider.aliyun.oss;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.*;
import com.doubeye.commons.application.CreationInformation;
import com.doubeye.commons.application.SelfManagedClosableDaemon;
import net.sf.json.JSONObject;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author doubeye
 * @version 1.0.0
 * 用来处理阿里云客户端的助手类
 */
@SuppressWarnings("unused | WeakerAccess")
public class OssHelper {

    public static OSSClient getOSSClient(String endPoint, String accessKeyId, String accessKeySecret) throws NoSuchMethodException {
        OSSClient client = new OSSClient(endPoint, accessKeyId, accessKeySecret);
        CreationInformation information = new CreationInformation();
        information.setClosable(client);
        information.setCloseHelperClass(OssHelper.class);
        information.setCloseHelperMethod(OssHelper.class.getDeclaredMethod("shutDownOSSClient", OSSClient.class));
        SelfManagedClosableDaemon.addClosable(client, information);
        return client;
    }

    public static void shutDownOSSClient(OSSClient client) {
        SelfManagedClosableDaemon.removeClosable(client);
        client.shutdown();
    }

    public static void putString(OSSClient client, String bucket, String key, String content) {
        client.putObject(bucket, key, new ByteArrayInputStream(content.getBytes()));
    }

    /**
     * 上传文件
     * @param client OSS客户端
     * @param bucket 桶名
     * @param key 文件名
     * @param file 上传的文件对象
     * @throws FileNotFoundException 文件未找到异常
     */
    public static PutObjectResult putFile(OSSClient client, String bucket, String key, File file) throws FileNotFoundException {
        InputStream in = new FileInputStream(file);
        return client.putObject(bucket, key, in);
    }

    public static PutObjectResult putUri(OSSClient client, String bucket, String key, String uri) throws IOException {
        InputStream inputStream = new URL(uri).openStream();
        //InputStream inputStream = new URL("https://www.aliyun.com/").openStream();
        return client.putObject(bucket, key, inputStream);
    }

    public static PutObjectResult putFile(OSSClient client, String bucket, String key, File file, Callback callback) throws FileNotFoundException {
        InputStream in = new FileInputStream(file);
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucket, key,
                new FileInputStream(file));
        putObjectRequest.setCallback(callback);
        return client.putObject(putObjectRequest);
    }


    public static void uploadFile(OSSClient client, String bucket, String key, File file, JSONObject meta, Map<String, Object> config) throws Throwable {
        UploadFileRequest uploadFileRequest = new UploadFileRequest(bucket, key);
        uploadFileRequest.setUploadFile(file.getAbsolutePath());
        if (meta != null) {
            uploadFileRequest.setObjectMetadata(fromJSONObject(meta));
        }
        initUploadConfig(uploadFileRequest, config);
        client.uploadFile(uploadFileRequest);

    }
    private static final String DEFAULT_TASK_NUM = "1";
    private static final String DEFAULT_PART_SIZE = 100 * 1024 + "";
    /**
     * 获得断点续传的默认配置
     * @return 断点续传的默认配置
     * 包括参数如下：
     * taskNum 上传线程，默认为1
     * partSize 分片大小单位为K， 默认为100K
     */
    public static Map<String, Object> getDefaultUploadConfig() {
        Map<String, Object> config = new HashMap<>();
        config.put("taskNum", DEFAULT_TASK_NUM);
        config.put("partSize", DEFAULT_PART_SIZE);
        return config;
    }

    private static void initUploadConfig(UploadFileRequest uploadFileRequest, Map<String, Object> config) {
        if (config == null) {
            config = getDefaultUploadConfig();
        }
        uploadFileRequest.setTaskNum(Integer.parseInt(config.getOrDefault("taskNum", DEFAULT_TASK_NUM).toString()));
        uploadFileRequest.setPartSize(Integer.parseInt(config.getOrDefault("partSize", DEFAULT_PART_SIZE).toString()));
        if (config.containsKey("callback") && config.get("callback") instanceof Callback) {
            uploadFileRequest.setCallback((Callback) config.get("callback"));
        }
        uploadFileRequest.setEnableCheckpoint(true);
    }



    public static InputStream getInputStream(OSSClient client, String bucket, String key) {
        OSSObject object = client.getObject(bucket, key);
        return object.getObjectContent();
    }

    public static String getString(OSSClient client, String bucket, String key) throws IOException {
        StringBuilder builder = new StringBuilder();
        try (InputStream in = getInputStream(client, bucket, key)) {
            if (in != null) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line).append("\n");
                }
            }
        }
        return builder.toString();
    }

    public static File getFile(OSSClient client, String bucket, String key, String fileName) {
        File file = new File(fileName);
        client.getObject(new GetObjectRequest(bucket, key), file);
        return file;
    }

    public static void deleteObject(OSSClient client, String bucket, String key) {
        client.deleteObject(bucket, key);
    }

    public static void putObject(OSSClient client, String bucket, String key, String content, JSONObject meta) {
        if (meta.toString().getBytes().length > MAX_META_SIZE) {
            throw new RuntimeException(MESSAGE_META_EXCEEDED_MAX_SIZE + meta.toString());
        }
        ObjectMetadata objectMetadata = fromJSONObject(meta);
        client.putObject(bucket, key, new ByteArrayInputStream(content.getBytes()), objectMetadata);
    }

    public static JSONObject getObjectMetaData(OSSClient client, String bucket, String key) {
        ObjectMetadata metadata = client.getObjectMetadata(bucket, key);
        return fromObjectMeta(metadata);
    }

    private static JSONObject fromObjectMeta(ObjectMetadata metadata) {
        return JSONObject.fromObject(metadata.getUserMetadata());
    }

    public static void main(String[] args) throws Throwable {
        String bucket = "doubeye-test-conversation-analyze";
        String key = "chineseCharsDemo.txt";
        JSONObject meta = new JSONObject();
        meta.put("p1", "value1");
        meta.put("p2", "中文元数据测试");
        OSSClient client = OssHelper.getOSSClient("oss-cn-beijing.aliyuncs.com", "LTAIknRDVvtaSLBM", "6X9cZbHkjZtxFtPzwylIRajIMSdd6h");
        putObject(client, bucket, key, "中文内容的测试", meta);
        System.out.println(getString(client, bucket, key));
        JSONObject metaFromServer = getObjectMetaData(client, bucket, key);
        System.out.println(metaFromServer);
        //deleteObject(client, bucket, key);


        //文件测试
        String fileKey = "record.wav";
        /*
        putFile(client, bucket, "record.wav", new File("d:/up.wav"));
        getFile(client, bucket, "record.wav", "d:/down.wav");
        deleteObject(client, bucket, fileKey);
        */
        //测试断点续传
        //uploadFile(client, bucket, fileKey, new File("d:/up.wav"), null, null);
        //getFile(client, bucket, fileKey, "d:/down.wav");
        //deleteObject(client, bucket, fileKey);

        //uri测试
        String uri = "http://121.31.255.235:12301/record/RLYTX_0005_635ad70e0a04ec14_13141367508_13488829098_20180418172121.mp3";
        PutObjectResult result = putUri(client, bucket, "rltest.mp3", uri);
        System.out.println(result.getClientCRC().equals(result.getServerCRC()));

        shutDownOSSClient(client);
    }

    private static ObjectMetadata fromJSONObject(JSONObject source) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        Set<?> keys = source.keySet();
        for (Object key : keys) {
            objectMetadata.addUserMetadata(key.toString(), source.getString(key.toString()));
        }
        return objectMetadata;
    }

    private static final int MAX_META_SIZE = 8192;
    private static final String MESSAGE_META_EXCEEDED_MAX_SIZE = "元数据信息超过阿里云的允许范围8K，实际内容为：";

}
