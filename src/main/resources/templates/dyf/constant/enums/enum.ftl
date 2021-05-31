package ${enumClass.packageName};

<#list enumClass.packagesToImport as packageName>
import ${packageName};
</#list>

/**
 * ${enumClass.enumDesc}
 */
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public enum ${enumClass.enumName} {


    <#list enumClass.enumItems as enumItem>
        /**
        * ${enumItem.enumItemDesc}
        */
        ${enumItem.enumItemName}("${enumItem.enumItemValue}"),

    </#list>
    ;

    @Getter
    private String value;

    public static ${enumClass.enumName} convert(String inputValue) {
        for (${enumClass.enumName} enumItem : ${enumClass.enumName}.values()) {
        if (enumItem.getValue().equals(inputValue)) {
            return enumItem;
            }
        }
        throw new RuntimeException("Enum Transfer Wrong.");
    }


}