    /**
     * 根据id数组，批量删除${tableInfo.tableAlias}
     */
    public void delete${tableInfo.entityClassName}(String tenantId, ${tableInfo.entityClassName}IdsRequest request) {
        judge${tableInfo.entityClassName}ExistByIdsAndTenantId(request.getIds(), tenantId);

        ${tableInfo.entityObjectName}Mapper.deleteBatchIds(request.getIds());
    }