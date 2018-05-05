package com.doubeye.experiments.hadoop.c05;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.io.compress.CompressionCodec;
import org.apache.hadoop.io.compress.CompressionOutputStream;
import org.apache.hadoop.util.ReflectionUtils;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;


public class StreamCompressor {
    /**
     * hadoop jar d:\experiments.jar com.doubeye.experiments.hadoop.c05.StreamCompressor org.apache.hadoop.io.compress.GzipCodec < d:\1901.txt
     * @param args
     * @throws ClassNotFoundException
     * @throws IOException
     */
    public static void main(String[] args) throws ClassNotFoundException, IOException {
        String codecClassname = args[0];
        Class<?> codecClass = Class.forName(codecClassname);
        Configuration configuration = new Configuration();
        CompressionCodec codec = (CompressionCodec) ReflectionUtils.newInstance(codecClass, configuration);

        CompressionOutputStream out = codec.createOutputStream(System.out);

        IOUtils.copyBytes(System.in, out, 4096, false);
        out.finish();
    }
}
