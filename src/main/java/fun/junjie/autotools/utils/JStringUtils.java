package fun.junjie.autotools.utils;

import fun.junjie.autotools.config.project.ProjectConfig;
import org.apache.commons.lang3.StringUtils;

public class JStringUtils {

    /**
     * 下划线转驼峰（首字母大写）
     */
    public static String underlineToCamelCapitalized(String inputStr) {
        if (StringUtils.isBlank(inputStr)) {
            return "";
        }

        inputStr = StringUtils.trim(inputStr);

        String[] segments = inputStr.split("_");
        if (segments.length > 0) {
            for (int i = 0; i < segments.length; i++) {
                segments[i] = StringUtils.capitalize(segments[i]);
            }
        }

        return StringUtils.join(segments);
    }

    /**
     * 下划线转驼峰（首字母非大写）
     */
    public static String underlineToCamelUncapitalized(String inputStr) {
        if (StringUtils.isBlank(inputStr)) {
            return "";
        }

        inputStr = StringUtils.trim(inputStr);

        String[] segments = inputStr.split("_");
        if (segments.length > 0) {
            for (int i = 1; i < segments.length; i++) {
                segments[i] = StringUtils.capitalize(segments[i]);
            }
        }

        return StringUtils.join(segments);
    }


    public static String removeTableNamePrefix(String tableName) {
        if (tableName.startsWith(ProjectConfig.getTablePrefix())) {
            return tableName.substring(ProjectConfig.getTablePrefix().length());
        } else {
            return tableName;
        }
    }

}
