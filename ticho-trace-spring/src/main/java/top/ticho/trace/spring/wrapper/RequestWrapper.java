package top.ticho.trace.spring.wrapper;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 *
 *
 * @author zhajianjun
 * @date 2023-03-27 12:34
 */
@Slf4j
public class RequestWrapper extends HttpServletRequestWrapper {

    private final String body;

    private final Map<String, String[]> parameterMap;

    /**
     * Wrapper的构造方法，主要是将body里的内容取出来，然后存储到对象中的body变量中，方便
     * 后续复用
     * <p>
     *  io.undertow.servlet.spec.HttpServletRequestImpl#parseFormData()
     *  查看上述的方法，需要执行下request.getParameterMap()，才能正常解析form-data类型的参数数据
     * </p>
     *
     * @param request The request to wrap
     * @throws IllegalArgumentException if the request is null
     */
    public RequestWrapper(HttpServletRequest request) {
        super(request);
        // 需要执行下request.getParameterMap()，才能正常解析form-data类型的参数数据
        this.parameterMap = request.getParameterMap();
        body = getBodyString(request);
    }

    /**
     * 获取请求Body
     *
     * @param request request
     * @return String
     */
    public String getBodyString(ServletRequest request) {
        try {
            ServletInputStream inputStream = request.getInputStream();
            return inputStream2String(inputStream);
        } catch (IOException e) {
            log.error("{}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 将inputStream里的数据读取出来并转换成字符串
     *
     * @param inputStream inputStream
     * @return String
     */
    private String inputStream2String(InputStream inputStream) {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, Charset.defaultCharset()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            log.error("{}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
        return sb.toString().trim();
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        return this.parameterMap;
    }

    /**
     * 获取输入流
     * <p>让它能重复获取到body里的内容，这样才不会影响后续的流程</p>
     *
     * @return {@link ServletInputStream}
     */
    @Override
    public ServletInputStream getInputStream() {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(body.getBytes(StandardCharsets.UTF_8));
        return new ServletInputStream() {
            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public boolean isReady() {
                return false;
            }

            @Override
            public void setReadListener(ReadListener readListener) {
            }

            @Override
            public int read() {
                return byteArrayInputStream.read();
            }
        };
    }

    /**
     * 获取字符流
     *
     * @return {@link BufferedReader}
     */
    @Override
    public BufferedReader getReader() {
        return new BufferedReader(new InputStreamReader(getInputStream()));
    }



    /**
     * 获取body
     *
     * @return {@link String}
     */
    public String getBody() {
        return this.body;
    }

}

