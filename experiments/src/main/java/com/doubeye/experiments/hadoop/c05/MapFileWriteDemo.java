package com.doubeye.experiments.hadoop.c05;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.*;

import java.io.IOException;
import java.net.URI;

public class MapFileWriteDemo {
    private static final String[] DATA = {
            "One, two, buckle my shoe",
            "Three, fore, shut the door",
            "Five, six, pick up sticks",
            "Seven, eight, lay them straight",
            "Nine, ten, a big fat hen",
    };
    /**
     * hadoop jar d:\experiments.jar com.doubeye.experiments.hadoop.c05.MapFileWriteDemo /data/number.map
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        String uri = args[0];
        Configuration configuration = new Configuration();
        FileSystem fs = FileSystem.get(URI.create(uri), configuration);
        Path path = new Path(uri);

        IntWritable key = new IntWritable();
        Text value = new Text();
        MapFile.Writer writer = null;

        try {
            //writer = new MapFile.Writer(configuration, fs, uri, key.getClass(), value.getClass());
            writer = new MapFile.Writer(configuration, path,
                    MapFile.Writer.keyClass(key.getClass()),
                    MapFile.Writer.valueClass(value.getClass()),
                    MapFile.Writer.compression(SequenceFile.CompressionType.NONE));
            for (int i = 0; i < 1024; i++) {
                key.set(i + 1);
                value.set(DATA[i % DATA.length]);
                writer.append(key, value);
            }
        } finally {
            IOUtils.closeStream(writer);
        }
    }
}
