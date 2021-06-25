package ${templateConfig.filePackage};

import lombok.Data;
import ${templateConfig.otherConfigs["enums-package"]}.*;
import javax.validation.constraints.NotBlank;

@Data
public class Update${tableInfo.entityClassName}Request {

<#list tableInfo.columnInfos as columnInfo>
    <#if columnInfo.enumInfo??>
    /**
     * ${columnInfo.columnComment}
     *
     * @see ${templateConfig.otherConfigs["enums-package"]}.${columnInfo.enumInfo.enumClassName}#value
     */
    <#if columnInfo.isPrimaryKey>
    @NotBlank
    </#if>
    private ${columnInfo.enumInfo.enumValueType} ${columnInfo.fieldObjectName};
    <#else>
    /**
     * ${columnInfo.columnComment}
     */
    <#if columnInfo.isPrimaryKey>
    @NotBlank
    </#if>
    private ${columnInfo.fieldType} ${columnInfo.fieldObjectName};
    </#if>

</#list>
}
