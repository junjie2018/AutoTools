package fun.junjie.autotools.domain.postgre;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Data
public class Table {

    private String prefix;
    private String tableName;
    private String tableDesc;

    List<Column> columnList;

    public Table(String tableName, String tableDesc) {

        this.prefix = "t_dyf_";

        this.tableName = tableName;
        this.tableDesc = tableDesc == null ? "" : tableDesc;

        this.columnList = new ArrayList<>();
    }

    public void addColumn(Column column) {
        this.columnList.add(column);
    }
}
