package top.ticho.trace.core.util;

import lombok.extern.slf4j.Slf4j;
import org.beetl.core.BeetlKit;
import org.beetl.core.Configuration;
import org.beetl.core.GroupTemplate;
import org.beetl.core.ReThrowConsoleErrorHandler;
import org.beetl.core.ResourceLoader;
import org.beetl.core.Template;
import org.beetl.core.config.BeetlConfig;
import org.beetl.core.resource.StringTemplateResourceLoader;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author zhajianjun
 * @date 2021-10-23 0:11
 */
@Slf4j
public class BeetlUtil {
    private BeetlUtil() {

    }

    public static final ResourceLoader<String> STRING_TEMPLATE_INSTANCE = new StringTemplateResourceLoader();
    public static GroupTemplate gt;

    static {
        gt = null;
        StringTemplateResourceLoader resourceLoader = new StringTemplateResourceLoader();
        Configuration cfg;
        try {
            cfg = Configuration.defaultConfiguration();
        } catch (IOException var3) {
            throw new RuntimeException(var3);
        }

        gt = new GroupTemplate(resourceLoader, cfg);
        gt.setErrorHandler(new ReThrowConsoleErrorHandler());
    }

    public static String render(String template, Map<String, ?> paras) {
        Template t = gt.getTemplate(template);
        t.binding(paras);
        return t.render();
    }



}
