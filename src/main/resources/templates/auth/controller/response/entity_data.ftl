package com.sdstc.authcenter.controller.response;

import com.sdstc.authcenter.enums.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ${tableInfo.tableJavaNameCapitalized}Data {

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
