package fun.junjie.autotools.constant;

import java.util.regex.Pattern;

public class ToolsConfig {
    public static Pattern ENUM_COMMENT_PATTERN = Pattern.compile("^([\\u4e00-\\u9fa5]{1,})（(([A-Za-z0-9]+：[\\u4e00-\\u9fa5]{1,}，?)+)）$");
    public static Pattern NUMBER_PATTERN = Pattern.compile("^[0-9]*$");
}
