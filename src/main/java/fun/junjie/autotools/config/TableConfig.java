package fun.junjie.autotools.config;

import lombok.Data;

import java.util.List;

@Data
public class TableConfig {
    private String tableName;
    private String primaryKey;
    private List<String> templateToGenerate;
}
