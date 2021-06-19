package fun.junjie.autotools.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "project-config")
public class ProjectConfig {
    /**
     * 模板文件根目录
     */
    private String templateDir;

    /**
     * 临时文件夹
     */
    private String tempDir;

    /**
     * 储存表信息Yaml文件的文件夹
     */
    private String tableInfoDir;

    /**
     * 枚举的模式
     */
    private String enumCommentPattern;

    /**
     * 数字的模式
     */
    private String numberPattern;
}
