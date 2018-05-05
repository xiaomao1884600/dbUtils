package com.doubeye.experiments.hadoop.c05;


import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.util.ReflectionUtils;

import java.io.IOException;
import java.net.URI;


public class SequenceFileReadDemo {
    /**
     * hadoop jar d:\experiments.jar com.doubeye.experiments.hadoop.c05.SequenceFileReadDemo /data/sequenceFileDemo.txt
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        String uri = args[0];
        Configuration configuration = new Configuration();
        FileSystem fs = FileSystem.get(URI.create(uri), configuration);
        Path path = new Path(uri);

        SequenceFile.Reader reader = null;
        try {
            //reader = new SequenceFile.Reader(fs, path, configuration);
            reader = new SequenceFile.Reader(configuration, SequenceFile.Reader.file(path));
            Writable key = (Writable) ReflectionUtils.newInstance(reader.getKeyClass(), configuration);
            Writable value = (Writable) ReflectionUtils.newInstance(reader.getValueClass(), configuration);
            long position = reader.getPosition();
            while (reader.next(key, value)) {
                String syncSeen = reader.syncSeen() ? "*" : "";
                System.out.printf("[%s%s]\t%s\t%s\n", position, syncSeen, key, value);
                position = reader.getPosition();
            }
        } finally {
            IOUtils.closeStream(reader);
        }
    }
}
