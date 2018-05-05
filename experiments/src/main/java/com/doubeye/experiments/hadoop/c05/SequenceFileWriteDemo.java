package com.doubeye.experiments.hadoop.c05;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.SequenceFile.Writer;
import org.apache.hadoop.io.Text;

import java.io.IOException;
import java.net.URI;


public class SequenceFileWriteDemo {
    private static final String[] DATA = {
            "One, two, buckle my shoe",
            "Three, fore, shut the door",
            "Five, six, pick up sticks",
            "Seven, eight, lay them straight",
            "Nine, ten, a big fat hen",
    };
    /**
     * hadoop jar d:\experiments.jar com.doubeye.experiments.hadoop.c05.SequenceFileWriteDemo /data/sequenceFileDemo.txt
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
        SequenceFile.Writer writer = null;

        try {
            //writer = SequenceFile.createWriter(fs, configuration, path, key.getClass(), value.getClass());
            writer = SequenceFile.createWriter(configuration, SequenceFile.Writer.file(path),
                    SequenceFile.Writer.keyClass(key.getClass()),
                    SequenceFile.Writer.valueClass(value.getClass()),
                    SequenceFile.Writer.compression(SequenceFile.CompressionType.NONE));
            for (int i = 0; i < 100; i++) {
                key.set(100 - i);
                value.set(DATA[i % DATA.length]);
                System.out.printf("[%s]\t%s\t%s\n", writer.getLength(), key, value);
                writer.append(key, value);
            }
        } finally {
            IOUtils.closeStream(writer);
        }
    }
}
