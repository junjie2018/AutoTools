    /**
     * 更新${tableInfo.tableAlias}
     */
    public void update${tableInfo.entityClassName}(String tenantId, Update${tableInfo.entityClassName}Request request) {
        judge${tableInfo.entityClassName}ExistByIdAndTenantId(request.getId(), tenantId);

        ${tableInfo.entityClassName} ${tableInfo.entityObjectName}Update = BeanUtil.copyProperties(request, ${tableInfo.entityClassName}.class);

        ${tableInfo.entityObjectName}Update.setId(request.getId());

        ${tableInfo.entityObjectName}Mapper.updateById(${tableInfo.entityObjectName}Update);
    }