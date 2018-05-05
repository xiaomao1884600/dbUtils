package com.doubeye.snippet;

/**
 * @author doubeye
 * 使用classLoader来载入资源（Maven项目结构）
 */
public class LoadResourceByClassLoader {
    public static void main(String[] args) {
        ClassLoader loader;
        loader = ClassLoader.getSystemClassLoader();
        loader = LoadResourceByClassLoader.class.getClassLoader();
        loader = Thread.currentThread().getContextClassLoader();
        if (loader == null) {
            System.out.println("no loader");
        }
        //不要在路径前面加/，否则表示当前类路径下的资源，资源存在在src/resources下，或者相应的maven配置的resource路径下
        System.out.println(loader.getResource("logisticRegression/all_enrolled.txt").getFile());

    }
}
