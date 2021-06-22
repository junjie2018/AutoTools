    /**
     * 创建${tableInfo.entityAlias}
     *
     * @return 创建的${tableInfo.entityAlias}的Id
     */
    @PostMapping("/create${tableInfo.entityClassName}")
    public ResponseVo<String> create${tableInfo.entityClassName}(
            @RequestAttribute(APICons.REQUEST_USER_ID) String userId,
            @RequestAttribute(APICons.REQUEST_COMPANY_ID) String orgId,
            @RequestBody @Valid Create${tableInfo.entityClassName}Request request) {

        String ${tableInfo.entityObjectName}Id = ${tableInfo.entityClassName}Service.create${tableInfo.entityClassName}(userId, orgId, request)

        return ResponseVo.createSuccessByData();
    }