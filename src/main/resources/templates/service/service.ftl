package com.sdstc.dyf.meta.core.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sdstc.dyf.meta.common.data.${serviceClass.entityName}Data;
import com.sdstc.dyf.meta.common.request.Create${serviceClass.entityName}Request;
import com.sdstc.dyf.meta.common.request.${serviceClass.entityName}IdsRequest;
import com.sdstc.dyf.meta.common.request.Page${serviceClass.entityName}sRequest;
import com.sdstc.dyf.meta.common.request.Update${serviceClass.entityName}Request;

import java.util.List;

public interface ${serviceClass.serviceName} {

    /**
     * 创建${serviceClass.entityChineseName}
     */
    String create${serviceClass.entityName}(Create${serviceClass.entityName}Request request);

    /**
     * 更新${serviceClass.entityChineseName}
     */
    void update${serviceClass.entityName}(Update${serviceClass.entityName}Request request);

    /**
     * 删除${serviceClass.entityChineseName}
     */
    void delete${serviceClass.entityName}s(${serviceClass.entityName}IdsRequest request);

    /**
     * 查询${serviceClass.entityChineseName}
     */
    List<${serviceClass.entityName}Data> query${serviceClass.entityName}s(${serviceClass.entityName}IdsRequest request);

    /**
     * 查询所有${serviceClass.entityChineseName}（分页版）
     */
    Page<${serviceClass.entityName}Data> page${serviceClass.entityName}s(Page${serviceClass.entityName}sRequest request);

    /**
     * 查询所有${serviceClass.entityChineseName}（非分页版）
     */
    List<${serviceClass.entityName}Data> list${serviceClass.entityName}s();

}
