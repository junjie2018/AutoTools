package fun.junjie.autotools.constant;

import fun.junjie.autotools.config.project.ProjectConfig;

import java.util.regex.Pattern;

public class ToolsConfig {
    public static Pattern ENUM_COMMENT_PATTERN = Pattern.compile("^([\\u4e00-\\u9fa5]{1,})（(([A-Za-z0-9-]+：[\\u4e00-\\u9fa5]{1,}，?)+)）$");
    public static Pattern NUMBER_PATTERN = Pattern.compile("^[0-9]*$");

    public static final String JAVA_TYPE = "fun.junjie.autotools.domain.yaml.JavaType";

    public static final String TEMP_DIR = "C:\\Users\\wujj\\AppData\\Local\\Temp\\AutoTools";

    public static final String TABLES_INFO_DIR = "D:\\Download\\spring-demo-master\\spring-demo-master\\cn\\AutoTools\\src\\main\\resources\\tables";

    public static final String COMPARE_EXE_PATH = "D:\\Software\\Beyond Compare 3\\BCompare.exe";

}
