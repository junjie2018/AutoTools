    private List<${tableInfo.entityClassName}> judge${tableInfo.entityClassName}ExistByIdsAndTenantId(List<String> ids, String tenantId) {
        LambdaQueryWrapper<${tableInfo.entityClassName}> queryWrapper = new LambdaQueryWrapper<${tableInfo.entityClassName}>()
                .eq(${tableInfo.entityClassName}::getOrgId, tenantId)
                .in(${tableInfo.entityClassName}::getId, ids);

        List<${tableInfo.entityClassName}> ${tableInfo.entityObjectName}List = ${tableInfo.entityObjectName}Mapper.selectList(queryWrapper);

        if (${tableInfo.entityObjectName}List.size() != ids.size()) {
            throw new RuntimeException("Wrong");
        }

        return ${tableInfo.entityObjectName}List;
    }