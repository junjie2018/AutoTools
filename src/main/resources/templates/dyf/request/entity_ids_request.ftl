package com.sdstc.dyf.meta.common.request;

import lombok.Data;

import java.util.List;

@Data
public class ${entityClass.entityName}IdsRequest {
    /**
     * 需要删除的${entityClass.entityChineseName}Id
     */
    private List<String> ${entityClass.entityNameLower}Ids;
}
