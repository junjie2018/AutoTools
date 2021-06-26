package ${properties.package};

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableLogic;
import java.time.LocalDateTime;
import lombok.*;

<#if packagesToImport??>
<#list packagesToImport as packageToImport>
import ${packageToImport};
</#list>
</#if>

import java.io.Serializable;

/**
 * @author wujj
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("${tableName}")
public class ${beanClass} implements Serializable {

     <#list columnInfos as columnInfo>

     <@noSpaceLine>
     /**
      * ${columnInfo.columnComment}
      */

     <#if columnInfo.columnName == "id">
     @TableId(type = IdType.ID_WORKER_STR)
     </#if>

     <#if columnInfo.columnName == "is_delete">
     @TableLogic
     </#if>

     <#if columnInfo.enumInfo??>
     private ${columnInfo.enumInfo.enumValueType} ${columnInfo.beanObject};
     <#elseif columnInfo.internalClassInfo??>
     private JSONObject ${columnInfo.beanObject};
     <#else>
     private ${columnInfo.fieldType} ${columnInfo.beanObject};
     </#if>

     </@noSpaceLine>

    </#list>

}
