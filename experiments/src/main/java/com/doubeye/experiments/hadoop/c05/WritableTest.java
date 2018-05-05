package com.doubeye.experiments.hadoop.c05;


import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.util.StringUtils;

import java.io.*;

public class WritableTest {
    public static void main(String[] args) throws IOException {
        // IntWritable writable = new IntWritable(163);
        IntWritable writable = new IntWritable();
        writable.set(163);

        byte[] bytes = serialize(writable);
        System.out.println(StringUtils.byteToHexString(bytes));
        IntWritable newWriteable = new IntWritable();
        deserialize(newWriteable, bytes);
        System.out.println(newWriteable.get());
    }


    public static byte[] serialize(Writable writable) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        DataOutputStream dataOut = new DataOutputStream(out);

        writable.write(dataOut);
        dataOut.close();
        return out.toByteArray();
    }

    public static byte[] deserialize(Writable writable, byte[] bytes) throws IOException {
        ByteArrayInputStream in = new ByteArrayInputStream(bytes);
        DataInputStream dataIn = new DataInputStream(in);
        writable.readFields(dataIn);
        dataIn.close();
        return bytes;
    }


}
