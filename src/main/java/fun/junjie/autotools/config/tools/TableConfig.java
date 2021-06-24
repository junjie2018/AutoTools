package fun.junjie.autotools.config.tools;

import lombok.Data;

import java.util.List;

@Data
public class TableConfig {
    private String tableName;
    private String tableAlias;
    private String primaryKeyColumn;
    private List<String> templateToGenerate;
}
