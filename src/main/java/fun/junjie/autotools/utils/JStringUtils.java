package fun.junjie.autotools.utils;

import fun.junjie.autotools.config.ProjectConfig;
import fun.junjie.autotools.config.tools.ToolsConfig;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@RequiredArgsConstructor
public class JStringUtils {

    private final ToolsConfig toolsConfig;
    private final ProjectConfig projectConfig;
    private static ToolsConfig toolsConfigStatic;
    private static ProjectConfig projectConfigStatic;

    @PostConstruct
    public void init() {
        toolsConfigStatic = toolsConfig;
        projectConfigStatic = projectConfig;
    }

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

    /**
     * 移除表明的前缀
     */
    public static String removeTableNamePrefix(String tableName) {
        if (tableName.startsWith(toolsConfigStatic.getTablesConfig().getTablePrefix())) {
            return tableName.substring(toolsConfigStatic.getTablesConfig().getTablePrefix().length());
        } else {
            return tableName;
        }
    }
}
