package fun.junjie.autotools.config.tools;

import lombok.Data;

import java.util.List;

@Data
public class TablesConfig {
    private String tablePrefix;
    /**
     * 默认主键列名称
     */
    private String defaultPrimaryKeyColumn;
    private List<TableConfig> tableConfig;
}
