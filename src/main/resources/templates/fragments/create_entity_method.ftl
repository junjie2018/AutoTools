<#--controller方法-->
<@fragment name="controller">

    /**
     * 创建${tableAlias}
     *
     * @return 创建的${tableAlias}的Id
     */
    @PostMapping("/create${entityClassName}")
    @Transactional
    public ResponseVo<String> create${entityClassName}(
            @RequestHeader(APICons.HEADER_TENANT_ID) String tenantId,
            @RequestAttribute(APICons.REQUEST_USER_ID) String userId,
            @RequestBody @Valid Create${entityClassName}Request request) {

        return ResponseVo.createSuccessByData(${entityObjectName}Service.create${entityClassName}(userId, tenantId, request));

    }

</@fragment>

<#--service方法-->
<@fragment name="service">

    /**
     * 创建${tableAlias}
     */
    public String create${entityClassName}(String userId, String tenantId, Create${entityClassName}Request request) {

        ${entityClassName} ${entityObjectName}Insert = ${entityClassName}.builder()
                <#list columnInfos as columnInfo>
                .${columnInfo.fieldObjectName}(request.get${columnInfo.fieldClassName}())
                </#list>
                .build();

        ${entityObjectName}Mapper.insert(${entityObjectName}Insert);

        return ${entityObjectName}Insert.getId();
    }

</@fragment>