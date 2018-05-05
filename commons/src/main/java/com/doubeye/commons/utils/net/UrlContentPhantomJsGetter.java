package com.doubeye.commons.utils.net;

import com.doubeye.commons.utils.collection.CollectionUtils;
import com.doubeye.commons.utils.runtime.RuntimeRunner;
import com.doubeye.commons.utils.runtime.RuntimeRunningResult;


import java.io.IOException;


public class UrlContentPhantomJsGetter {

    public static String getContent(String url) throws IOException, InterruptedException {
        RuntimeRunningResult result = RuntimeRunner.runCommand(getCommand(url), false, "utf-8");
        if (result.getRunningResult()) {
            return CollectionUtils.toString(result.getMessage(), "");
        } else {
            return CollectionUtils.toString(result.getErrorMessage(), "");
        }
    }

    public static String executeCode(String codeFileName) throws IOException, InterruptedException {
        String[] command = RuntimeRunner.getOSShellCommand(FILENAME_PHANTOM_JS_EXE + " " + codeFileName);
        RuntimeRunningResult result = RuntimeRunner.runCommand(command, false, "utf-8");
        if (result.getRunningResult()) {
            return CollectionUtils.toString(result.getMessage(), "");
        } else {
            return CollectionUtils.toString(result.getErrorMessage(), "");
        }
    }

    private static String[] getCommand(String url) {
        return RuntimeRunner.getOSShellCommand(FILENAME_PHANTOM_JS_EXE + " " + FILENAME_GETTER_CODE + " " + url);
    }

    private static final String FILENAME_PHANTOM_JS_EXE = "d:/phantomjs-2.1.1-windows/bin/phantomjs.exe ";
    private static final String FILENAME_GETTER_CODE = "D:/phantomjs-2.1.1-windows/code/getter.js ";

    public static void main(String[] args) throws IOException, InterruptedException {
        String result = getContent("https://www.tianyancha.com/search?key=%E5%B1%B1%E4%B8%9C%E9%9B%85%E7%99%BE%E7%89%B9%E7%A7%91&checkFrom=searchBox");
    }
}
