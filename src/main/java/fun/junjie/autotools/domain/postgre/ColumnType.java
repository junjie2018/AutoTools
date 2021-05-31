package fun.junjie.autotools.domain.postgre;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@SuppressWarnings("SpellCheckingInspection")
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum ColumnType {
    /**
     * varchar
     */
    VARCHAR("varchar"),

    /**
     * int2
     */
    INT2("int2"),

    /**
     * int4
     */
    INT4("int4"),

    /**
     * timestamptz
     */
    TIMESTAMPTZ("timestamptz"),

    /**
     * jsonb
     */
    JSONB("jsonb"),

    /**
     * date
     */
    DATE("date"),
    ;

    @Getter
    private String typeNameFromJdbcTemplate;

    public static ColumnType toColumnType(String typeName) {

        for (ColumnType columnType : ColumnType.values()) {
            if (typeName.equals(columnType.getTypeNameFromJdbcTemplate())) {
                return columnType;
            }
        }

        throw new RuntimeException("No Such typeName");
    }
}
