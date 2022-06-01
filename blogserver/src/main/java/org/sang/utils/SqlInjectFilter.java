package org.sang.utils;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;

import org.sang.config.SqlInjectHttpServletRequestWrapper;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

/**
 * SQL注入过滤器
 *
 * @author zzg
 *
 */
@Component
@WebFilter(filterName = "SqlInjectFilter", urlPatterns = "/*")
public class SqlInjectFilter implements Filter {

    /**
     * 过滤器配置对象
     */
    FilterConfig filterConfig = null;

    /**
     * 是否启用(默认启用)
     */
    private boolean enable = true;


    /**
     * 忽略的URL
     */
    @Value("${security.sql.excludes}")
    private String excludes;


    /**
     * 初始化
     */
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.filterConfig = filterConfig;
    }

    /**
     * 拦截
     */
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;

        // 不启用或者已忽略的URL不拦截
        if (!enable || isExcludeUrl(request.getServletPath())) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }
        SqlInjectHttpServletRequestWrapper sqlInjectHttpServletRequestWrapper = new SqlInjectHttpServletRequestWrapper(
                request);
        filterChain.doFilter(sqlInjectHttpServletRequestWrapper, servletResponse);
    }

    /**
     * 销毁
     */
    @Override
    public void destroy() {
        this.filterConfig = null;
    }

    /**
     * 判断是否为忽略的URL
     *
     * @param urlPath URL路径
     * @return true-忽略，false-过滤
     */
    private boolean isExcludeUrl(String url) {
        if (excludes == null || excludes.isEmpty()) {
            return false;
        }
        List<String> urls = Arrays.asList(excludes.split(","));
        return urls .stream().map(pattern -> Pattern.compile("^" + pattern)).map(p -> p.matcher(url))
                .anyMatch(Matcher::find);
    }
}