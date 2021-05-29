package fun.junjie.autotools.domain.yaml;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class TableRoot {

    private String tableName;
    private String tableDesc;
    private List<ColumnRoot> columns;

    public TableRoot(String tableName, String tableDesc) {
        this.tableName = tableName;
        this.tableDesc = tableDesc;

        columns = new ArrayList<>();
    }

    public void addColumn(ColumnRoot columnRoot) {
        if (columns == null) {
            columns = new ArrayList<>();
        }
        columns.add(columnRoot);
    }
}
