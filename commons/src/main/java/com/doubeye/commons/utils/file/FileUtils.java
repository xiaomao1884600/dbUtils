package com.doubeye.commons.utils.file;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.io.IOUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

/**
 * @author doubeye
 * @version 1.0.0
 * 文件工具类
 */
public class FileUtils {
    /**
     * 将StringBuilder的内容写入文件
     * @param fileName 文件名
     * @param builder 文件内容
     * @throws IOException IO异常
     */
    public static void toFile(String fileName, StringBuilder builder) throws IOException {
        toFile(fileName, builder, false);
    }

    /**
     * 将StringBuilder的内容写入文件
     * @param fileName 文件名
     * @param builder 文件内容
     * @throws IOException IO异常
     */
    public static void toFile(String fileName, StringBuilder builder, boolean append) throws IOException {
        File file = new File(fileName);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, append))) {
            writer.write(builder.toString());
            writer.flush();
        }
    }


    /**
     * 将StringBuilder的内容写入文件
     * @param fileName 文件名
     * @param builder 文件内容
     * @param charSet 字符集
     * @throws IOException IO异常
     */
    public static void toFile(String fileName, StringBuilder builder, boolean append, String charSet) throws IOException {
        File file = new File(fileName);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, append))) {
            writer.write(builder.toString());
            writer.flush();
        }
    }

    /**
     * 将JSONArray写入到文件，注意，此方法将覆盖现有文件
     * @param fileName 文件名
     * @param array JSONArray 对象
     * @throws IOException IO异常
     */
    public static void toFile(String fileName, JSONArray array) throws IOException {
        toFile(fileName, array, false);
    }

    /**
     * 将JSONArray写入到文件
     * @param fileName 文件名
     * @param array JSONArray 对象
     * @param appendable 是否可追加写
     * @throws IOException IO异常
     */
    public static void toFile(String fileName, JSONArray array, boolean appendable) throws IOException {
        File file = new File(fileName);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, appendable))) {
            for (int i = 0; i < array.size(); i ++) {
                writer.write(array.getString(i) + "\n");
                if (i % 5000 == 0) {
                    writer.flush();
                }
            }
            writer.flush();
        }
    }

    /**
     * 将JSONObject写入到指定文件
     * @param fileName 文件名
     * @param content jsonObject对象
     * @throws IOException IO异常
     */
    public static void toFile(String fileName, JSONObject content) throws IOException {
        toFile(fileName, content, false);
    }

    public static void toFile(String fileName, JSONObject content, boolean appendable) throws IOException {
        File file = new File(fileName);
        /*
        String string = content.toString();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, appendable))) {
            int position = 0;
            while (position < string.length()) {
                System.out.println(position + ' ' + string.length());
                writer.write(string, position, Math.min(string.length(), position + BUFFER_SIZE));
                position += BUFFER_SIZE;
                writer.flush();
            }
        }
        */
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, appendable))) {
            writer.write(content.toString());
            writer.flush();
        }
    }

    public static void toFile(String fileName, byte[] content) throws IOException {
        int offset = 0;
        try (FileOutputStream out = new FileOutputStream(fileName)){
            while(offset <= content.length){
                System.out.println(content.length + "--" + offset);
                out.write(content, offset, Math.min((offset + BUFFER_SIZE), content.length));
                offset += BUFFER_SIZE;
            }
        }
    }

    /**
     * 将图像写入到文件
     * @param fileName 文件名
     * @param image BufferedImage 对象
     */
    public static void toFile(String fileName, BufferedImage image, String imageType) throws IOException {
        ImageIO.write(image, imageType, new File(fileName));
    }

    /**
     * 获得指定文件夹下的所有文件
     * @param rootDirector 根目录
     * @return 文件夹下所有文件列表
     */
    public static List<File> getAllFileInDirectory(String rootDirector) throws IOException {
        List<File> allFiles = new ArrayList<>();
        Files.walkFileTree(Paths.get(rootDirector), new SimpleFileVisitor<Path>(){
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs){
                allFiles.add(file.toFile());
                return FileVisitResult.CONTINUE;
            }
        });
        return allFiles;
    }


    /**
     * 获得指定文件夹下的所有文件
     * @param rootDirector 根目录
     * @param extendName 文件扩展名
     * @param limit 最多返回文件数量，当值小于等于0时，表示不限制
     * @return 文件夹下所有文件列表
     */
    public static List<File> getAllFileInDirectory(String rootDirector, String extendName, long limit) throws IOException {
        List<File> allFiles = new ArrayList<>();
        Files.walkFileTree(Paths.get(rootDirector), new SimpleFileVisitor<Path>(){
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs){
                if (attrs.isRegularFile() && file.toString().endsWith(extendName)) {
                    allFiles.add(file.toFile());
                }
                if ((limit > 0) && (allFiles.size() == limit)) {
                    return FileVisitResult.TERMINATE;
                } else {
                    return FileVisitResult.CONTINUE;
                }
            }
        });
        return allFiles;
    }

    /**
     * 将文件转为字符串
     * @param fileName 文件名
     * @param charset 字符集
     * @return 文件内容
     * @throws IOException IO异常
     */
    public static String fromFile(String fileName, String charset) throws IOException {
        try(InputStream inputStream = new FileInputStream(new File(fileName))) {
            return IOUtils.toString(inputStream, charset);
        }
    }



    private static final int BUFFER_SIZE = 4096;

    public static void main(String[] args) throws IOException {
        List<File> files = getAllFileInDirectory("D:/java", ".java", 5);
        files.forEach(file->{
            System.out.println(file.getName());
        });
    }
}
