package ${templateConfig.filePackage};

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class ${tableInfo.entityClassName}IdsRequest {
    /**
     * 需要删除的${tableInfo.tableAlias}Id
     */
    @NotNull
    private List<String> ids;
}
