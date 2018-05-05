package com.doubeye.record.quality.inspection.speachcraft.loader;

import com.doubeye.commons.utils.collection.CollectionUtils;
import com.doubeye.commons.utils.file.FileUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * @author doubeye
 * 将话术中句子的词频载入并整理的加载器
 */
@SuppressWarnings("unused | WeakerAccess")
public class TermFrequencyLoader {
    /**
     * 存放词频文件的根目录
     */
    private String rootDirectory;
    /**
     * 词频文件的过滤模式
     */
    private String frequencyPatten;
    /**
     * 保存原始句子的文件过滤模式
     */
    private String originPatten;


    public String getRootDirectory() {
        return rootDirectory;
    }

    public void setRootDirectory(String rootDirectory) {
        this.rootDirectory = rootDirectory;
    }

    public String getFrequencyPatten() {
        return frequencyPatten;
    }

    public void setFrequencyPatten(String frequencyPatten) {
        this.frequencyPatten = frequencyPatten;
    }

    public String getOriginPatten() {
        return originPatten;
    }

    public void setOriginPatten(String originPatten) {
        this.originPatten = originPatten;
    }

    /**
     * 加载词频文件，加载后的结果格式如下
     * 词频为JSONArray，每个元素为话术中的一句话，包括如下属性
     * id，话术语句的id，结构为文件名称__行号（句子号，从0开始）
     * sentence 原始语句
     * category 分类
     * termFrequency 词频
     * @throws IOException IO异常
     */
    public JSONArray load() throws IOException {
        JSONArray result = new JSONArray();
        List<File> wordFrequencyFiles = FileUtils.getAllFileInDirectory(rootDirectory, frequencyPatten, 0);
        wordFrequencyFiles.forEach(wordFrequencyFile -> {
            try {
                result.addAll(processSpeechcraft(wordFrequencyFile));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        return result;
    }

    /**
     * 处理词频文件
     * @param wordFrequencyFile 词频文件
     * @return 处理后的句子词频数组
     */
    private JSONArray processSpeechcraft(File wordFrequencyFile) throws IOException {
        JSONArray result = new JSONArray();
        String termFrequencyFileName = wordFrequencyFile.getName();
        String speechcraftFileName = wordFrequencyFile.getAbsolutePath().replace(frequencyPatten, "") + originPatten;
        JSONArray termFrequencies = CollectionUtils.toJSONArray(wordFrequencyFile.getAbsolutePath());
        List<String> sentences = CollectionUtils.loadFromFile(speechcraftFileName);
        for (int i = 0; i < termFrequencies.size(); i ++) {
            JSONObject entry = new JSONObject();
            String category = StringUtils.substringBetween(termFrequencyFileName, "_", frequencyPatten);
            entry.put(PROPERTY_NAME.TERM_FREQUENCY.toString(), termFrequencies.get(i));
            entry.put(PROPERTY_NAME.CATEGORY.toString(), category);
            entry.put(PROPERTY_NAME.ID.toString(), category + "__" + i);
            entry.put(PROPERTY_NAME.SENTENCE.toString(), sentences.get(i));
            result.add(entry);
        }
        return result;
    }

    public enum PROPERTY_NAME {
        /**
         * id
         */
        ID("id"),
        /**
         * 话术中的橘子
         */
        SENTENCE("sentence"),
        /**
         * 分类
         */
        CATEGORY("category"),
        /**
         * 词频
         */
        TERM_FREQUENCY("termFrequency"),
        ;
        private String propertyName;

        public String getPropertyName() {
            return propertyName;
        }

        PROPERTY_NAME(String propertyName) {
            this.propertyName = propertyName;
        }
        @Override
        public String toString() {
            return propertyName;
        }
    }

    public static void main(String[] args) throws IOException {
        TermFrequencyLoader loader = new TermFrequencyLoader();
        loader.setRootDirectory("d:/speechcraft");
        loader.setOriginPatten("_sc.txt");
        loader.setFrequencyPatten("_tf.txt");
        JSONArray result = loader.load();
        System.out.println(result.toString());
    }
}
