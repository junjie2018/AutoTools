package fun.junjie.autotools.domain.postgre;

import lombok.Data;

@Data
public class Column {
    private String columnName;
    private String columnDesc;
    private ColumnType columnType;

    public Column(String columnName, String remarks, String typeName) {
        this.columnName = columnName;
        this.columnDesc = remarks;
        this.columnType = ColumnType.toColumnType(typeName);
    }
}
