package ${properties.package};

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import com.sdstc.core.constants.APICons;
import com.sdstc.core.vo.ResponseVo;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

<@packets/>

/**
 * ${entityName}管理
 */
@RestController
@RequiredArgsConstructor
public class ${beanClass}Controller {

    private final ${beanClass}Service ${beanObject}Service;

    <@fragments/>

}
