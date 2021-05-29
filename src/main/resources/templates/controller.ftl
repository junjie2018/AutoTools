package com.sdstc.dyf.meta.core.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sdstc.dyf.meta.common.data.${entityClass.entityName}Data;
import com.sdstc.dyf.meta.common.request.Create${entityClass.entityName}Request;
import com.sdstc.dyf.meta.common.request.${entityClass.entityName}IdsRequest;
import com.sdstc.dyf.meta.common.request.Page${entityClass.entityName}sRequest;
import com.sdstc.dyf.meta.common.request.Update${entityClass.entityName}Request;
import com.sdstc.dyf.meta.core.service.${entityClass.entityName}Service;
import com.sdstc.scdp.common.vo.ResultVo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class ${entityClass.entityName}Controller {

    private final ${entityClass.entityName}Service ${entityClass.entityNameLower}Service;

    /**
     * 创建领域
     *
     * @return 创建的领域的Id
     */
    @PostMapping("/create${entityClass.entityName}")
    public ResultVo<String> create${entityClass.entityName}(@RequestBody @Valid Create${entityClass.entityName}Request request) {
        return ResultVo.success(${entityClass.entityNameLower}Service.create${entityClass.entityName}(request));
    }

    /**
     * 更新领域
     */
    @PostMapping("/update${entityClass.entityName}")
    public ResultVo update${entityClass.entityName}(@RequestBody @Valid Update${entityClass.entityName}Request request) {

        ${entityClass.entityNameLower}Service.update${entityClass.entityName}(request);

        return ResultVo.success();
    }

    /**
     * 删除领域
     */
    @PostMapping("/delete${entityClass.entityName}s")
    public ResultVo<String> delete${entityClass.entityName}s(@RequestBody @Valid ${entityClass.entityName}IdsRequest request) {

        ${entityClass.entityNameLower}Service.delete${entityClass.entityName}s(request);

        return ResultVo.success();
    }

    /**
     * 查询领域
     */
    @PostMapping("/query${entityClass.entityName}s")
    public ResultVo<List<${entityClass.entityName}Data>> query${entityClass.entityName}s(@RequestBody @Valid ${entityClass.entityName}IdsRequest request) {

        return ResultVo.success(${entityClass.entityNameLower}Service.query${entityClass.entityName}s(request));

    }

    /**
     * 查询所有领域（分页版）
     */
    @PostMapping("/page${entityClass.entityName}s")
    public ResultVo<Page<${entityClass.entityName}Data>> page${entityClass.entityName}s(Page${entityClass.entityName}sRequest request) {
        return ResultVo.success(${entityClass.entityNameLower}Service.page${entityClass.entityName}s(request));
    }

    /**
     * 查询所有领域（非分页版）
     */
    @PostMapping("/list${entityClass.entityName}s")
    public ResultVo<List<${entityClass.entityName}Data>> list${entityClass.entityName}s() {
        return ResultVo.success(${entityClass.entityNameLower}Service.list${entityClass.entityName}s());
    }

}
