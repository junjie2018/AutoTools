package fun.junjie.autotools.domain.java;

import fun.junjie.autotools.domain.postgre.Column;
import fun.junjie.autotools.domain.postgre.ColumnType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("ArraysAsListWithZeroOrOneArgument")
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum JavaType {

    /**
     * Integer
     */
    Long("Long",
            "java.lang",
            "java.lang.Long",
            new HashSet<>(Arrays.asList(ColumnType.INT2, ColumnType.INT4))),

    /**
     * String
     */
    String("String",
            "java.lang",
            "java.lang.String",
            new HashSet<>(Arrays.asList(ColumnType.VARCHAR))),

    /**
     * LocalDateTime
     */
    LOCAL_DATE_TIME("LocalDateTime",
            "java.time",
            "java.time.LocalDateTime",
            new HashSet<>(Arrays.asList(ColumnType.DATE, ColumnType.TIMESTAMPTZ))),

    /**
     * Object
     */
    Object("Object",
            "java.lang",
            "java.lang.Object",
            new HashSet<>(Arrays.asList(ColumnType.JSONB))),
    ;

    /**
     * String
     */
    @Getter
    private String javaTypeName;

    /**
     * java.lang
     */
    private String javaTypePackageName;

    /**
     * java.lang.String
     */
    private String javaTypeFullName;

    @Getter
    private Set<ColumnType> columnTypes;

    public static JavaType toJavaType(ColumnType columnType) {
        for (JavaType javaType : JavaType.values()) {
            if (javaType.getColumnTypes().contains(columnType)) {
                return javaType;
            }
        }

        throw new RuntimeException("未处理的ColumnType");
    }
}
