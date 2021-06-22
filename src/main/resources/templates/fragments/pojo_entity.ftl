<#list tableInfo.columnInfos as columnInfo>
    /**
     * ${columnInfo.columnComment}
     */
    <#if columnInfo.isPrimaryKey>
    @TableId(type = IdType.ID_WORKER_STR)
    </#if>
    <#if columnInfo.enumInfo??>
    private ${columnInfo.enumInfo.enumValueType} ${columnInfo.fieldObjectName};
    <#elseif columnInfo.internalClassInfo??>
    private ${columnInfo.fieldType} ${columnInfo.fieldObjectName};
    <#else>
    private ${columnInfo.fieldType} ${columnInfo.fieldObjectName};
    </#if>

</#list>