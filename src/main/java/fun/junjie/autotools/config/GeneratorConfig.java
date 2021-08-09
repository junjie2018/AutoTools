package fun.junjie.autotools.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Data
@Component
@ConfigurationProperties(prefix = "tools-config")
public class ToolsConfig {
    private String projectName;
    private String tablePrefix;
    private String defaultPrimaryKey;
    private Map<String, String> properties;
    private List<TableConfig> tableConfigs;
    private List<TemplateConfig> templateConfigs;

    public TemplateConfig getTemplateConfig(String templateFileName) {
        for (TemplateConfig templateConfig : templateConfigs) {
            if (templateConfig.getTemplateFilename().equals(templateFileName)) {
                return templateConfig;
            }
        }
        throw new RuntimeException("Wrong When Find TemplateConfig");
    }

    public String getEntityName(String tableName) {
        for (TableConfig tableConfig : tableConfigs) {
            if (tableName.equals(tableConfig.getTableName())) {
                return tableConfig.getEntityName();
            }
        }
        throw new RuntimeException("Wrong When Find TemplateConfig");
    }
}
