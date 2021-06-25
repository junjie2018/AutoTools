package ${templateConfig.filePackage};

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.*;

<#list templateConfig.packagesToImport as package>
import ${package};
</#list>

import java.io.Serializable;

/**
 * @author wujj
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("${tableInfo.tableName}")
public class ${tableInfo.entityClassName} implements Serializable {

<#include "../../fragments/pojo_entity.ftl">

}
