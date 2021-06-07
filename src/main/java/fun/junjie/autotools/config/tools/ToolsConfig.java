package fun.junjie.autotools.config.tools;

import fun.junjie.autotools.config.ProjectConfig;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
@ConfigurationProperties(prefix = "tools-config")
@RequiredArgsConstructor
public class ToolsConfig {
    private String projectName;
    private TablesConfig tablesConfig;
    private List<TemplatesConfig> templatesConfig;

    private final ProjectConfig projectConfig;

    public boolean isPrimaryKey(String tableName, String fieldName) {
        String primaryKeyName = projectConfig.getDefaultPrimaryKeyName();
        for (TableConfig tableConfig : tablesConfig.getTableConfig()) {
            if (tableConfig.getTableName().equals(tableName)) {
                if (tableConfig.getPrimaryKeyName() != null) {
                    primaryKeyName = tableConfig.getPrimaryKeyName();
                }
                break;
            }
        }
        return primaryKeyName.equals(fieldName);
    }

    public String getEntityName(String tableName) {
        for (TableConfig tableConfig : tablesConfig.getTableConfig()) {
            if (tableConfig.getTableName().equals(tableName)) {
                return tableConfig.getEntityName();
            }
        }
        return "";
    }
}
