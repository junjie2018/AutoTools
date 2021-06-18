package fun.junjie.autotools.domain.postgre;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@SuppressWarnings("SpellCheckingInspection")
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum ColumnType {
    /**
     * varchar
     */
    VARCHAR(Arrays.asList("varchar")),

    /**
     * int2
     */
    INT2(Arrays.asList("int2")),

    /**
     * int4
     */
    INT4(Arrays.asList("int4")),

    /**
     * int8
     */
    INT8(Arrays.asList("int8")),

    /**
     * timestamptz
     */
    TIMESTAMPTZ(Arrays.asList("timestamptz")),

    /**
     * jsonb
     */
    JSONB(Arrays.asList("jsonb")),

    /**
     * date
     */
    DATE(Arrays.asList("date", "timestamp")),
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
