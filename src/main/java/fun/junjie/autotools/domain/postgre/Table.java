package fun.junjie.autotools.domain.postgre;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Table {

    private String tableName;
    private String tableDesc;

    private List<Column> columnList;

    public Table(String tableName, String tableDesc) {
        this.tableName = tableName;
        this.tableDesc = tableDesc == null ? "" : tableDesc;
        this.columnList = new ArrayList<>();
    }

    public void addColumn(Column column) {
        this.columnList.add(column);
    }
}
