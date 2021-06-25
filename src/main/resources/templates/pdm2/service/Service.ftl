package ${properties.package};

import cn.hutool.core.bean.BeanUtil;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;

<#if packagesToImport??>
<#list packagesToImport as packageToImport>
import ${packageToImport};
</#list>
</#if>

@Service
@AllArgsConstructor
public class ${beanClass}Service {

    private final ${beanClass}Mapper ${beanObject}Mapper;

    <@include tpl="CreateEntity.ftl" fragment="ServiceMethod"/>

}
