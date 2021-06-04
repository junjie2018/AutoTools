package com.sdstc.authcenter.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import com.sdstc.authcenter.controller.request.${tableInfo.tableJavaNameCapitalized}IdsRequest;
import com.sdstc.authcenter.controller.request.Create${tableInfo.tableJavaNameCapitalized}Request;
import com.sdstc.authcenter.controller.request.Page${tableInfo.tableJavaNameCapitalized}Request;
import com.sdstc.authcenter.controller.request.Update${tableInfo.tableJavaNameCapitalized}Request;
import com.sdstc.authcenter.response.auth.${tableInfo.tableJavaNameCapitalized}Data;


import java.util.List;

public interface ${tableInfo.tableJavaNameCapitalized}Service {

    /**
     * 创建${tableInfo.entityName}
     */
    String create${tableInfo.tableJavaNameCapitalized}(String userId, String orgId, Create${tableInfo.tableJavaNameCapitalized}Request request);

    /**
     * 更新${tableInfo.entityName}
     */
    void update${tableInfo.tableJavaNameCapitalized}(Update${tableInfo.tableJavaNameCapitalized}Request request);

    /**
     * 删除${tableInfo.entityName}
     */
    void delete${tableInfo.tableJavaNameCapitalized}(${tableInfo.tableJavaNameCapitalized}IdsRequest request);

    /**
     * 查询${tableInfo.entityName}
     */
    List<${tableInfo.tableJavaNameCapitalized}Data> query${tableInfo.tableJavaNameCapitalized}(${tableInfo.tableJavaNameCapitalized}IdsRequest request);

    /**
     * 查询所有${tableInfo.entityName}（分页版）
     */
    Page<${tableInfo.tableJavaNameCapitalized}Data> page${tableInfo.tableJavaNameCapitalized}(Page${tableInfo.tableJavaNameCapitalized}Request request);

    /**
     * 查询所有${tableInfo.entityName}（非分页版）
     */
    List<${tableInfo.tableJavaNameCapitalized}Data> list${tableInfo.tableJavaNameCapitalized}();

}
