package com.sdstc.authcenter.controller.request;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class ${tableInfo.tableJavaNameCapitalized}IdsRequest {
    /**
     * 需要删除的领域Id
     */
    @NotNull
    private List<String> ${tableInfo.tableJavaNameUncapitalized}Ids;
}
