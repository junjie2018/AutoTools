package ${templateConfig.filePackage};


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import ${templateConfig.otherConfigs["service-package"]}.${tableInfo.entityClassName}Service;
import ${templateConfig.otherConfigs["request-package"]}.*;
import ${templateConfig.otherConfigs["response-package"]}.*;
import com.sdstc.core.constants.APICons;
import com.sdstc.core.vo.ResponseVo;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * ${tableInfo.tableAlias}管理
 */
@RestController
@RequiredArgsConstructor
public class ${tableInfo.entityClassName}Controller {

    private final ${tableInfo.entityClassName}Service ${tableInfo.entityObjectName}Service;

    <#include "../../fragments/controller_method_create_entity.ftl">



    <#include "../../fragments/controller_method_update_entity.ftl">



    <#include "../../fragments/controller_method_page_entity.ftl">



    <#include "../../fragments/controller_method_query_entity.ftl">



    <#include "../../fragments/controller_method_delete_entity.ftl">
}
