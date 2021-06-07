package com.sdstc.authcenter.controller.request;

import lombok.Data;
import com.sdstc.authcenter.enums.*;
import javax.validation.constraints.NotBlank;
import com.sdstc.authcenter.controller.codec.EnumCodec;

@Data
public class Update${tableInfo.tableJavaNameCapitalized}Request {

<#list tableInfo.entityFields as field>
    <#if field.isEnumType>
    /**
     * ${field.fieldDesc}
     *
     * @see com.sdstc.authcenter.enums.${field.fieldType}#value
     */
    // @JSONField(serializeUsing = EnumCodec.class, deserializeUsing = EnumCodec.class)
    // private ${field.fieldType} ${field.fieldNameUncapitalized};
     private ${field.enumValueType} ${field.fieldNameUncapitalized};
    <#else>
    /**
     * ${field.fieldDesc}
     */
    private ${field.fieldType} ${field.fieldNameUncapitalized};
    </#if>

</#list>
}
