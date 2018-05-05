package com.doubeye.experiments.hadoop.c03;


import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FileUtil;
import org.apache.hadoop.fs.Path;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;

public class ListStatus {
    /**
     * hadoop jar d:\experiments.jar com.doubeye.experiments.hadoop.c03.ListStatus hdfs://localhost:9000/result hdfs://localhost:9000/data
     */
    public static void main(String[] args) throws IOException {
        String uri = args[0];

        Configuration configuration = new Configuration();
        FileSystem fs = FileSystem.get(URI.create(uri), configuration);

        Path[] paths = new Path[args.length];
        for (int i = 0; i < paths.length; i ++) {
            paths[i] = new Path(args[i]);
        }
        FileStatus[] statuses = fs.listStatus(paths);
        Path[] listedPaths = FileUtil.stat2Paths(statuses);
        for (Path p : listedPaths) {
            System.out.println(p.toString());
        }
    }
}
