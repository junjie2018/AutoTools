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
public class ${tableInfo.entityClassName}TestController {

    private final ${tableInfo.entityClassName}Service ${tableInfo.entityObjectName}Service;

    <@include tpl="fragments/create_entity.ftl" fragment="controller"/>

}
