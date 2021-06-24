package ${templateConfig.filePackage};

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import ${templateConfig.otherConfigs["entity-package"]}.*;
import ${templateConfig.otherConfigs["mapper-package"]}.*;
import ${templateConfig.otherConfigs["request-package"]}.*;
import ${templateConfig.otherConfigs["response-package"]}.*;
import ${templateConfig.otherConfigs["entity-package"]}.*;
import com.sdstc.pdm.server.utils.PageUtils;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class ${tableInfo.entityClassName}Service {

    private final ${tableInfo.entityClassName}Mapper ${tableInfo.entityObjectName}Mapper;

    <#include "../../fragments/service_method_create_entity.ftl">



    <#include "../../fragments/service_method_delete_entity.ftl">



    <#include "../../fragments/service_method_page_entity.ftl">



    <#include "../../fragments/service_method_query_entity.ftl">



    <#include "../../fragments/service_method_update_entity.ftl">



    <#include "../../fragments/service_private_method_judge_entity_exist_by_id_and_tenantId.ftl">



    <#include "../../fragments/service_private_method_judge_entity_exist_by_ids_and_tenantId.ftl">

}
