package com.sdstc.authcenter.pojo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.*;

import com.sdstc.authcenter.enums.*;

import java.io.Serializable;

@Data
@Builder
@TableName("${tableInfo.tableNameWithPrefix}")
public class ${tableInfo.tableJavaNameCapitalized} implements Serializable {

<#list tableInfo.entityFields as field>
    <#if field.isEnumType>
    /**
     * ${field.fieldDesc}
     *
     * @see com.sdstc.authcenter.enums.${field.fieldType}#value
     */
    // @TableField(typeHandler = ${field.fieldType}TypeHandler.class)
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
