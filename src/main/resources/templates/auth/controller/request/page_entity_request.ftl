package com.sdstc.authcenter.controller.request;

import lombok.Data;

@Data
public class Page${tableInfo.tableJavaNameCapitalized}Request {
    /**
     * 当前页数
     */
    private Integer current;

    /**
     * 每页记录数量
     */
    private Integer limit;
}
