    /**
     * 分页查询${tableInfo.tableAlias}
     */
    public Page<${tableInfo.entityClassName}Data> page${tableInfo.entityClassName}(String tenantId, Page${tableInfo.entityClassName}Request request) {
        LambdaQueryWrapper<${tableInfo.entityClassName}> queryWrapper = new LambdaQueryWrapper<${tableInfo.entityClassName}>()
                .eq(${tableInfo.entityClassName}::getOrgId, tenantId)
                .orderByDesc(${tableInfo.entityClassName}::getGmtCreateTime);

        Page<${tableInfo.entityClassName}> entityPage = new Page<>(
                request.getCurrent(),
                request.getLimit());

        ${tableInfo.entityObjectName}Mapper.selectPage(entityPage, queryWrapper);

        return PageUtils.entityPageToResponseDataPage(entityPage, ${tableInfo.entityClassName}Data.class);

    }