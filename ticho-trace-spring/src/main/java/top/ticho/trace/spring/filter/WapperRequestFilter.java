package top.ticho.trace.spring.filter;

import org.springframework.stereotype.Component;
import top.ticho.trace.spring.wrapper.RequestWrapper;
import top.ticho.trace.spring.wrapper.ResponseWrapper;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 *
 *
 * @author zhajianjun
 * @date 2023-03-27 12:34
 */
@Component
@WebFilter(urlPatterns = "/*", filterName = "wapperRequestFilter")
public class WapperRequestFilter implements Filter {

    // @formatter:off

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        ServletRequest requestWrapper = null;
        if(servletRequest instanceof HttpServletRequest) {
            requestWrapper = new RequestWrapper((HttpServletRequest) servletRequest);
        }
        ServletResponse responseWrapper = null;
        if(servletResponse instanceof HttpServletResponse) {
            responseWrapper = new ResponseWrapper((HttpServletResponse) servletResponse);
        }
        if(requestWrapper != null) {
            servletRequest = requestWrapper;
        }
        if(responseWrapper != null) {
            servletResponse = responseWrapper;
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {

    }
}
