package com.sdstc.authcenter.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sdstc.authcenter.controller.request.${tableInfo.tableJavaNameCapitalized}IdsRequest;
import com.sdstc.authcenter.controller.request.Create${tableInfo.tableJavaNameCapitalized}Request;
import com.sdstc.authcenter.controller.request.Page${tableInfo.tableJavaNameCapitalized}Request;
import com.sdstc.authcenter.controller.request.Update${tableInfo.tableJavaNameCapitalized}Request;
import com.sdstc.authcenter.controller.response.${tableInfo.tableJavaNameCapitalized}Data;
import com.sdstc.authcenter.service.${tableInfo.tableJavaNameCapitalized}Service;
import com.sdstc.core.constants.APICons;
import com.sdstc.core.vo.ResponseVo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * ${tableInfo.entityName}管理
 */
@RestController
@RequiredArgsConstructor
public class ${tableInfo.tableJavaNameCapitalized}Controller {

    private final ${tableInfo.tableJavaNameCapitalized}Service ${tableInfo.tableJavaNameUncapitalized}Service;

    /**
     * 创建${tableInfo.entityName}
     *
     * @return 创建的${tableInfo.entityName}的Id
     */
    @PostMapping("/create${tableInfo.tableJavaNameCapitalized}")
    public ResponseVo<String> create${tableInfo.tableJavaNameCapitalized}(
            @RequestAttribute(APICons.REQUEST_USER_ID) String userId,
            @RequestAttribute(APICons.REQUEST_COMPANY_ID) String orgId,
            @RequestBody @Valid Create${tableInfo.tableJavaNameCapitalized}Request request) {
        return ResponseVo.createSuccessByData(${tableInfo.tableJavaNameUncapitalized}Service.create${tableInfo.tableJavaNameCapitalized}(userId, orgId, request));
    }

    /**
     * 更新${tableInfo.entityName}
     */
    @PostMapping("/update${tableInfo.tableJavaNameCapitalized}")
    public ResponseVo update${tableInfo.tableJavaNameCapitalized}(@RequestBody @Valid Update${tableInfo.tableJavaNameCapitalized}Request request) {

        ${tableInfo.tableJavaNameUncapitalized}Service.update${tableInfo.tableJavaNameCapitalized}(request);

        return ResponseVo.createSuccess();
    }

    /**
     * 删除${tableInfo.entityName}
     */
    @PostMapping("/delete${tableInfo.tableJavaNameCapitalized}s")
    public ResponseVo<String> delete${tableInfo.tableJavaNameCapitalized}s(@RequestBody @Valid ${tableInfo.tableJavaNameCapitalized}IdsRequest request) {

        ${tableInfo.tableJavaNameUncapitalized}Service.delete${tableInfo.tableJavaNameCapitalized}(request);

        return ResponseVo.createSuccess();
    }

    /**
     * 查询${tableInfo.entityName}
     */
    @PostMapping("/query${tableInfo.tableJavaNameCapitalized}s")
    public ResponseVo<List<${tableInfo.tableJavaNameCapitalized}Data>> query${tableInfo.tableJavaNameCapitalized}s(@RequestBody @Valid ${tableInfo.tableJavaNameCapitalized}IdsRequest request) {
        return ResponseVo.createSuccessByData(${tableInfo.tableJavaNameUncapitalized}Service.query${tableInfo.tableJavaNameCapitalized}(request));
    }

    /**
     * 查询所有${tableInfo.entityName}（分页版）
     */
    @PostMapping("/page${tableInfo.tableJavaNameCapitalized}s")
    public ResponseVo<Page<${tableInfo.tableJavaNameCapitalized}Data>> page${tableInfo.tableJavaNameCapitalized}s(@RequestBody @Valid Page${tableInfo.tableJavaNameCapitalized}Request request) {
        return ResponseVo.createSuccessByData(${tableInfo.tableJavaNameUncapitalized}Service.page${tableInfo.tableJavaNameCapitalized}(request));
    }

    /**
     * 查询所有${tableInfo.entityName}（非分页版）
     */
    @PostMapping("/list${tableInfo.tableJavaNameCapitalized}s")
    public ResponseVo<List<${tableInfo.tableJavaNameCapitalized}Data>> list${tableInfo.tableJavaNameCapitalized}s() {
        return ResponseVo.createSuccessByData(${tableInfo.tableJavaNameUncapitalized}Service.list${tableInfo.tableJavaNameCapitalized}());
    }

}
