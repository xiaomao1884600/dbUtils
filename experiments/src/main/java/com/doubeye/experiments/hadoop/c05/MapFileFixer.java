package com.doubeye.experiments.hadoop.c05;


import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.MapFile;
import org.apache.hadoop.io.SequenceFile;

import java.io.IOException;
import java.net.URI;

public class MapFileFixer {
    /**
     * hadoop jar d:\hadoop\share\hadoop\mapreduce\hadoop-mapreduce-examples-*.jar sort -r 1 -inFormat org.apache.hadoop.mapreduce.lib.input.SequenceFileInputFormat -outFormat org.apache.hadoop.mapreduce.lib.output.SequenceFileOutputFormat -outKey org.apache.hadoop.io.IntWritable -outValue org.apache.hadoop.io.Text /data/sequenceFileDemo.txt /data/newnumbers.map
     * hadoop fs -mv /data/newnumbers.map/part-r-00000 /data/newnumbers.map/data
     * hadoop jar d:\experiments.jar com.doubeye.experiments.hadoop.c05.MapFileFixer /data/newnumbers.map
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws Exception {
        String mapUri = args[0];
        Configuration configuration = new Configuration();
        FileSystem fs = FileSystem.get(URI.create(mapUri), configuration);
        Path map = new Path(mapUri);
        Path mapData = new Path(map, MapFile.DATA_FILE_NAME);

        SequenceFile.Reader reader = new SequenceFile.Reader(configuration, SequenceFile.Reader.file(mapData));
        Class keyClass = reader.getKeyClass();
        Class valueClass = reader.getValueClass();
        reader.close();
        long entries = MapFile.fix(fs, map, keyClass, valueClass, false, configuration);
        System.out.printf("Created MapFile %s with %d entries\n", map, entries);
    }
}
