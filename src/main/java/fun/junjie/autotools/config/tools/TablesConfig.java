package fun.junjie.autotools.config.tools;

import lombok.Data;

import java.util.List;

@Data
public class TablesConfig {
    private String tablePrefix;
    private List<TableConfig> tableConfig;
}
