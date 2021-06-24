package ${templateConfig.filePackage};

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * ${enumInfo.enumComment}
 *
 * @author wujj
 */
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public enum ${enumInfo.enumClassName} {

<#list enumInfo.enumItems as enumItem>
    /**
     * ${enumItem.enumItemComment}
     */
    <#if enumInfo.enumValueType=="String">
    ${enumItem.enumItemName}("${enumItem.enumItemValue}"),
    <#else>
    ${enumItem.enumItemName}(${enumItem.enumItemValue}),
    </#if>
</#list>
    ;

    @Getter
    private ${enumInfo.enumValueType} value;

    <#if enumInfo.enumValueType=="String">
    public static ${enumInfo.enumClassName} convert(String inputValue) {
    <#else>
    public static ${enumInfo.enumClassName} convert(Integer inputValue) {
    </#if>
        for (${enumInfo.enumClassName} enumItem : ${enumInfo.enumClassName}.values()) {
            if (enumItem.getValue().equals(inputValue)) {
                return enumItem;
            }
        }
        throw new RuntimeException("Enum Transfer Wrong.");
    }
}