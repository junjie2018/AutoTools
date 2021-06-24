package ${templateConfig.filePackage};

import lombok.Data;

<#list templateConfig.packagesToImport as package>
import ${package};
</#list>

/**
 * @author wujj
 */
@Data
public class Create${tableInfo.entityClassName}Request {

<#list tableInfo.columnInfos as columnInfo>
    <#if columnInfo.enumInfo??>
    /**
     * ${columnInfo.columnComment}
     *
     * @see ${templateConfig.otherConfigs["enums-package"]}.${columnInfo.enumInfo.enumClassName}#value
     */
    private ${columnInfo.enumInfo.enumValueType} ${columnInfo.fieldObjectName};
    <#else>
    /**
     * ${columnInfo.columnComment}
     */
    private ${columnInfo.fieldType} ${columnInfo.fieldObjectName};
    </#if>

</#list>
}
