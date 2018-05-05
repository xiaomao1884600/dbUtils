package com.doubeye.log.analyzer;

import com.doubeye.commons.utils.file.FileUtils;
import net.sf.json.JSONObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;

public class LogReformat {
    private static final Logger logger = LogManager.getLogger(LogReformat.class);
    private static final String INPUT_DIRECTORY = "D:/logToBeChange";
    private static final String LINE_SEPARATOR = "\n";

    private static File[] getFileNames(String rootDirectory) {
        File path = new File(rootDirectory);
        return path.listFiles();
    }

    private static void processOnlyRequestTime() throws IOException {
        File[] logFiles = getFileNames(INPUT_DIRECTORY);
        for (File logFile : logFiles) {
            try (
                    InputStreamReader reader = new InputStreamReader(new FileInputStream(logFile), "utf-8");
                    BufferedReader bufferedReader = new BufferedReader(reader)
            ) {
                String line;
                long lineNumber = 0;
                long errorCount = 0;
                StringBuilder stringBuilder = new StringBuilder();
                while ((line = bufferedReader.readLine()) != null) {
                    lineNumber++;
                    // 跳过第一行
                    if (lineNumber > 1) {
                        try {
                            //String processed = EControllerLogFileAnalyzer.processLogEntryOnlyConvertTimestamp(line);
                            String processed = EControllerLogFileAnalyzer.processLogEntryOnlyConvertTimestampAppend(line);
                            stringBuilder.append(processed).append(LINE_SEPARATOR);
                        } catch (Exception e) {
                            errorCount ++;
                            logger.error(lineNumber + "       " + e.getMessage() + "       " + line);
                        }
                    }
                    if (lineNumber % 5000 == 0) {
                        logger.trace(logFile + " processed " + lineNumber);
                    }
                }
                String jsonLogFileName = logFile.getCanonicalPath().replace(".txt", ".changed");
                System.out.println("errorCount = " + errorCount);
                FileUtils.toFile(jsonLogFileName, stringBuilder);
            }
        }
    }

    private static void processToJson() throws IOException {
        if (true) {
            JSONObject obj = EControllerLogFileAnalyzer.processLogEntry(TEST);
            System.out.println(obj.toString());
            return;
        }
        File[] logFiles = getFileNames(INPUT_DIRECTORY);
        for (File logFile : logFiles) {
            try (
                    InputStreamReader reader = new InputStreamReader(new FileInputStream(logFile), "utf-8");
                    BufferedReader bufferedReader = new BufferedReader(reader)
            ) {
                String line;
                long lineNumber = 0;
                long errorCount = 0;
                StringBuilder stringBuilder = new StringBuilder();
                while ((line = bufferedReader.readLine()) != null) {
                    lineNumber++;
                    // 跳过第一行
                    if (lineNumber > 1) {
                        try {
                            JSONObject entry = EControllerLogFileAnalyzer.processLogEntry(line);
                            stringBuilder.append(entry.toString()).append(LINE_SEPARATOR);
                        } catch (Exception e) {
                            errorCount ++;
                            logger.error(lineNumber + "       " + e.getMessage() + "       " + line);
                        }
                    }
                    if (lineNumber % 5000 == 0) {
                        logger.trace(logFile + " processed " + lineNumber);
                    }
                }
                String jsonLogFileName = logFile.getCanonicalPath().replace(".txt", ".json");
                System.out.println("errorCount = " + errorCount);
                FileUtils.toFile(jsonLogFileName, stringBuilder);
            }
        }
    }

    public static void main(String[] args) throws IOException {
        processOnlyRequestTime();
    }


    private static final String TEST = "25BBB63D-5AD2-232C-508F-4485C01FDE64 | App\\Http\\Controllers\\Teaching\\TimetableController@anySetclazzcourse | {\"v\":\"1500519288303\",\"data\":{\"clazzid\":\"5619\",\"versionid\":\"18\",\"courseinfos\":[{\"clazzcourseid\":\"74356\",\"clazzid\":\"5619\",\"courseid\":\"3474\",\"coursesort\":\"32\",\"coursecolor\":\"#46d8b8\",\"title\":\"\\u6e38\\u620f\\u539f\\u753b\\u6bd5\\u4e1a\\u8bbe\\u8ba1IV\",\"code\":\"GA2D14183\",\"course_sign\":\"1\",\"course_diff_sign\":\"0\"},{\"clazzcourseid\":\"74357\",\"clazzid\":\"5619\",\"courseid\":\"4166\",\"coursesort\":\"33\",\"coursecolor\":\"#56a4dd\",\"title\":\"\\u7b80\\u5f0f\\u98ce\\u683c\\u4eba\\u4f53\\u8bfe\",\"code\":\"GA2D10279\",\"course_sign\":\"1\",\"course_diff_sign\":\"1\"},{\"clazzcourseid\":\"74358\",\"clazzid\":\"5619\",\"courseid\":\"4167\",\"coursesort\":\"34\",\"coursecolor\":\"#bc87e9\",\"title\":\"\\u6e38\\u620f\\u89d2\\u8272\\u5f62\\u4f53\\u8868\\u73b0\\u8bfe\",\"code\":\"GA2D10280\",\"course_sign\":\"1\",\"course_diff_sign\":\"1\"},{\"clazzcourseid\":\"74359\",\"clazzid\":\"5619\",\"courseid\":\"4168\",\"coursesort\":\"35\",\"coursecolor\":\"#c0a58a\",\"title\":\"\\u89d2\\u8272\\u8bbe\\u8ba1\\u8bfe\",\"code\":\"GA2D10281\",\"course_sign\":\"1\",\"course_diff_sign\":\"1\"},{\"clazzcourseid\":\"74360\",\"clazzid\":\"5619\",\"courseid\":\"4169\",\"coursesort\":\"36\",\"coursecolor\":\"#f0aa31\",\"title\":\"\\u89d2\\u8272\\u8868\\u73b0\\u8bfe\",\"code\":\"GA2D10282\",\"course_sign\":\"1\",\"course_diff_sign\":\"1\"},{\"clazzcourseid\":\"74361\",\"clazzid\":\"5619\",\"courseid\":\"4170\",\"coursesort\":\"37\",\"coursecolor\":\"#46d8b8\",\"title\":\"\\u4e3b\\u89d2\\u4e3b\\u9898\\u8bbe\\u8ba1\\u8bfe\",\"code\":\"GA2D10283\",\"course_sign\":\"1\",\"course_diff_sign\":\"1\"},{\"clazzcourseid\":\"74362\",\"clazzid\":\"5619\",\"courseid\":\"4171\",\"coursesort\":\"38\",\"coursecolor\":\"#8ec136\",\"title\":\"\\u4e3b\\u89d2\\u8868\\u73b0\\u8bbe\\u8ba1\\u8bfe\",\"code\":\"GA2D10284\",\"course_sign\":\"1\",\"course_diff_sign\":\"1\"},{\"clazzcourseid\":\"74363\",\"clazzid\":\"5619\",\"courseid\":\"4172\",\"coursesort\":\"39\",\"coursecolor\":\"#c73d6e\",\"title\":\"\\u4e3b\\u89d2\\u5957\\u88c5\\u8bbe\\u8ba1\\u8bfe\",\"code\":\"GA2D10285\",\"course_sign\":\"1\",\"course_diff_sign\":\"1\"},{\"clazzcourseid\":\"74364\",\"clazzid\":\"5619\",\"courseid\":\"4173\",\"coursesort\":\"40\",\"coursecolor\":\"#4169ec\",\"title\":\"\\u4e3b\\u89d2\\u5957\\u88c5\\u5347\\u7ea7\\u8bfe\",\"code\":\"GA2D10286\",\"course_sign\":\"1\",\"course_diff_sign\":\"1\"},{\"clazzcourseid\":\"74365\",\"clazzid\":\"5619\",\"courseid\":\"4174\",\"coursesort\":\"41\",\"coursecolor\":\"#ef622b\",\"title\":\"\\u4e09\\u5934\\u8eab\\u5973\\u804c\\u4e1a\\u8bbe\\u8ba1\",\"code\":\"GA2D10287\",\"course_sign\":\"1\",\"course_diff_sign\":\"1\"},{\"clazzcourseid\":\"74366\",\"clazzid\":\"5619\",\"courseid\":\"4175\",\"coursesort\":\"42\",\"coursecolor\":\"#43c143\",\"title\":\"\\u4e09\\u5934\\u8eab\\u7537\\u804c\\u4e1a\\u8bbe\\u8ba1\",\"code\":\"GA2D10288\",\"course_sign\":\"1\",\"course_diff_sign\":\"1\"},{\"clazzcourseid\":\"74367\",\"clazzid\":\"5619\",\"courseid\":\"4176\",\"coursesort\":\"43\",\"coursecolor\":\"#2db8d7\",\"title\":\"\\u4e94\\u5934\\u8eab\\u5973\\u804c\\u4e1a\\u8bbe\\u8ba1\",\"code\":\"GA2D10289\",\"course_sign\":\"1\",\"course_diff_sign\":\"1\"},{\"clazzcourseid\":\"74368\",\"clazzid\":\"5619\",\"courseid\":\"4177\",\"coursesort\":\"44\",\"coursecolor\":\"#ef777a\",\"title\":\"\\u4e94\\u5934\\u8eab\\u7537\\u804c\\u4e1a\\u8bbe\\u8ba1\",\"code\":\"GA2D10290\",\"course_sign\":\"1\",\"course_diff_sign\":\"1\"},{\"clazzcourseid\":\"74369\",\"clazzid\":\"5619\",\"courseid\":\"4178\",\"coursesort\":\"45\",\"coursecolor\":\"#56a4dd\",\"title\":\"\\u82f1\\u96c4\\u8fd1\\u6218\\u804c\\u4e1a\\u89d2\\u8272\\u8bbe\\u8ba1\",\"code\":\"GA2D10291\",\"course_sign\":\"1\",\"course_diff_sign\":\"1\"},{\"clazzcourseid\":\"74370\",\"clazzid\":\"5619\",\"courseid\":\"4179\",\"coursesort\":\"46\",\"coursecolor\":\"#bc87e9\",\"title\":\"\\u82f1\\u96c4\\u8fdc\\u7a0b\\u804c\\u4e1a\\u89d2\\u8272\\u8bbe\\u8ba1\",\"code\":\"GA2D10292\",\"course_sign\":\"1\",\"course_diff_sign\":\"1\"},{\"clazzcourseid\":\"74371\",\"clazzid\":\"5619\",\"courseid\":\"4180\",\"coursesort\":\"47\",\"coursecolor\":\"#c0a58a\",\"title\":\"\\u82f1\\u96c4\\u8f85\\u52a9\\u804c\\u4e1a\\u89d2\\u8272\\u8bbe\\u8ba1\\u8bfe\",\"code\":\"GA2D10293\",\"course_sign\":\"1\",\"course_diff_sign\":\"1\"},{\"clazzcourseid\":\"74372\",\"clazzid\":\"5619\",\"courseid\":\"4181\",\"coursesort\":\"48\",\"coursecolor\":\"#f0aa31\",\"title\":\"\\u82f1\\u96c4\\u804c\\u4e1a\\u76ae\\u80a4\\u7f6e\\u6362\\u8bbe\\u8ba1\",\"code\":\"GA2D10294\",\"course_sign\":\"1\",\"course_diff_sign\":\"1\"},{\"clazzcourseid\":\"74373\",\"clazzid\":\"5619\",\"courseid\":\"4182\",\"coursesort\":\"49\",\"coursecolor\":\"#46d8b8\",\"title\":\"\\u6e38\\u620f\\u573a\\u666f\\u690d\\u88ab\\u8bbe\\u8ba1\\u8bfe\",\"code\":\"GA2D10295\",\"course_sign\":\"1\",\"course_diff_sign\":\"1\"},{\"clazzcourseid\":\"74374\",\"clazzid\":\"5619\",\"courseid\":\"4183\",\"coursesort\":\"50\",\"coursecolor\":\"#8ec136\",\"title\":\"\\u6e38\\u620f\\u573a\\u666f\\u5c71\\u4f53\\u8bbe\\u8ba1\\u8bfe\",\"code\":\"GA2D10296\",\"course_sign\":\"1\",\"course_diff_sign\":\"1\"},{\"clazzcourseid\":\"74375\",\"clazzid\":\"5619\",\"courseid\":\"4184\",\"coursesort\":\"51\",\"coursecolor\":\"#c73d6e\",\"title\":\"\\u6e38\\u620f\\u6a2a\\u7248\\u8bbe\\u8ba1\\u8bfeI\",\"code\":\"GA2D10297\",\"course_sign\":\"1\",\"course_diff_sign\":\"1\"},{\"clazzcourseid\":\"74376\",\"clazzid\":\"5619\",\"courseid\":\"4185\",\"coursesort\":\"52\",\"coursecolor\":\"#4169ec\",\"title\":\"\\u6e38\\u620f\\u6a2a\\u7248\\u8bbe\\u8ba1\\u8bfeII\",\"code\":\"GA2D10298\",\"course_sign\":\"1\",\"course_diff_sign\":\"1\"},{\"clazzcourseid\":\"74377\",\"clazzid\":\"5619\",\"courseid\":\"4186\",\"coursesort\":\"53\",\"coursecolor\":\"#ef622b\",\"title\":\"\\u6e38\\u620f\\u529f\\u80fd\\u6027\\u573a\\u666f\\u5efa\\u7b51\\u8bbe\\u8ba1I\",\"code\":\"GA2D10299\",\"course_sign\":\"1\",\"course_diff_sign\":\"1\"},{\"clazzcourseid\":\"74378\",\"clazzid\":\"5619\",\"courseid\":\"4187\",\"coursesort\":\"54\",\"coursecolor\":\"#43c143\",\"title\":\"\\u6e38\\u620f\\u529f\\u80fd\\u6027\\u573a\\u666f\\u5efa\\u7b51\\u8bbe\\u8ba1II\",\"code\":\"GA2D10300\",\"course_sign\":\"1\",\"course_diff_sign\":\"1\"},{\"clazzcourseid\":\"74379\",\"clazzid\":\"5619\",\"courseid\":\"4188\",\"coursesort\":\"55\",\"coursecolor\":\"#2db8d7\",\"title\":\"\\u6e38\\u620f\\u573a\\u666f\\u89c4\\u5212\\u8bbe\\u8ba1\\u8bfe\",\"code\":\"GA2D10301\",\"course_sign\":\"1\",\"course_diff_sign\":\"1\"},{\"clazzcourseid\":\"74380\",\"clazzid\":\"5619\",\"courseid\":\"4189\",\"coursesort\":\"56\",\"coursecolor\":\"#ef777a\",\"title\":\"\\u6e38\\u620f\\u573a\\u666f\\u6982\\u5ff5\\u8bbe\\u8ba1\\u8bfe\",\"code\":\"GA2D10302\",\"course_sign\":\"1\",\"course_diff_sign\":\"1\"},{\"clazzcourseid\":\"74381\",\"clazzid\":\"5619\",\"courseid\":\"4190\",\"coursesort\":\"57\",\"coursecolor\":\"#56a4dd\",\"title\":\"\\u6e38\\u620f\\u5361\\u724c\\u8bbe\\u8ba1\\u8bfeI\",\"code\":\"GA2D10303\",\"course_sign\":\"1\",\"course_diff_sign\":\"1\"},{\"clazzcourseid\":\"74382\",\"clazzid\":\"5619\",\"courseid\":\"4191\",\"coursesort\":\"58\",\"coursecolor\":\"#bc87e9\",\"title\":\"\\u6e38\\u620f\\u5361\\u724c\\u8bbe\\u8ba1\\u8bfeII\",\"code\":\"GA2D10304\",\"course_sign\":\"1\",\"course_diff_sign\":\"1\"},{\"clazzcourseid\":\"74383\",\"clazzid\":\"5619\",\"courseid\":\"4192\",\"coursesort\":\"59\",\"coursecolor\":\"#c0a58a\",\"title\":\"\\u6e38\\u620f\\u6d77\\u62a5\\u8bbe\\u8ba1\\u8bfeI\",\"code\":\"GA2D10305\",\"course_sign\":\"1\",\"course_diff_sign\":\"1\"},{\"clazzcourseid\":\"74384\",\"clazzid\":\"5619\",\"courseid\":\"4193\",\"coursesort\":\"60\",\"coursecolor\":\"#f0aa31\",\"title\":\"\\u6e38\\u620f\\u6d77\\u62a5\\u8bbe\\u8ba1\\u8bfeII\",\"code\":\"GA2D10306\",\"course_sign\":\"1\",\"course_diff_sign\":\"1\"},{\"clazzcourseid\":\"74385\",\"clazzid\":\"5619\",\"courseid\":\"4194\",\"coursesort\":\"61\",\"coursecolor\":\"#46d8b8\",\"title\":\"\\u6e38\\u620f\\u539f\\u753b\\u6bd5\\u4e1a\\u8bbe\\u8ba1\\u8bfeI\",\"code\":\"GA2D10307\",\"course_sign\":\"1\",\"course_diff_sign\":\"1\"},{\"clazzcourseid\":\"74386\",\"clazzid\":\"5619\",\"courseid\":\"4195\",\"coursesort\":\"62\",\"coursecolor\":\"#8ec136\",\"title\":\"\\u6e38\\u620f\\u539f\\u753b\\u6bd5\\u4e1a\\u8bbe\\u8ba1\\u8bfeII\",\"code\":\"GA2D10308\",\"course_sign\":\"1\",\"course_diff_sign\":\"1\"},{\"clazzcourseid\":\"74387\",\"clazzid\":\"5619\",\"courseid\":\"4196\",\"coursesort\":\"63\",\"coursecolor\":\"#c73d6e\",\"title\":\"\\u6e38\\u620f\\u539f\\u753b\\u6bd5\\u4e1a\\u8bbe\\u8ba1\\u8bfeIII\",\"code\":\"GA2D10309\",\"course_sign\":\"1\",\"course_diff_sign\":\"1\"},{\"clazzcourseid\":\"74388\",\"clazzid\":\"5619\",\"courseid\":\"4197\",\"coursesort\":\"64\",\"coursecolor\":\"#4169ec\",\"title\":\"\\u6e38\\u620f\\u5e94\\u8058\\u6a21\\u62df\\u6d4b\\u8bd5\\u8bfe\",\"code\":\"GA2D10310\",\"course_sign\":\"0\",\"course_diff_sign\":\"1\"}]},\"token\":\"qj1qihnkpv1zzd2c495950805ae0d987827be7b2816c2\",\"type\":\"1\"} | 3076 | 10.2.14.22 | 0 | 1500519252 | 1500519252";

}
