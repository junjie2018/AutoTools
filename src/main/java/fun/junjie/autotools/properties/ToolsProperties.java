package fun.junjie.autotools.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import java.io.IOException;
import java.util.regex.Pattern;

@Data
@Component
@ConfigurationProperties(prefix = "tools")
public class ToolsProperties {
    /**
     * 模板文件根目录
     */
    private String templateDir;

    /**
     * 枚举的模式
     */
    private Pattern enumCommentPattern;

    /**
     * 数字的模式
     */
    private Pattern numberPattern;

    public String getTemplateDir() {

        return templateDir.startsWith("classpath:") ?
                classpathLabelToAbsolute(templateDir) :
                templateDir;

    }

    private String classpathLabelToAbsolute(String path) {
        try {
            return ResourceUtils.getFile(path).getAbsolutePath();
        } catch (IOException e) {
            throw new RuntimeException("TemplateDir Config Wrong");
        }
    }
}