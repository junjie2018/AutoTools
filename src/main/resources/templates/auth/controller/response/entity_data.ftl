package com.sdstc.authcenter.controller.response;

import com.sdstc.authcenter.enums.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ${tableInfo.tableJavaNameCapitalized}Data {

<#list tableInfo.entityFields as field>
    /**
     * ${field.fieldDesc}
     */
    private ${field.fieldType} ${field.fieldNameUncapitalized};

</#list>
}
