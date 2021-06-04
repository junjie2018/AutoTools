package com.sdstc.authcenter.controller.request;

import lombok.Data;
import com.sdstc.authcenter.enums.*;
import javax.validation.constraints.NotBlank;

@Data
public class Create${tableInfo.tableJavaNameCapitalized}Request {


<#list tableInfo.entityFields as field>
    /**
     * ${field.fieldDesc}
     */
    private ${field.fieldType} ${field.fieldNameUncapitalized};

</#list>
}
