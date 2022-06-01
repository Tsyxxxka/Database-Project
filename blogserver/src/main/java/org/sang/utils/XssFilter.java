package org.sang.utils;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;


import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


//对请求参数进行过滤
@Slf4j
@WebFilter(filterName="xssFilter", urlPatterns="/*")
@SuppressWarnings("unchecked")
public class XssFilter implements Filter {

    // endsWith(exclude),放行静态资源
    public List<String> excludes = Arrays.asList(
            "/login", "/logout", ".html", ".js", ".gif", ".jpg", ".png", ".css",
            ".ico", ".woff2", ".woff", ".tt", ".ttf");

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        if (handleExcludeURL(req)) {
            filterChain.doFilter(request, response);
            return;
        }

        XssHttpServletRequestWrapper xssRequest = new XssHttpServletRequestWrapper((HttpServletRequest) request);
        filterChain.doFilter(xssRequest, response);
    }

    private boolean handleExcludeURL(HttpServletRequest request) {
        if (CollectionUtils.isEmpty(excludes)) {
            return false;
        }

        String path = request.getServletPath();
        if(path == null){
            return false;
        }

        return excludes.stream()
                .anyMatch(method->path.endsWith(method)
                        || path.matches(method));
    }

    @Override
    public void init(FilterConfig filterConfig) {
        String temp = filterConfig.getInitParameter("excludes");
        if (temp != null) {
            String[] url = temp.split(",");
            excludes.addAll(Arrays.asList(url));
        }
    }

    public class XssHttpServletRequestWrapper extends HttpServletRequestWrapper {

        private Map<String, String[]> parameterMap;

        public XssHttpServletRequestWrapper(HttpServletRequest request) {
            super(request);
            this.parameterMap = request.getParameterMap();
        }

        @Override
        public String getParameter(String name) {
            String[] results = parameterMap.get(name);
            if (results == null || results.length <= 0)
                return null;
            else {
                String value = results[0];
                if (value != null) {
                    value = StringUtil.cleanXSS(value);
                }
                return value;
            }
        }

        @Override
        public String[] getParameterValues(String name) {
            String[] values = parameterMap.get(name);
            if (values == null) {
                return null;
            }
            int length = values.length;
            String[] needFilterValues = new String[length];
            for (int i = 0; i < length; i++) {
                //filter all character
                needFilterValues[i] = StringUtil.cleanXSS(values[i]);
                if (!StringUtils.equals(needFilterValues[i], values[i])) {
                    log.warn("The request parameter contains xss character!");
                    log.info("The xss character before filter:" + values[i] + "\r\n" + ", after filter:" + needFilterValues[i]);
                }
            }
            return needFilterValues;
        }

        @Override
        public ServletInputStream getInputStream() throws IOException {
            if (super.getContentType().contains("application/json"))
            {
                String string = getRequestBody(super.getInputStream());
                Object parameterObj = JSON.parse(string);
                if (parameterObj instanceof JSONObject)
                {
                    Map<String, Object> map = JSON.parseObject(string, Map.class);
                    Map<String, Object> resultMap = new HashMap<>(map.size());
                    for (String key : map.keySet()) {
                        Object val = map.get(key);
                        if (map.get(key) instanceof String) {
                            resultMap.put(key, StringUtil.cleanXSS(val.toString()));
                        } else {
                            resultMap.put(key, val);
                        }
                    }
                    string = JSON.toJSONString(resultMap);
                }else {
                    string = StringUtil.cleanXSS(string);
                }

                final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(string.getBytes(StandardCharsets.UTF_8));
                return new ServletInputStream() {
                    @Override
                    public int read() throws IOException {
                        return byteArrayInputStream.read();
                    }

                    @Override
                    public boolean isFinished() {
                        return false;
                    }

                    @Override
                    public boolean isReady() {
                        return false;
                    }

                    @Override
                    public void setReadListener(ReadListener listener) {
                    }
                };
            }else {
                return super.getInputStream();
            }
        }

        private String getRequestBody(InputStream stream) {
            String line = null;
            StringBuilder body = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));

            try {
                while ((line = reader.readLine()) != null) {
                    body.append(line);
                }
            } catch (IOException e) {
                log.error("read request body fail");
            }
            return body.toString();
        }
    }
}
