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
public class ${tableInfo.entityClassName}TestService {

    private final ${tableInfo.entityClassName}Mapper ${tableInfo.entityObjectName}Mapper;

    <@include tpl="fragments/create_entity.ftl" fragment="service"/>

}
