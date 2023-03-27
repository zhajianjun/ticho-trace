package top.ticho.trace.spring.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import top.ticho.trace.spring.filter.WapperRequestFilter;
import top.ticho.trace.spring.interceptor.WebLogInterceptor;
import top.ticho.trace.spring.prop.SpringTraceLogProperty;

/**
 *
 *
 * @author zhajianjun
 * @date 2023-03-27 12:34
 */
@Configuration
@ConditionalOnClass(WebMvcConfigurer.class)
@ConditionalOnProperty(value = "ticho.trace.enable", havingValue = "true", matchIfMissing = true)
public class SpringTraceConfig implements WebMvcConfigurer {

    @Bean

    public SpringTraceLogProperty springTraceLogProperty(){
        return new SpringTraceLogProperty();
    }

    @Autowired
    private SpringTraceLogProperty springTraceLogProperty;

    @Bean
    public WapperRequestFilter wapperRequestFilter() {
        return new WapperRequestFilter();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new WebLogInterceptor(springTraceLogProperty)).order(Ordered.HIGHEST_PRECEDENCE + 10);
    }

}
