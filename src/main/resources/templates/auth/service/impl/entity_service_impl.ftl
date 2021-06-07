package com.sdstc.authcenter.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sdstc.authcenter.mapper.AuthOrganizeMapper;
import com.sdstc.core.utils.UserUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import com.sdstc.authcenter.service.${tableInfo.tableJavaNameCapitalized}Service;
import com.sdstc.authcenter.controller.request.${tableInfo.tableJavaNameCapitalized}IdsRequest;
import com.sdstc.authcenter.controller.request.Create${tableInfo.tableJavaNameCapitalized}Request;
import com.sdstc.authcenter.controller.request.Page${tableInfo.tableJavaNameCapitalized}Request;
import com.sdstc.authcenter.controller.request.Update${tableInfo.tableJavaNameCapitalized}Request;
import com.sdstc.authcenter.controller.response.${tableInfo.tableJavaNameCapitalized}Data;
import com.sdstc.authcenter.pojo.${tableInfo.tableJavaNameCapitalized};
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ${tableInfo.tableJavaNameCapitalized}ServiceImpl implements ${tableInfo.tableJavaNameCapitalized}Service {

    private final ${tableInfo.tableJavaNameCapitalized}Mapper ${tableInfo.tableJavaNameUncapitalized}Mapper;

    @Override
    public String create${tableInfo.tableJavaNameCapitalized}(String userId, String orgId, Create${tableInfo.tableJavaNameCapitalized}Request request) {

        ${tableInfo.tableJavaNameCapitalized} ${tableInfo.tableJavaNameUncapitalized}Insert = ${tableInfo.tableJavaNameCapitalized}.builder()
        <#list tableInfo.entityFields as field>
                .${field.fieldNameUncapitalized}(request.get${field.fieldNameCapitalized}())
        </#list>
                .build();

        ${tableInfo.tableJavaNameUncapitalized}Mapper.insert(${tableInfo.tableJavaNameUncapitalized}Insert);

        return ${tableInfo.tableJavaNameUncapitalized}Insert.getId();
    }

    @Override
    public void update${tableInfo.tableJavaNameCapitalized}(Update${tableInfo.tableJavaNameCapitalized}Request request) {

        ${tableInfo.tableJavaNameCapitalized} ${tableInfo.tableJavaNameUncapitalized}InDb = ${tableInfo.tableJavaNameUncapitalized}Mapper.selectById(request.getId());
        if (${tableInfo.tableJavaNameUncapitalized}InDb == null) {
            throw new RuntimeException("Wrong");
        }

        ${tableInfo.tableJavaNameCapitalized} ${tableInfo.tableJavaNameUncapitalized}Update = BeanUtil.copyProperties(request, ${tableInfo.tableJavaNameCapitalized}.class);

        ${tableInfo.tableJavaNameUncapitalized}Update.setId(${tableInfo.tableJavaNameUncapitalized}InDb.getId());

        ${tableInfo.tableJavaNameUncapitalized}Mapper.updateById(${tableInfo.tableJavaNameUncapitalized}Update);

    }

    @Override
    public void delete${tableInfo.tableJavaNameCapitalized}(${tableInfo.tableJavaNameCapitalized}IdsRequest request) {

        LambdaQueryWrapper<${tableInfo.tableJavaNameCapitalized}> queryWrapper = new LambdaQueryWrapper<${tableInfo.tableJavaNameCapitalized}>()
                .in(${tableInfo.tableJavaNameCapitalized}::getId, request.get${tableInfo.tableJavaNameCapitalized}Ids());

        Integer count = ${tableInfo.tableJavaNameUncapitalized}Mapper.selectCount(queryWrapper);

        if (request.get${tableInfo.tableJavaNameCapitalized}Ids().size() != count) {
            throw new RuntimeException("Wrong");
        }

        ${tableInfo.tableJavaNameUncapitalized}Mapper.deleteBatchIds(request.get${tableInfo.tableJavaNameCapitalized}Ids());
    }

    @Override
    public List<${tableInfo.tableJavaNameCapitalized}Data> query${tableInfo.tableJavaNameCapitalized}(${tableInfo.tableJavaNameCapitalized}IdsRequest request) {

        LambdaQueryWrapper<${tableInfo.tableJavaNameCapitalized}> queryWrapper = new LambdaQueryWrapper<${tableInfo.tableJavaNameCapitalized}>()
                .in(${tableInfo.tableJavaNameCapitalized}::getId, request.get${tableInfo.tableJavaNameCapitalized}Ids());


        List<${tableInfo.tableJavaNameCapitalized}Data> ${tableInfo.tableJavaNameUncapitalized}DataList = new ArrayList<>();

        for (${tableInfo.tableJavaNameCapitalized} ${tableInfo.tableJavaNameUncapitalized} : ${tableInfo.tableJavaNameUncapitalized}Mapper.selectList(queryWrapper)) {
            ${tableInfo.tableJavaNameUncapitalized}DataList.add(BeanUtil.copyProperties(${tableInfo.tableJavaNameUncapitalized}, ${tableInfo.tableJavaNameCapitalized}Data.class));
        }

        return ${tableInfo.tableJavaNameUncapitalized}DataList;
    }

    @Override
    public Page<${tableInfo.tableJavaNameCapitalized}Data> page${tableInfo.tableJavaNameCapitalized}(Page${tableInfo.tableJavaNameCapitalized}Request request) {

        LambdaQueryWrapper<${tableInfo.tableJavaNameCapitalized}> queryWrapper = new LambdaQueryWrapper<${tableInfo.tableJavaNameCapitalized}>()
                .orderByDesc(${tableInfo.tableJavaNameCapitalized}::getGmtCreateTime);

        Page<${tableInfo.tableJavaNameCapitalized}> page = new Page<>(
                request.getCurrent(),
                request.getLimit());

        ${tableInfo.tableJavaNameUncapitalized}Mapper.selectPage(page, queryWrapper);

        // 填充返回数据

        List<${tableInfo.tableJavaNameCapitalized}Data> ${tableInfo.tableJavaNameUncapitalized}List = new ArrayList<>();

        for (${tableInfo.tableJavaNameCapitalized} ${tableInfo.tableJavaNameUncapitalized} : page.getRecords()) {
            ${tableInfo.tableJavaNameUncapitalized}List.add(BeanUtil.copyProperties(${tableInfo.tableJavaNameUncapitalized}, ${tableInfo.tableJavaNameCapitalized}Data.class));
        }

        Page<${tableInfo.tableJavaNameCapitalized}Data> result = new Page<>();
        BeanUtils.copyProperties(page, result);
        result.setRecords(${tableInfo.tableJavaNameUncapitalized}List);

        return result;
    }

    @Override
    public List<${tableInfo.tableJavaNameCapitalized}Data> list${tableInfo.tableJavaNameCapitalized}() {
        LambdaQueryWrapper<${tableInfo.tableJavaNameCapitalized}> queryWrapper = new LambdaQueryWrapper<${tableInfo.tableJavaNameCapitalized}>()
                .orderByDesc(${tableInfo.tableJavaNameCapitalized}::getGmtCreateTime);


        List<${tableInfo.tableJavaNameCapitalized}> ${tableInfo.tableJavaNameUncapitalized}s = ${tableInfo.tableJavaNameUncapitalized}Mapper.selectList(queryWrapper);

        // 填充返回数据

        List<${tableInfo.tableJavaNameCapitalized}Data> ${tableInfo.tableJavaNameUncapitalized}List = new ArrayList<>();

        for (${tableInfo.tableJavaNameCapitalized} ${tableInfo.tableJavaNameUncapitalized} : ${tableInfo.tableJavaNameUncapitalized}s) {
            ${tableInfo.tableJavaNameUncapitalized}List.add(BeanUtil.copyProperties(${tableInfo.tableJavaNameUncapitalized}, ${tableInfo.tableJavaNameCapitalized}Data.class));
        }

        return ${tableInfo.tableJavaNameUncapitalized}List;
    }

}
