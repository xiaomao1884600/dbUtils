package com.doubeye.experiments.hadoop.c03;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.util.Progressable;


import java.io.*;
import java.net.URI;

public class FileCopyWithProgress {

    /**
     * hadoop jar d:\experiments.jar com.doubeye.experiments.hadoop.c03.FileCopyWithProgress d:\1901.txt hdfs://localhost:9000/data/1901v2.txt
     */

    public static void main(String[] args) throws IOException {
        String localSrc = args[0];
        String dst = args[1];
        InputStream in = new BufferedInputStream(new FileInputStream(localSrc));

        Configuration configuration = new Configuration();
        FileSystem fileSystem = FileSystem.get(URI.create(dst), configuration);
        OutputStream out = fileSystem.create(new Path(dst), new Progressable() {
            @Override
            public void progress() {
                System.out.print('.');
            }
        });
        IOUtils.copyBytes(in, out, 4096, true);
    }
}
