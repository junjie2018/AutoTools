package fun.junjie.autotools.domain.postgre;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@SuppressWarnings({"SpellCheckingInspection", "ArraysAsListWithZeroOrOneArgument"})
public enum ColumnType {
    /**
     * varchar
     */
    VARCHAR(Arrays.asList("varchar")),

    /**
     * int
     */
    INT(Arrays.asList("int2", "int4", "int8")),

    /**
     * jsonb
     */
    JSONB(Arrays.asList("jsonb")),

    /**
     * date
     */
    DATE(Arrays.asList("date", "timestamp", "timestamptz")),
    ;

    @Getter
    private List<String> typeNameFromJdbcTemplate;

    public static ColumnType toColumnType(String typeName) {

        for (ColumnType columnType : ColumnType.values()) {
            if (columnType.getTypeNameFromJdbcTemplate().contains(typeName)) {
                return columnType;
            }
        }

        throw new RuntimeException("No Such typeName");
    }
}
