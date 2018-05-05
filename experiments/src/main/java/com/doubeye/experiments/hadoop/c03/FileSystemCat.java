package com.doubeye.experiments.hadoop.c03;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

/**
 * hadoop jar d:\experiments.jar com.doubeye.experiments.hadoop.c03.FileSystemCat hdfs://localhost:9000/result/part-r-00000
 */
public class FileSystemCat {
    public static void main(String[] args) throws IOException {
        String uri = args[0];
        Configuration configuration = new Configuration();
        FileSystem fileSystem = FileSystem.get(URI.create(uri), configuration);
        InputStream inputStream = null;
        try {
            inputStream = fileSystem.open(new Path(uri));
            IOUtils.copyBytes(inputStream, System.out, 4096, false);
        } finally {
            IOUtils.closeStream(inputStream);
        }
    }
}
