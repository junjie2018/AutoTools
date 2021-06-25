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

        ${beanClass} ${beanObject}Insert = ${beanClass}.builder()
                <#list columnInfos as columnInfo>
                .${columnInfo.beanObject}(request.get${columnInfo.beanClass}())
                </#list>
                .build();

        ${beanObject}Mapper.insert(${beanObject}Insert);

        return ${beanObject}Insert.getId();
    }

</@fragment>

<#-- createEntityRequest-->
<@fragment name="CreateEntityRequest">

package ${properties.package};

import lombok.Data;

<#list packagesToImport as packageToImport>
import ${packageToImport};
</#list>

@Data
public class Create${beanClass}Request {

    <#list columnInfos as columnInfo>

    <#if columnInfo.enumInfo??>
    /**
     * ${columnInfo.columnComment}
     *
     * @see ${properties.enumsPackage}.${columnInfo.enumInfo.enumClassName}#value
     */
    private ${columnInfo.enumInfo.enumValueType} ${columnInfo.fieldObjectName};
    <#else>
    /**
     * ${columnInfo.columnComment}
     */
    private ${columnInfo.fieldType} ${columnInfo.beanObject};
    </#if>

    </#list>
}

</@fragment>