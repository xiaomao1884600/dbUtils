package com.doubeye.commons.utils.file;


import java.io.*;
import java.util.List;
import java.util.zip.GZIPOutputStream;

/**
 * @author doubeye
 * @version 1.0.0
 * 压缩工具类
 */
@SuppressWarnings("unused")
public class ZipUtils {
    /**
     * 缓冲区大小
     */
    private static final int BUFFER = 1024;

    /**
     * zip 实现
     * @param sourceFileNames 源文件名
     * @param zippedFileName 压缩文件名
     * @throws IOException IO异常
     */
    @SuppressWarnings("WeakerAccess")
    public static void gzip(List<String> sourceFileNames, String zippedFileName) throws IOException {

        try (GZIPOutputStream out = new GZIPOutputStream(new FileOutputStream(zippedFileName))) {
            byte[] buffer = new byte[BUFFER];
            int readLen;
            for (String fileName : sourceFileNames) {
                File file = new File(fileName);
                try (InputStream in = new BufferedInputStream(new FileInputStream(file))) {
                    while ((readLen = in.read(buffer, 0, BUFFER)) != -1) {
                        out.write(buffer, 0, readLen);
                    }
                }
            }
            out.finish();
            out.flush();
        }
    }
    /* TODO 放到单元测试
    public static void main(String[] args) throws IOException {
        String[] fileNames = new String[]{
                "d:\\1901.txt",
                "d:\\1902.txt",
        };
        List<String> list = new ArrayList<>();
        for (String fileName : fileNames) {
            list.add(fileName);
        }
        String targetFileName = "d:\\data.gz";
        ZipUtils.gzip(list, targetFileName);
    }
    */
}
