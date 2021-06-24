    /**
     * 根据id数组，批量查询${tableInfo.tableAlias}
     */
    public List<${tableInfo.entityClassName}Data> query${tableInfo.entityClassName}(String tenantId, ${tableInfo.entityClassName}IdsRequest request) {
        List<${tableInfo.entityClassName}> ${tableInfo.entityObjectName}List = judge${tableInfo.entityClassName}ExistByIdsAndTenantId(request.getIds(), tenantId);

        List<${tableInfo.entityClassName}Data> ${tableInfo.entityObjectName}DataList = new ArrayList<>();

        for (${tableInfo.entityClassName} ${tableInfo.entityObjectName} : ${tableInfo.entityObjectName}List) {
            ${tableInfo.entityObjectName}DataList.add(BeanUtil.copyProperties(${tableInfo.entityObjectName}, ${tableInfo.entityClassName}Data.class));
        }

        return ${tableInfo.entityObjectName}DataList;
    }