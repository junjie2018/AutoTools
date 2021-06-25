package ${properties.package};

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import ${properties.entityPackage}.*;
import com.sdstc.pdm.server.utils.PageUtils;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class ${beanClass}TestService {

private final ${beanClass}Mapper ${beanObject}Mapper;

    <@include tpl="CreateEntity.ftl" fragment="ServiceMethod"/>

}
