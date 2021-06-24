    /**
     * 更新${tableInfo.tableAlias}
     */
    @PostMapping("/update${tableInfo.entityClassName}")
    @Transactional
    public ResponseVo update${tableInfo.entityClassName}(
            @RequestHeader(APICons.HEADER_TENANT_ID) String tenantId,
            @RequestBody @Valid Update${tableInfo.entityClassName}Request request) {

        ${tableInfo.entityObjectName}Service.update${tableInfo.entityClassName}(tenantId, request);

        return ResponseVo.createSuccess();
    }