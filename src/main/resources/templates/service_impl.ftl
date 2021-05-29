package com.sdstc.dyf.meta.core.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sdstc.dyf.meta.common.data.${entityClass.entityName}Data;
import com.sdstc.dyf.meta.common.request.Create${entityClass.entityName}Request;
import com.sdstc.dyf.meta.common.request.${entityClass.entityName}IdsRequest;
import com.sdstc.dyf.meta.common.request.Page${entityClass.entityName}sRequest;
import com.sdstc.dyf.meta.common.request.Update${entityClass.entityName}Request;
import com.sdstc.dyf.meta.core.dao.${entityClass.entityName}Dao;
import com.sdstc.dyf.meta.core.po.${entityClass.entityName}Po;
import com.sdstc.dyf.meta.core.service.${entityClass.entityName}Service;
import com.sdstc.scdp.common.util.UserUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ${entityClass.entityName}ServiceImpl implements ${entityClass.entityName}Service {

    private final ${entityClass.entityName}Dao ${entityClass.entityNameLower}Dao;

    @Override
    public String create${entityClass.entityName}(Create${entityClass.entityName}Request request) {

        ${entityClass.entityName}Po ${entityClass.entityNameLower}PoInsert = ${entityClass.entityName}Po.builder()
        <#list entityClass.fields as field>
                .${field.fieldName}(request.get${field.fieldNameUpper}())
        </#list>
                .build();

        ${entityClass.entityNameLower}Dao.insert(${entityClass.entityNameLower}PoInsert);

        return ${entityClass.entityNameLower}PoInsert.getId();
    }

    @Override
    public void update${entityClass.entityName}(Update${entityClass.entityName}Request request) {

        ${entityClass.entityName}Po ${entityClass.entityNameLower}PoInDb = ${entityClass.entityNameLower}Dao.selectById(request.getId());
        if (${entityClass.entityNameLower}PoInDb == null) {
            throw new RuntimeException("Wrong");
        }

        ${entityClass.entityName}Po ${entityClass.entityNameLower}PoUpdate = BeanUtil.copyProperties(request, ${entityClass.entityName}Po.class);

        ${entityClass.entityNameLower}PoUpdate.setId(${entityClass.entityNameLower}PoInDb.getId());

        ${entityClass.entityNameLower}Dao.updateById(${entityClass.entityNameLower}PoInDb);

    }

    @Override
    public void delete${entityClass.entityName}s(${entityClass.entityName}IdsRequest request) {

        LambdaQueryWrapper<${entityClass.entityName}Po> queryWrapper = new LambdaQueryWrapper<${entityClass.entityName}Po>()
                .in(${entityClass.entityName}Po::getId, request.get${entityClass.entityName}Ids());

        Integer count = ${entityClass.entityNameLower}Dao.selectCount(queryWrapper);

        if (request.get${entityClass.entityName}Ids().size() != count) {
            throw new RuntimeException("Wrong");
        }

        ${entityClass.entityNameLower}Dao.deleteBatchIds(request.get${entityClass.entityName}Ids());
    }

    @Override
    public List<${entityClass.entityName}Data> query${entityClass.entityName}s(${entityClass.entityName}IdsRequest request) {

        LambdaQueryWrapper<${entityClass.entityName}Po> queryWrapper = new LambdaQueryWrapper<${entityClass.entityName}Po>()
                .in(${entityClass.entityName}Po::getId, request.get${entityClass.entityName}Ids());


        List<${entityClass.entityName}Data> ${entityClass.entityNameLower}DataList = new ArrayList<>();

        for (${entityClass.entityName}Po ${entityClass.entityNameLower}Po : ${entityClass.entityNameLower}Dao.selectList(queryWrapper)) {
            ${entityClass.entityNameLower}DataList.add(BeanUtil.copyProperties(${entityClass.entityNameLower}Po, ${entityClass.entityName}Data.class));
        }

        return ${entityClass.entityNameLower}DataList;
    }

    @Override
    public Page<${entityClass.entityName}Data> page${entityClass.entityName}s(Page${entityClass.entityName}sRequest request) {

        LambdaQueryWrapper<${entityClass.entityName}Po> queryWrapper = new LambdaQueryWrapper<${entityClass.entityName}Po>()
                .orderByDesc(${entityClass.entityName}Po::getGmtCreateTime);

        Page<${entityClass.entityName}Po> page = new Page<>(
                request.getCurrent(),
                request.getLimit());

        ${entityClass.entityNameLower}Dao.selectPage(page, queryWrapper);

        // 填充返回数据

        List<${entityClass.entityName}Data> ${entityClass.entityNameLower}List = new ArrayList<>();

        for (${entityClass.entityName}Po ${entityClass.entityNameLower}Po : page.getRecords()) {
            ${entityClass.entityNameLower}List.add(BeanUtil.copyProperties(${entityClass.entityNameLower}Po, ${entityClass.entityName}Data.class));
        }

        Page<${entityClass.entityName}Data> result = new Page<>();
        BeanUtils.copyProperties(page, result);
        result.setRecords(${entityClass.entityNameLower}List);

        return result;
    }

    @Override
    public List<${entityClass.entityName}Data> list${entityClass.entityName}s() {
        LambdaQueryWrapper<${entityClass.entityName}Po> queryWrapper = new LambdaQueryWrapper<${entityClass.entityName}Po>()
                .orderByDesc(${entityClass.entityName}Po::getGmtCreateTime);


        List<${entityClass.entityName}Po> ${entityClass.entityNameLower}Pos = ${entityClass.entityNameLower}Dao.selectList(queryWrapper);

        // 填充返回数据

        List<${entityClass.entityName}Data> ${entityClass.entityNameLower}List = new ArrayList<>();

        for (${entityClass.entityName}Po ${entityClass.entityNameLower}Po : ${entityClass.entityNameLower}Pos) {
            ${entityClass.entityNameLower}List.add(BeanUtil.copyProperties(${entityClass.entityNameLower}Po, ${entityClass.entityName}Data.class));
        }

        return ${entityClass.entityNameLower}List;
    }

}
