package com.doubeye.experiments.hadoop.c03;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;

import java.io.IOException;
import java.net.URI;

public class RegexExcludePathFilter implements PathFilter{
    private final String regex;
    public RegexExcludePathFilter(String regex) {
        this.regex = regex;
    }

    @Override
    public boolean accept(Path path) {
        return !path.toString().matches(regex);
    }
    /**
     * hadoop jar d:\experiments.jar com.doubeye.experiments.hadoop.c03.RegexExcludePathFilter hdfs://localhost:9000/result hdfs://localhost:9000/data
     */
    public static void main(String[] args) throws IOException {
        String uri = args[0];

        Configuration configuration = new Configuration();
        FileSystem fs = FileSystem.get(URI.create(uri), configuration);

        Path path = new Path(URI.create(uri));
        FileStatus[] statuses = fs.globStatus(path, new RegexExcludePathFilter("[0-9]"));
        Path[] listedPaths = FileUtil.stat2Paths(statuses);
        for (Path p : listedPaths) {
            System.out.println(p.toString());
        }
    }
}
