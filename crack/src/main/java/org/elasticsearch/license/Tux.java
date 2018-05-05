package org.elasticsearch.license;

public class Tux extends Thread{
    static  String aName = "vandeleru";
    public static void main(String[] args) {
        Tux t = new Tux();
        t.piggy(aName);
        System.out.println(aName);
    }
    public void piggy(String aName) {
        aName = aName + "wiggy";
        start();
    }
    @Override
    public void run() {
        for (int i = 0; i < 4; i ++) {
            aName = aName + " " + i;
        }
    }
}
