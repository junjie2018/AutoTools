package fun.junjie.autotools.domain.postgre;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum ColumnType {
    /**
     *
     */
    VARCHAR("varchar"),
    INT2("int2"),
    INT4("int4"),
    TIMESTAMPTZ("timestamptz"),
    JSONB("jsonb"),
    DATE("date"),
    ;

    @Getter
    private String nameFromJdbcTemplate;

    public static ColumnType toColumnType(String typeName) {

        for (ColumnType columnType : ColumnType.values()) {
            if (typeName.equals(columnType.getNameFromJdbcTemplate())) {
                return columnType;
            }
        }

        throw new RuntimeException("No Such typeName");
    }
}
