package fun.junjie.autotools.domain.yaml;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public enum JavaType {

    /**
     * NUMBER(Integer or Long)
     */
    NUMBER("Number"),

    /**
     * String
     */
    STRING("String"),

    /**
     * String EnumClass
     */
    STRING_ENUM("StringEnum"),

    /**
     * Number EnumClass
     */
    NUMBER_ENUM("NumberEnum"),

    /**
     * Date
     */
    DATE("Date"),

    /**
     * Object
     */
    OBJECT("Object"),

    ;

    private String javaTypeName;

    public static JavaType convert(String javaTypeName) {
        for (JavaType javaType : JavaType.values()) {
            if (javaType.getJavaTypeName().equals(javaTypeName)) {
                return javaType;
            }
        }
        throw new RuntimeException("Wrong Convert Params");
    }
}
