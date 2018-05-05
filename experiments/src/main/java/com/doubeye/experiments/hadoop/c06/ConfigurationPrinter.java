package com.doubeye.experiments.hadoop.c06;


import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import java.io.IOException;
import java.util.Map;

public class ConfigurationPrinter extends Configured implements Tool{
    static {
        Configuration.addDefaultResource("hdfs-default.xml");
        Configuration.addDefaultResource("hdfs-site.xml");
        Configuration.addDefaultResource("yarn-default.xml");
        Configuration.addDefaultResource("yarn-site.xml");
        Configuration.addDefaultResource("mapred-default.xml");
        Configuration.addDefaultResource("mapred-site.xml");
    }
    @Override
    public int run(String[] strings) throws Exception {
        Configuration configuration = getConf();
        for (Map.Entry<String, String> entry : configuration) {
            System.out.printf("%s-%s\n", entry.getKey(), entry.getValue());
        }
        return 0;
    }

    /**
     * hadoop jar d:\experiments.jar com.doubeye.experiments.hadoop.c06.ConfigurationPrinter
     * @param args
     */
    public static void main(String[] args) throws Exception {
        int exitCode = ToolRunner.run(new ConfigurationPrinter(), args);
        System.exit(exitCode);
    }
}
