    /**
     * 创建角色
     *
     * @return 创建的角色的Id
     */
    @PostMapping("/create${tableInfo.entityName}")
    public ResponseVo<String> createAuthAppRole(
            @RequestHeader(APICons.HEADER_TENANT_ID) String tenantId,
            @RequestAttribute(APICons.REQUEST_USER_ID) String userId,
            @RequestBody @Valid Create${tableInfo.entityName}Request request) {
        return ResponseVo.createSuccessByData(authAppRoleService.createAuthAppRole(userId, tenantId, request));
    }