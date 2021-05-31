package com.sdstc.dyf.meta.common.request;

<#list entityClass.packagesToImport as packageName>
import ${packageName};
</#list>

@Data
public class Create${entityClass.entityName}Request {

<#list entityClass.fields as field>
    /**
     * ${field.fieldDesc}
     */
    private ${field.fieldType} ${field.fieldName};

</#list>


<#list entityClass.internalClasses as internalClass>

    @Data
    public static class ${internalClass.internalClassName} {
    <#list internalClass.fields as field>
        /**
         * ${field.fieldDesc}
         */
        private ${field.fieldType} ${field.fieldName};
    </#list>
    }

</#list>

}
