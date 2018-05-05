package com.doubeye.commons.utils.automation.classGenerator;

import com.doubeye.commons.utils.collection.CollectionUtils;
import com.doubeye.commons.utils.file.FileUtils;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author doubeye
 * @version 1.0.1
 * 用来生成常量类定义的助手
 * 历史
 *  1.0.1
 *      + 添加增加抑制警告的属性及相关方法
 */
public class ConstantClassHelper {
    /**
     * import 列表
     */
    private List<String> imports = new ArrayList<>();
    /**
     * 常量字段列表
     */
    private List<ConstantField> fields = new ArrayList<>();
    /**
     * 包名
     */
    private String packageName;
    /**
     * 类名
     */
    private String className;
    /**
     * 项目源代码的根目录
     */
    private String projectSourceRoot;
    /**
     * 作者
     */
    private String author = "doubeye";
    /**
     * 版本
     */
    private String version = "1.0.0";
    /**
     * 类的注释
     */
    private String classComment;
    /**
     * @SuppressWarning
     */
    private String suppresses = "unused";
    /**
     * 类的内容
     */
    private StringBuilder classContent = new StringBuilder();

    public List<ConstantField> getFields() {
        return fields;
    }

    public void setFields(List<ConstantField> fields) {
        this.fields = fields;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public void setProjectSourceRoot(String projectSourceRoot) {
        this.projectSourceRoot = projectSourceRoot;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setClassComment(String classComment) {
        this.classComment = classComment;
    }

    public String getSuppresses() {
        return suppresses;
    }

    public void setSuppresses(String suppresses) {
        this.suppresses = suppresses;
    }

    /**
     * 添加一个字段
     * @param field 字段
     */
    public void addField(ConstantField field) {
        fields.add(field);
    }

    public void generateClassContent() throws IOException {
        //包名
        classContent.append(String.format(PACKAGE_NAME_TEMPLATE, packageName)).append(LINE_SEPARATOR);
        //imports
        imports.forEach(element -> classContent.append(String.format(IMPORT_TEMPLATE, element)).append(LINE_SEPARATOR));
        //类注释开始
        classContent.append(JAVA_DOC_COMMENT_START);
        //作者
        classContent.append(JAVA_DOC_COMMENT_LINE_START).append(String.format(JAVA_DOC_AUTHOR_TEMPLATE, author)).append(LINE_SEPARATOR);
        //版本
        classContent.append(JAVA_DOC_COMMENT_LINE_START).append(String.format(JAVA_DOC_VERSION_TEMPLATE, version)).append(LINE_SEPARATOR);
        //类注释内容
        appendCommentContent(classContent, classComment);
        //自动生成内容
        classContent.append(AUTO_GENERATED_MESSAGE).append(LINE_SEPARATOR);
        classContent.append(JAVA_DOC_COMMENT_END);
        //SuppressWarning
        classContent.append(String.format(SUPPRESS_WARNNG_TEMPLATE, suppresses)).append(LINE_SEPARATOR);
        //类名
        classContent.append(String.format(CLASS_NAME_TEMPLATE, className)).append(LINE_SEPARATOR);
        //所有的静态属性
        appendFields();
        //类结束的括号
        classContent.append(CLASS_END);
        writeToClassFile();
    }

    /**
     * 生成所有的静态属性内容
     */
    private void appendFields() {
        fields.forEach(field -> classContent.append(field.getDefinition()).append(LINE_SEPARATOR));
    }

    /**
     * 将注释的内容按行进行分割
     * @param builder StringBuilder 对象
     * @param commentContent 注释内容
     */
    private static void appendCommentContent(StringBuilder builder, String commentContent) {
        appendCommentContent(builder, commentContent, "");
    }

    /**
     * 将注释的内容按行进行分割
     * @param builder StringBuilder 对象
     * @param commentContent 注释内容
     */
    static void appendCommentContent(StringBuilder builder, String commentContent, String linePrefix) {
        List<String> list = CollectionUtils.split(commentContent, LINE_SEPARATOR);
        list.forEach(line -> builder.append(linePrefix).append(JAVA_DOC_COMMENT_LINE_START).append(line).append(LINE_SEPARATOR));
    }



    /**
     * 将内容写入到文件
     */
    private void writeToClassFile() throws IOException {
        String fileName = getFullClassFileName(projectSourceRoot, packageName, className);
        FileUtils.toFile(fileName, classContent);
    }

    /**
     * 获得类文件的完整名称
     * @param projectSourceRoot 项目根目录
     * @param packageName 包名
     * @param className 类名
     * @return 包含文件路径的类文件名
     */
    private static String getFullClassFileName(String projectSourceRoot, String packageName, String className) {
        if (!projectSourceRoot.endsWith("/")) {
            projectSourceRoot += "/";
        }
        return projectSourceRoot + packageName.replace(".", "/") + "/" + className + CLASS_EXTENT_FILE_NAME;
    }

    /**
     * 行结束符
     */
    static final String LINE_SEPARATOR = "\r\n";
    /**
     * 自动生成内容
     */
    private static final String AUTO_GENERATED_MESSAGE = "This class is auto-generated by tool ConstantClassHelper(developed by doubeye(doubeye@sina.com))";
    /**
     * class文件扩展名
     */
    private static final String CLASS_EXTENT_FILE_NAME = ".java";
    /**
     * JAVA DOC 注释开头
     */
    static final String JAVA_DOC_COMMENT_START = "/**" + LINE_SEPARATOR;
    /**
     * JAVA DOC 注释行开头
     */
    private static final String JAVA_DOC_COMMENT_LINE_START = " * ";
    /**
     * JAVA DOC 注释结尾
     */
    static final String JAVA_DOC_COMMENT_END = " */" + LINE_SEPARATOR;
    /**
     * 作者注释模板
     */
    private static final String JAVA_DOC_AUTHOR_TEMPLATE = "@author %s";
    /**
     * 版本注释模板
     */
    private static final String JAVA_DOC_VERSION_TEMPLATE = "@version %s";
    /**
     * 包模板
     */
    private static final String PACKAGE_NAME_TEMPLATE = "package %s;";

    private static final String IMPORT_TEMPLATE = "import %s;";

    private static final String SUPPRESS_WARNNG_TEMPLATE = "@SuppressWarnings(\"%s\")";

    /**
     * 类定义模板
     */
    private static final String CLASS_NAME_TEMPLATE = "public class %s {";
    /**
     * 类定义结尾
     */
    private static final String CLASS_END = "}";

    public void addImport(String importClass) {
        imports.add(importClass);
    }
}
