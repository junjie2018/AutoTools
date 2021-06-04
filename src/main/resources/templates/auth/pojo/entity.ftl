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
    /**
     * ${field.fieldDesc}
     */
    <#if field.isPrimaryKey>
    @TableId
    </#if>
    private ${field.fieldType} ${field.fieldNameUncapitalized};

</#list>
}
