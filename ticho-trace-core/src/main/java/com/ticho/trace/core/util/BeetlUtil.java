package com.ticho.trace.core.util;

import lombok.extern.slf4j.Slf4j;
import org.beetl.core.Configuration;
import org.beetl.core.GroupTemplate;
import org.beetl.core.ReThrowConsoleErrorHandler;
import org.beetl.core.ResourceLoader;
import org.beetl.core.Template;
import org.beetl.core.resource.StringTemplateResourceLoader;

import java.io.IOException;
import java.util.Map;

/**
 * 模版引擎渲染工具类
 *
 * @author zhajianjun
 * @date 2021-10-23 0:11
 */
@Slf4j
public class BeetlUtil {
    private BeetlUtil() {
    }

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

    /**
     * 渲染
     *
     * @param template 模板表达式
     * @param paramsMap 参数map
     * @return {@link String}
     */
    public static String render(String template, Map<String, ?> paramsMap) {
        Template t = gt.getTemplate(template);
        t.binding(paramsMap);
        return t.render();
    }



}
