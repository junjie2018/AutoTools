    /**
     * 创建${tableInfo.tableAlias}
     *
     * @return 创建的${tableInfo.tableAlias}的Id
     */
    @PostMapping("/create${tableInfo.entityClassName}")
    @Transactional
    public ResponseVo<String> create${tableInfo.entityClassName}(
            @RequestHeader(APICons.HEADER_TENANT_ID) String tenantId,
            @RequestAttribute(APICons.REQUEST_USER_ID) String userId,
            @RequestBody @Valid Create${tableInfo.entityClassName}Request request) {
        return ResponseVo.createSuccessByData(${tableInfo.entityObjectName}Service.create${tableInfo.entityClassName}(userId, tenantId, request));
    }