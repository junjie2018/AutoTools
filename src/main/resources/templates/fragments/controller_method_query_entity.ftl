    /**
     * 根据id数组，批量查询${tableInfo.tableAlias}
     */
    @PostMapping("/query${tableInfo.entityClassName}")
    public ResponseVo<List<${tableInfo.entityClassName}Data>> query${tableInfo.entityClassName}(
            @RequestHeader(APICons.HEADER_TENANT_ID) String tenantId,
            @RequestBody @Valid ${tableInfo.entityClassName}IdsRequest request) {
        return ResponseVo.createSuccessByData(${tableInfo.entityObjectName}Service.query${tableInfo.entityClassName}(tenantId, request));
    }