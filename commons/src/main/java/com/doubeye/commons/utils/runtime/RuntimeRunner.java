package com.doubeye.commons.utils.runtime;


import com.doubeye.commons.utils.collection.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;
import java.util.ArrayList;
import java.util.List;


/**
 * @author doubeye
 * @version 1.0.0
 * 命令行运行助手类
 */
public class RuntimeRunner {
    /**
     * 日志
     */
    private static Logger logger = LogManager.getLogger(RuntimeRunner.class);

    /**
     * 运行命令
     * @param command 命令行
     * @return 运行结果
     * @throws IOException IO异常
     * @throws InterruptedException 终端异常
     */
    public static RuntimeRunningResult runCommand(String[] command) throws IOException, InterruptedException {
        /*
        boolean executeResult = true;
        logger.debug(CollectionUtils.toString(command, " "));
        Runtime runtime = Runtime.getRuntime();
        Process p = runtime.exec(command);
        logger.debug("runtime.exec(command) executed");
        RuntimeRunningResult result = new RuntimeRunningResult();

        String charset = guessConsoleEncoding().toString();
        logger.debug("shoing charset :" + charset);
        try {
            if (p.waitFor() != 0) {
                logger.debug("p.waitFor() != 0");
                executeResult = false;
            }
            result.setMessage(fromInputStream(p.getInputStream(), charset));
            logger.debug("normal Message : " + result.getMessage().toString());
            result.setErrorMessage(fromInputStream(p.getErrorStream(), charset));
            logger.debug("error Message : " + result.getErrorMessage().toString());
        } finally {
            p.destroy();
        }

        result.setRunningResult(executeResult);
        return result;
        */
        return runCommand(command, true);
    }

    /**
     * 运行命令
     * @param command 命令行
     * @return 运行结果
     * @throws IOException IO异常
     * @throws InterruptedException 终端异常
     */
    public static RuntimeRunningResult runCommand(String[] command, boolean wait) throws IOException, InterruptedException {
        return runCommand(command, wait, "");
    }


    /**
     * 运行命令
     * @param command 命令行
     * @return 运行结果
     * @throws IOException IO异常
     * @throws InterruptedException 终端异常
     */
    public static RuntimeRunningResult runCommand(String[] command, boolean wait, String charset) throws IOException, InterruptedException {
        boolean executeResult = true;
        logger.debug(CollectionUtils.toString(command, " "));
        Runtime runtime = Runtime.getRuntime();
        Process p = runtime.exec(command);
        logger.debug("runtime.exec(command) executed");
        RuntimeRunningResult result = new RuntimeRunningResult();

        if (StringUtils.isEmpty(charset)) {
            charset = guessConsoleEncoding().toString();
        }
        logger.debug("showing charset :" + charset);
        try {
            if (wait && p.waitFor() != 0) {
                logger.debug("p.waitFor() != 0");
                executeResult = false;
            }
            result.setMessage(fromInputStream(p.getInputStream(), charset));
            //logger.debug("normal Message : " + result.getMessage().toString());
            result.setErrorMessage(fromInputStream(p.getErrorStream(), charset));
            //logger.debug("error Message : " + result.getErrorMessage().toString());
        } finally {
            p.destroy();
        }

        result.setRunningResult(executeResult);
        return result;
    }

    /**
     * 从输入流中按指定字符集获得字符串列表
     * @param is InputStream对象
     * @param charset 字符集
     * @return 输入流中的字符串形式
     * @throws IOException IO异常
     */
    private static List<String> fromInputStream(InputStream is, String charset) throws IOException {
        List<String> result = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, charset))) {
            String line;
            while ((line = reader.readLine()) != null) {
                result.add(line);
            }
            return result;
        } finally {
            is.close();
        }
    }
    /* TODO 放到单元测试
    public static void main(String[] args) throws IOException, InterruptedException {
        RuntimeRunningResult result = runCommand(new String[]{"cmd", "/C", "ipconfig"});
    }
    */

    /**
     * 获得控制台的字符集
     * @return 控制台的字符集
     * @author 此方法摘自StackOverfloaw,忘记作者是谁了
     */
    @SuppressWarnings("WeakerAccess")
    public static Charset guessConsoleEncoding() {
        // We cannot use Console class directly, because we also need the access to the raw byte stream,
        // e.g. for pushing in a raw output from a forked VM invocation. Therefore, we are left with
        // reflectively poking out the Charset from Console, and use it for our own private output streams.

        try {
            Field f = Console.class.getDeclaredField("cs");
            f.setAccessible(true);
            Console console = System.console();
            if (console != null) {
                Object res = f.get(console);
                if (res instanceof Charset) {
                    return (Charset) res;
                }
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            logger.error(e.getMessage());
        }

        try {
            Method m = Console.class.getDeclaredMethod("encoding");
            m.setAccessible(true);
            Object res = m.invoke(null);
            if (res instanceof String) {
                return Charset.forName((String) res);
            }
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException | IllegalCharsetNameException | UnsupportedCharsetException e) {
            logger.error(e.getMessage());
        }
        return Charset.defaultCharset();
    }

    public static String[] getOSShellCommand(String command) {
        if (System.getProperty("os.name").toLowerCase().startsWith("win")) {
            System.out.println(command);
            return new String[]{"cmd", "/C", command};
        } else {
            return new String[]{"sh", "-c", command};
        }
    }
}
