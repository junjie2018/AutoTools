package com.sdstc.dyf.meta.common.request;

import lombok.Data;

@Data
public class Page${entityClass.entityName}sRequest {
    /**
     * 当前页数
     */
    private Integer current;

    /**
     * 每页记录数量
     */
    private Integer limit;
}
