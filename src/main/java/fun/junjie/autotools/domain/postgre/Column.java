package fun.junjie.autotools.domain.postgre;

import lombok.Data;

@Data
public class Column {
    private String columnName;
    private String columnComment;
    private ColumnType columnType;

    public Column(String columnName, String remarks, String typeName) {
        this.columnName = columnName;
        this.columnComment = remarks;
        this.columnType = ColumnType.toColumnType(typeName);
    }
}
