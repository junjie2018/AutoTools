package ${templateConfig.filePackage};

import ${templateConfig.otherConfigs["enums-package"]}.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ${tableInfo.entityClassName}Data {

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
