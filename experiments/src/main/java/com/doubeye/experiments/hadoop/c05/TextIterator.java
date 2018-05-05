package com.doubeye.experiments.hadoop.c05;

import org.apache.hadoop.io.Text;

import java.nio.ByteBuffer;

public class TextIterator {

    public static void main(String[] args) {
        Text t = new Text("\u0041\u00DF\u6771\uD801\uDC00");
        ByteBuffer buffer = ByteBuffer.wrap(t.copyBytes(), 0, t.getLength());
        int cp;
        while (buffer.hasRemaining() && (cp = Text.bytesToCodePoint(buffer)) != -1) {
            System.out.println(Integer.toHexString(cp));
        }
    }
}
