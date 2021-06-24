    /**
     * 根据id数组，批量删除${tableInfo.tableAlias}
     */
    @Transactional
    @PostMapping("/delete${tableInfo.entityClassName}")
    public ResponseVo delete${tableInfo.entityClassName}(
            @RequestHeader(APICons.HEADER_TENANT_ID) String tenantId,
            @RequestBody @Valid ${tableInfo.entityClassName}IdsRequest request) {

        ${tableInfo.entityObjectName}Service.delete${tableInfo.entityClassName}(tenantId, request);

        return ResponseVo.createSuccess();
    }