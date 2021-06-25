<#--controller方法-->
<@fragment name="ControllerMethod">

    /**
     * 创建${entityName}
     *
     * @return 创建的${entityName}的Id
     */
    @PostMapping("/create${beanClass}")
    @Transactional
    public ResponseVo<String> create${beanClass}(
            @RequestHeader(APICons.HEADER_TENANT_ID) String tenantId,
            @RequestAttribute(APICons.REQUEST_USER_ID) String userId,
            @RequestBody @Valid Create${beanClass}Request request) {

        return ResponseVo.createSuccessByData(${beanObject}Service.create${beanClass}(userId, tenantId, request));

    }

</@fragment>

<#--service方法-->
<@fragment name="ServiceMethod">

    /**
     * 创建${entityName}
     */
    public String create${beanClass}(String userId, String tenantId, Create${beanClass}Request request) {

        <@noSpaceLine>
        ${beanClass} ${beanObject}Insert = ${beanClass}.builder()
                <#list columnInfos as columnInfo>

                <#if columnInfo.columnName == "id">
                    <#continue>
                </#if>

                <#if columnInfo.columnName == "org_id">
                .${columnInfo.beanObject}(tenantId)<#continue>
                </#if>

                <#if columnInfo.columnName == "modifier" || columnInfo.columnName == "creator">
                .${columnInfo.beanObject}(userId)<#continue>
                </#if>

                <#if columnInfo.columnName == "gmt_create_time" || columnInfo.columnName == "gmt_modify_time">
                .${columnInfo.beanObject}(LocalDateTime.now())<#continue>
                </#if>

                .${columnInfo.beanObject}(request.get${columnInfo.beanClass}())

                </#list>
                .build();
        </@noSpaceLine>

        ${beanObject}Mapper.insert(${beanObject}Insert);

        return ${beanObject}Insert.getId();
    }

</@fragment>