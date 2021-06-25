package ${templateConfig.filePackage};

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import ${templateConfig.otherConfigs["entity-package"]}.${tableInfo.entityClassName};
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ${tableInfo.entityClassName}Mapper extends BaseMapper<${tableInfo.entityClassName}> {

}