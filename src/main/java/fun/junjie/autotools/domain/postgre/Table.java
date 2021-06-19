package fun.junjie.autotools.domain.postgre;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Table {

    private String tableName;
    private String tableComment;

    private List<Column> columns;

    public Table(String tableName, String tableComment) {
        this.tableName = tableName;
        this.tableComment = tableComment == null ? "" : tableComment;
        this.columns = new ArrayList<>();
    }

    public void addColumn(Column column) {
        this.columns.add(column);
    }
}
