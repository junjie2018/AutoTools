    private ${tableInfo.entityClassName} judge${tableInfo.entityClassName}ExistByIdAndTenantId(String id, String tenantId) {
        LambdaQueryWrapper<${tableInfo.entityClassName}> queryWrapper = new LambdaQueryWrapper<${tableInfo.entityClassName}>()
                .eq(${tableInfo.entityClassName}::getId, id)
                .eq(${tableInfo.entityClassName}::getOrgId, tenantId)
                .select(${tableInfo.entityClassName}::getId);

        ${tableInfo.entityClassName} ${tableInfo.entityObjectName} = ${tableInfo.entityObjectName}Mapper.selectOne(queryWrapper);

        if (${tableInfo.entityObjectName} == null) {
            throw new RuntimeException("Wrong");
        }

        return ${tableInfo.entityObjectName};
    }