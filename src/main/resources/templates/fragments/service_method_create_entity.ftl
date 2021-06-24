    /**
     * 创建${tableInfo.tableAlias}
     */
    public String create${tableInfo.entityClassName}(String userId, String tenantId, Create${tableInfo.entityClassName}Request request) {

        ${tableInfo.entityClassName} ${tableInfo.entityObjectName}Insert = ${tableInfo.entityClassName}.builder()
<#list tableInfo.columnInfos as columnInfo>
                .${columnInfo.fieldObjectName}(request.get${columnInfo.fieldClassName}())
</#list>
                .build();

        ${tableInfo.entityObjectName}Mapper.insert(${tableInfo.entityObjectName}Insert);


        return ${tableInfo.entityObjectName}Insert.getId();
    }