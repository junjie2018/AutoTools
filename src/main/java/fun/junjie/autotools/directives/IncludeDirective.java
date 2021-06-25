package fun.junjie.autotools.directives;

import freemarker.cache.StringTemplateLoader;
import freemarker.core.Environment;
import freemarker.template.*;
import fun.junjie.autotools.utils.TemplateUtils;
import fun.junjie.autotools.utils.TemplateUtilsMax;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IncludeDirective implements TemplateDirectiveModel {

    private static final String TPL = "tpl";
    private static final String FRAGMENT = "fragment";

    private static Pattern FRAGMENT_PATTERN = Pattern.compile(
            "<@fragment name=\"([A-Za-z0-9-_]+)\">(.*?)</@fragment>",
            Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

    private static Map<Object, Map<String, String>> fragmentContents = new HashMap<>();

//    private Template getFragmentTemplate(Object fragmentTemplateSource, String fragmentName) throws IOException {
//
////        String fragmentContent = fragmentContents.get(fragmentTemplateSource).get(fragmentName);
//
////        if (StringUtils.isBlank(fragmentContent)) {
////            throw new RuntimeException("No Fragment Find");
////        }
//
//        return new Template(String.valueOf(new Date()),
//                new StringReader(fragmentContent),
//                new Configuration(Configuration.VERSION_2_3_23));
//
//    }

//    private void loadFragmentContents(Object fragmentTemplateSource, String fragmentContentsStr) {
//        Map<String, String> fragmentNameToFragmentInfo = new HashMap<>();
//
//        Matcher fragmentMatcher = FRAGMENT_PATTERN.matcher(fragmentContentsStr);
//        while (fragmentMatcher.find()) {
//            fragmentNameToFragmentInfo.put(fragmentMatcher.group(1), fragmentMatcher.group(2));
//        }
//
//        fragmentContents.put(fragmentTemplateSource, fragmentNameToFragmentInfo);
//    }

    @Override
    public void execute(Environment env, Map params, TemplateModel[] loopVars, TemplateDirectiveBody body) throws TemplateException, IOException {

        String fragmentTemplate = params.get(TPL) == null ? "" : params.get(TPL).toString();
        String fragmentName = params.get(FRAGMENT) == null ? "" : params.get(FRAGMENT).toString();

        String fragmentTplName = TemplateUtilsMax.getFragmentTplName(fragmentTemplate, fragmentName);

        Template template = env.getConfiguration().getTemplate(fragmentTplName);

        if (template == null) {
            throw new RuntimeException("no tpl param or fragment param");
        }

        env.include(template);

//
//        if (StringUtils.isBlank(fragmentTemplate) || StringUtils.isBlank(fragmentName)) {
//            throw new RuntimeException("no tpl param or fragment param");
//        }
//
//        Object fragmentTemplateSource = env
//                .getConfiguration()
//                .getTemplateLoader()
//                .findTemplateSource(fragmentTemplate);
//
//        if (fragmentTemplateSource == null) {
//            throw new RuntimeException("no this fragment tpl");
//        }
//
//        if (!fragmentContents.containsKey(fragmentTemplateSource)
//                || fragmentContents.get(fragmentTemplateSource).containsKey(fragmentName)) {
//
//            Reader fragmentReader = env
//                    .getConfiguration()
//                    .getTemplateLoader()
//                    .getReader(fragmentTemplateSource, "utf-8");
//
//            String fragmentContentsStr = IOUtils.toString(fragmentReader);
//
//            loadFragmentContents(fragmentTemplateSource, fragmentContentsStr);
//        }
//

    }
}
