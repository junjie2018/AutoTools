package fun.junjie.autotools.config.tools;

import lombok.Data;

@Data
public class TableConfig {
    private String tableName;
    private String entityName;
    private String primaryKeyName;
}
