package fun.junjie.autotools.utils;

import org.apache.commons.lang3.StringUtils;

public class JStringUtils {

    /**
     * 下划线转驼峰
     */
    public static String underlineToCamel(String inputStr) {
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


//    public static String removeTableNamePrefix(String tableName) {
//        if (tableName.startsWith(UserConfig.TABLE_PREFIX)) {
//            return tableName.substring(UserConfig.TABLE_PREFIX.length());
//        } else {
//            return tableName;
//        }
//    }

}
