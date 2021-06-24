    /**
     * 分页查询${tableInfo.tableAlias}
     */
    @PostMapping("/page${tableInfo.entityClassName}")
    public ResponseVo<Page<${tableInfo.entityClassName}Data>> page${tableInfo.entityClassName}(
            @RequestHeader(APICons.HEADER_TENANT_ID) String tenantId,
            @RequestBody @Valid Page${tableInfo.entityClassName}Request request) {
        return ResponseVo.createSuccessByData(${tableInfo.entityObjectName}Service.page${tableInfo.entityClassName}(tenantId, request));
    }