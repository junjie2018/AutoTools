package ${templateConfig.filePackage};

import lombok.Data;
import javax.validation.constraints.NotNull;

@Data
public class Page${tableInfo.entityClassName}Request {
    /**
     * 当前页数
     */
    @NotNull
    private Integer current;

    /**
     * 每页记录数量
     */
    @NotNull
    private Integer limit;

    @Data
    public static class Condition {

    }
}
