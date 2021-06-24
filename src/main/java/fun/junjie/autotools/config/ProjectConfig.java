package fun.junjie.autotools.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Data
@Component
@ConfigurationProperties(prefix = "project-config")
public class ProjectConfig {
    /**
     * 模板文件根目录
     */
    private String templateDir;

    /**
     * 储存表信息Yaml文件的文件夹
     */
    private String tableDataDir;

    /**
     * 枚举的模式
     */
    private String enumCommentPattern;

    /**
     * 数字的模式
     */
    private String numberPattern;

    public String getTemplateDir() {

        return templateDir.startsWith("classpath:") ?
                classpathLabelToAbsolute(templateDir) :
                templateDir;

    }

    public String getTableDataDir() {

        return tableDataDir.startsWith("classpath:") ?
                classpathLabelToAbsolute(tableDataDir) :
                tableDataDir;

    }

    private String classpathLabelToAbsolute(String path) {
        try {
            ClassPathResource templates = new ClassPathResource("templates");

            return ResourceUtils.getFile(templateDir).getAbsolutePath();
        } catch (IOException e) {
            throw new RuntimeException("TemplateDir Config Wrong");
        }
    }
}
