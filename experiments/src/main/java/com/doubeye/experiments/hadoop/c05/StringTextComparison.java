package com.doubeye.experiments.hadoop.c05;

import org.apache.hadoop.io.Text;

import java.io.UnsupportedEncodingException;

public class StringTextComparison {

    public static void string() throws UnsupportedEncodingException {
        String s = "\u0041\u00DF\u6771\uD801\uDC00";
        System.out.println(s.length());
        System.out.println(s.getBytes("UTF-8").length);

        System.out.println(s.indexOf("\u0041"));
        System.out.println(s.indexOf("\u00DF"));
        System.out.println(s.indexOf("\u6771"));
        System.out.println(s.indexOf("\uD801\uDC00"));

        System.out.println(s.charAt(0));
        System.out.println(s.charAt(1));
        System.out.println(s.charAt(2));
        System.out.println(s.charAt(3));
        System.out.println(s.charAt(4));

        System.out.println(s.codePointAt(0));
        System.out.println(s.codePointAt(1));
        System.out.println(s.codePointAt(2));
        System.out.println(s.codePointAt(3));
    }
    public static void text() {
        Text t = new Text("\u0041\u00DF\u6771\uD801\uDC00");
        System.out.println(t.getLength());
        System.out.println(t.find("\u0041"));
        System.out.println(t.find("\u00DF"));
        System.out.println(t.find("\u6771"));
        System.out.println(t.find("\uD801\uDC00"));

        System.out.println(t.charAt(0));
        System.out.println(t.charAt(1));
        System.out.println(t.charAt(3));
        System.out.println(t.charAt(6));
    }
    public static void main(String[] args) throws UnsupportedEncodingException {
        string();
        text();
    }

}
